package com.bloxbean.algorand.idea.action;

import com.bloxbean.algorand.idea.action.ui.CompileVarTmplInputDialog;
import com.bloxbean.algorand.idea.action.ui.VarParam;
import com.bloxbean.algorand.idea.action.util.VarTmplUtil;
import com.bloxbean.algorand.idea.language.psi.TEALFile;
import com.bloxbean.algorand.idea.module.sdk.AlgoSdkType;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.bloxbean.algorand.idea.module.toolwindow.AlgoToolWindowFactory.ALGO_WINDOW_ID;

public class TEALCompileAction extends AnAction {
    private final static Logger LOG = Logger.getInstance(TEALCompileAction.class);
    public static final String BUILD_FOLDER = "out";
    public static final String GENERATED_SRC = "generated-src";

    private static ConsoleView view = null;

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        PsiFile file = e.getDataContext().getData(CommonDataKeys.PSI_FILE);

        if (file != null && file instanceof TEALFile) {
            e.getPresentation().setEnabled(true);
        } else {
            e.getPresentation().setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (LOG.isDebugEnabled())
            LOG.debug("Compile TEAL file");

        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        if (project == null)
            return;

        final Module module = LangDataKeys.MODULE.getData(e.getDataContext());
        if(module == null)
            return;

        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        Sdk sdk = moduleRootManager.getSdk();

        if (sdk == null || !AlgoSdkType.getInstance().equals(sdk.getSdkType())) {
            Messages.showErrorDialog("Algorand SDK is not set for this module.", "TEAL Compilation");
            return;
        }

        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (!(psiFile instanceof TEALFile)) {
            Messages.showErrorDialog("Not a TEAL fille", "TEAL Compilation");
            return;
        }

        if (view == null) {
            TextConsoleBuilderFactory factory = TextConsoleBuilderFactory.getInstance();
            TextConsoleBuilder builder = factory.createBuilder(project);
            view = builder.getConsole();
        }

        ToolWindowManager manager = ToolWindowManager.getInstance(project);
        ToolWindow window = manager.getToolWindow(ALGO_WINDOW_ID);

        if (window != null) {
            final ContentManager contentManager = window.getContentManager();

            Content content = window.getContentManager().getContent(view.getComponent());

            if(content == null) {
                content = contentManager
                        .getFactory()
                        .createContent(view.getComponent(), "TEAL Compile", false);
                content.setCloseable(true);
                contentManager.addContent(content);
                window.show(() -> {
                });
            }

            window.show();
        }

        view.clear();

        String cwd = project.getBasePath();

        final String outputFileName = psiFile.getVirtualFile().getName() + ".tok";

        VirtualFile outFolder = null;
        VirtualFile moduleOutFolder = null;
        VirtualFile moduleRoot = module.getModuleFile().getParent();
        VirtualFile sourceFile = psiFile.getVirtualFile();
        File mergedSource = null;
        try {
            outFolder = moduleRoot.findChild(BUILD_FOLDER);
            if(outFolder == null || !outFolder.exists())
                outFolder = moduleRoot.createChildDirectory(this, BUILD_FOLDER);

            if(outFolder != null && outFolder.exists()) {
                 moduleOutFolder = outFolder.findChild(module.getName());

                 if(moduleOutFolder == null || !moduleOutFolder.exists())
                     moduleOutFolder = outFolder.createChildDirectory(this, module.getName());

                if(moduleOutFolder == null || !moduleOutFolder.exists()) {
                    view.print("Error creating module out folder for the output in " + outFolder.getCanonicalPath(), ConsoleViewContentType.LOG_ERROR_OUTPUT);
                    return;
                } else {
                    VirtualFile outputFile = moduleOutFolder.findChild(outputFileName);
                    if(outputFile != null && outputFile.exists()) {
                        outputFile.delete(this);
                    }
                }
            } else {
                view.print("Unable to create out folder in " + moduleRoot.getCanonicalPath() , ConsoleViewContentType.LOG_ERROR_OUTPUT);
                return;
            }
        } catch (IOException io) {
            LOG.error(io);
            view.print("Unable to create out folder " + io.getMessage(), ConsoleViewContentType.ERROR_OUTPUT);
        }

        //Get list of VAR_TMPL_* if available in the source file
        List<VarParam> varParams = null;
        try {
            varParams = VarTmplUtil.getListOfVarTmplInTEALFile(psiFile.getVirtualFile());
        } catch (IOException ioException) {
            ioException.printStackTrace();
            view.print("Unable to read teal file to get VAR_TMPL_ variables", ConsoleViewContentType.ERROR_OUTPUT);
            return;
        }

        //If VAR_TMPL_* found
        if(varParams != null && varParams.size() > 0) {
            CompileVarTmplInputDialog compileVarTmplInputDialog =  new CompileVarTmplInputDialog(psiFile.getName(), varParams);
            boolean result = compileVarTmplInputDialog.showAndGet();
            if(!result) {
                view.print("Compilation process was cancelled", ConsoleViewContentType.LOG_WARNING_OUTPUT);
                return;
            }

            List<VarParam> varParamsValues = compileVarTmplInputDialog.getParamsWithValues();

            VirtualFile genSrcFolder = createGeneratedSourceFolder(moduleOutFolder);
            if(genSrcFolder == null) {
                view.print("Compilation failed. 'generated-src' folder could not be created", ConsoleViewContentType.ERROR_OUTPUT);
                return;
            }

            //Create merged source file inside out/<module>/generated_src folder
            try {
                mergedSource = VarTmplUtil.createMergeSourceFile(this, sourceFile, genSrcFolder, varParamsValues);
            } catch (IOException ioException) {
                LOG.error("Error merging VAR_TMPL_* values with the source", ioException);
                view.print("Compilation failed. VAR_TMPL_ values could not be merged", ConsoleViewContentType.ERROR_OUTPUT);
                return;
            }
        }

        //Compilation configuration setup
        String outputFilePath = null;
        if(moduleOutFolder != null) {
            outputFilePath = moduleOutFolder.getCanonicalPath() + File.separator + outputFileName;
        }

        List<String> cmd = new ArrayList<>();
        cmd.add(sdk.getHomePath() + File.separator + "bin" + File.separator + "goal");
        cmd.add("clerk");
        cmd.add("compile");

        if(mergedSource != null)
            cmd.add(mergedSource.getAbsolutePath());
        else
            cmd.add(sourceFile.getCanonicalPath());

        if(outputFilePath != null) {
            cmd.add("-o");
            cmd.add(outputFilePath);
        }

        OSProcessHandler handler;
        try {
            handler = new OSProcessHandler(
                    new GeneralCommandLine(cmd).withWorkDirectory(cwd)
            );
        } catch (ExecutionException ex) {
            ex.printStackTrace();
            return;
        }

        view.attachToProcess(handler);
        handler.startNotify();

        final VirtualFile folderToRefresh = outFolder;
        final VirtualFile moduleOutputFolderToRefresh = moduleOutFolder;
        handler.addProcessListener(new ProcessListener() {
            @Override
            public void startNotified(@NotNull ProcessEvent event) {
                view.print("TEAL Compilation started...", ConsoleViewContentType.LOG_INFO_OUTPUT);
            }

            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                if(event.getExitCode() == 0) {
                    view.print("Compilation successful.", ConsoleViewContentType.LOG_INFO_OUTPUT);
                } else {
                    view.print("Compilation failed.", ConsoleViewContentType.LOG_ERROR_OUTPUT);
                }

                if(folderToRefresh != null) {
                    folderToRefresh.refresh(false, false);
                }

                if(moduleOutputFolderToRefresh != null) {
                    moduleOutputFolderToRefresh.refresh(false, true);
                    VirtualFile outputVFile = moduleOutputFolderToRefresh.findChild(outputFileName);
                    if(outputVFile != null)
                        outputVFile.refresh(true, true);
                }

            }

            @Override
            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {

            }
        });
    }

    private VirtualFile createGeneratedSourceFolder(VirtualFile moduleOutFolder) {
        VirtualFile genFolder = moduleOutFolder.findChild(GENERATED_SRC);
        if(genFolder == null || !genFolder.exists()) {
            try {
                genFolder = moduleOutFolder.createChildDirectory(this, GENERATED_SRC);
            } catch (IOException e) {
                LOG.error("Unable to create generated_src folder", e);
                e.printStackTrace();
                return null;
            }
        }
        return  genFolder;
    }
}
