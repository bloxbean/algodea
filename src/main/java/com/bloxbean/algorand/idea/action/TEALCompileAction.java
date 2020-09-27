package com.bloxbean.algorand.idea.action;

import com.bloxbean.algorand.idea.language.psi.TEALFile;
import com.bloxbean.algorand.idea.module.sdk.AlgoSdkType;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TEALCompileAction extends AnAction {
    private final static Logger LOG = Logger.getInstance(TEALCompileAction.class);

    private static ConsoleView view = null;
    private static ToolWindow window = null;

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        PsiFile file =  e.getDataContext().getData(CommonDataKeys.PSI_FILE);

        if(file != null && file instanceof TEALFile) {
            e.getPresentation().setEnabled(true);
        } else {
            e.getPresentation().setEnabled(false);
        }
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if(LOG.isDebugEnabled())
            LOG.debug("Compile TEAL file");

        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        if (project == null)
            return;

        Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();

        if(sdk == null || !AlgoSdkType.getInstance().equals(sdk.getSdkType())) {
            Messages.showErrorDialog("Algorand SDK is not set for this project.", "TEAL Compilation");
            return;
        }

        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if(!(psiFile instanceof TEALFile)) {
            Messages.showErrorDialog("Not a TEAL fille", "TEAL Compilation");
            return;
        }

        String cwd = project.getBaseDir().getPath();
        List<String> cmd = new ArrayList<>();
        cmd.add(sdk.getHomePath() + File.separator + "bin" + File.separator + "goal");
        cmd.add("clerk");
        cmd.add("compile");
        cmd.add(psiFile.getVirtualFile().getCanonicalPath());

        OSProcessHandler handler;
        try {
            handler = new OSProcessHandler(
                    new GeneralCommandLine(cmd).withWorkDirectory(cwd)
            );
        } catch(ExecutionException ex) {
            ex.printStackTrace();
            return;
        }

        if (view == null) {
            TextConsoleBuilderFactory factory = TextConsoleBuilderFactory.getInstance();
            TextConsoleBuilder builder = factory.createBuilder(project);
            view = builder.getConsole();
        }

        view.attachToProcess(handler);
        handler.startNotify();

        if (window == null) {
            ToolWindowManager manager = ToolWindowManager.getInstance(project);
            window = manager.registerToolWindow("Algorand", true, ToolWindowAnchor.BOTTOM);
            final ContentManager contentManager = window.getContentManager();

            Content content = contentManager
                    .getFactory()
                    .createContent(view.getComponent(), "", false);
            contentManager.addContent(content);
            window.show(() -> {});
        }
    }
}
