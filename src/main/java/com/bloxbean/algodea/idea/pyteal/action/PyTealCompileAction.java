package com.bloxbean.algodea.idea.pyteal.action;

import com.bloxbean.algodea.idea.compile.action.TEALCompileAction;
import com.bloxbean.algodea.idea.compile.service.*;
import com.bloxbean.algodea.idea.configuration.action.ConfigurationAction;
import com.bloxbean.algodea.idea.configuration.model.AlgoLocalSDK;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.core.action.AlgoBaseAction;
import com.bloxbean.algodea.idea.core.action.util.AlgoContractModuleHelper;
import com.bloxbean.algodea.idea.core.exception.LocalSDKNotConfigured;
import com.bloxbean.algodea.idea.nodeint.AlgoServerConfigurationHelper;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.util.AlgoModuleUtils;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.twelvemonkeys.lang.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class PyTealCompileAction extends AlgoBaseAction {

    private final static Logger LOG = Logger.getInstance(TEALCompileAction.class);

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        PsiFile file = e.getDataContext().getData(CommonDataKeys.PSI_FILE);

        if (file != null && file.getName().endsWith(".py")) {
            e.getPresentation().setEnabled(true);
        } else {
            e.getPresentation().setVisible(false);
            e.getPresentation().setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        if(project == null)
            return;

        final Module module = LangDataKeys.MODULE.getData(e.getDataContext());
        if (module == null)
            return;

        Sdk projectSDK = ProjectRootManager.getInstance(project).getProjectSdk();
        if(projectSDK == null) {
            //TODO warning add/set python sdk
            return;
        }

        if(projectSDK.getSdkType().getName().startsWith("Python")) {
            System.out.println(projectSDK.getSdkType().getName());
            System.out.println("It's a python sdk");
        } else {
            //warning
            return;
        }

        if (LOG.isDebugEnabled())
            LOG.debug("Compile TEAL file");

        FileDocumentManager.getInstance().saveAllDocuments();

        final AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();

        AlgoLocalSDK localSDK = AlgoServerConfigurationHelper.getCompilerLocalSDK(project);
        NodeInfo remoteSDK = null;
        if (localSDK == null) {
            remoteSDK = AlgoServerConfigurationHelper.getCompilerNodeInfo(project);
        }

        if (localSDK == null && remoteSDK == null) {
            IdeaUtil.showNotification(project, "Compilation configuration",
                    "Algorand Local SDK or Node configuration is not done for this module.", NotificationType.ERROR, ConfigurationAction.ACTION_ID);
            return;
        }
        final NodeInfo remoteCompilerSDK = remoteSDK;

        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        VirtualFile sourceFile = psiFile.getVirtualFile();

        String relativeSourcePath = AlgoModuleUtils.getRelativePathFromSourceRoot(project, sourceFile);


        //module output folder
        VirtualFile moduleOutFolder = AlgoContractModuleHelper.getModuleOutputFolder(console, module);

        if(StringUtil.isEmpty(relativeSourcePath))
            relativeSourcePath = psiFile.getVirtualFile().getName();

        VirtualFile generatedSrcFolder = AlgoContractModuleHelper.getGeneratedSourceFolder(moduleOutFolder);
        if(generatedSrcFolder == null) {
            //TODO error return
            return;
        }

        relativeSourcePath = convertExtensionPyToTEAL(relativeSourcePath);

        String generatedSourcePath = generatedSrcFolder.getCanonicalPath() + File.separator + relativeSourcePath;

        //Compilation configuration setup
        String outputFilePath = null;
        if (moduleOutFolder != null) {
            if(!StringUtil.isEmpty(relativeSourcePath))
                outputFilePath = moduleOutFolder.getCanonicalPath() + File.separator + relativeSourcePath + ".tok";
            else
                outputFilePath = moduleOutFolder.getCanonicalPath() + File.separator
                        + convertExtensionPyToTEAL(sourceFile.getName()) + ".tok";
        }

        //Delete previous generated file if any
        deleteFile(project, generatedSourcePath);
        //Delete if previous compiled file exists
        deleteFile(project, outputFilePath);

        String sourcePath = sourceFile.getCanonicalPath();

        final VirtualFile folderToRefresh = moduleOutFolder;
        final VirtualFile moduleOutputFolderToRefresh = moduleOutFolder;

        final String finalOutputFilePath = outputFilePath;
        final VirtualFile finalGeneratedSrcFolder = generatedSrcFolder;

        CompilationResultListener pyTealCompileListener = new CompilationResultListener() {
            @Override
            public void attachProcess(OSProcessHandler handler) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    try {
                        console.getView().attachToProcess(handler);
                    } catch (IncorrectOperationException ex) {
                        //This should not happen
                        //ex.printStackTrace();
                        console.showInfoMessage(ex.getMessage());
                        console.dispose();
                        console.getView().attachToProcess(handler);
                    }
                    handler.startNotify();
                });
            }

            @Override
            public void error(String message) {
                console.showErrorMessage(message);
            }

            @Override
            public void error(String message, Throwable t) {
                console.showErrorMessage(message, t);
            }

            @Override
            public void info(String message) {
                console.showInfoMessage(message);
            }

            @Override
            public void warn(String msg) {
                console.showWarningMessage(msg);
            }

            @Override
            public void onSuccessful(String sourceFile, String outputFile) {
                console.showSuccessMessage("PyTeal file compiled successfully.");
                console.showInfoMessage("TEAL file generated at " + outputFile);

                if (folderToRefresh != null) {
                    folderToRefresh.refresh(false, false);
                }

                if(finalGeneratedSrcFolder != null) {
                    finalGeneratedSrcFolder.refresh(false, true);
                }
            }

            @Override
            public void onFailure(String sourceFile, Throwable t) {
                console.showErrorMessage(String.format("PyTeal compilation failed for %s", sourceFile), t);
                IdeaUtil.showNotification(project, "PyTeal Compile", "PyTeal Compilation failed", NotificationType.ERROR, null);
            }
        };

        CompilationResultListener compilationResultListener = new CompilationResultListener() {
            @Override
            public void attachProcess(OSProcessHandler handler) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    try {
                        console.getView().attachToProcess(handler);
                    } catch (IncorrectOperationException ex) {
                        //This should not happen
                        //ex.printStackTrace();
                        console.showInfoMessage(ex.getMessage());
                        console.dispose();
                        console.getView().attachToProcess(handler);
                    }
                    handler.startNotify();
                });
            }

            @Override
            public void error(String message) {
                console.showErrorMessage(message);
            }

            @Override
            public void error(String message, Throwable t) {
                console.showErrorMessage(message, t);
            }

            @Override
            public void info(String message) {
                console.showInfoMessage(message);
            }

            @Override
            public void warn(String msg) {
                console.showWarningMessage(msg);
            }

            @Override
            public void onSuccessful(String sourceFile, String outputFile) {
                console.showSuccessMessage("TEAL file compiled successfully");

                if (folderToRefresh != null) {
                    folderToRefresh.refresh(false, false);
                }

                if (moduleOutputFolderToRefresh != null) {
                    moduleOutputFolderToRefresh.refresh(false, true);
                }

                VirtualFile outputVf = VfsUtil.findFileByIoFile(new File(outputFile), true);
                if(outputVf != null && outputVf.exists())
                    outputVf.refresh(true, true);

                IdeaUtil.showNotification(project, "TEAL Compile", "Compilation was successful", NotificationType.INFORMATION, null);
            }

            @Override
            public void onFailure(String sourceFile, Throwable t) {
                console.showErrorMessage(String.format("Compilation failed for %s", sourceFile), t);
                IdeaUtil.showNotification(project, "TEAL Compile", "Compilation failed", NotificationType.ERROR, null);
            }
        };

        Task.Backgroundable task = new Task.Backgroundable(project, "TEAL Compile") {

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                //Py compile
                PyTealCompileService pyTealCompileService = new PyTealCompileService(project);
                String content = pyTealCompileService.compile(projectSDK.getHomePath(), sourcePath, generatedSourcePath,
                        pyTealCompileListener);

                if(content == null || content.length() == 0) {
                    pyTealCompileListener.error("PyTeal compilation failed. " + sourcePath);
                    return;
                }

                try {
                    FileUtil.writeToFile(new File(generatedSourcePath), content);

                    //Refresh
                    VirtualFile outputVf = VfsUtil.findFileByIoFile(new File(generatedSourcePath), true);
                    if(outputVf != null && outputVf.exists())
                        outputVf.refresh(true, true);
                } catch (IOException ioException) {
                    compilationResultListener.error("error writing teal file : " + ioException.getMessage());
                }

                CompileService compileService = null;
                if (localSDK != null) {
                    try {
                        compileService = new GoalCompileService(project);
                    } catch (LocalSDKNotConfigured localSDKNotConfigured) {
                        Messages.showErrorDialog("Algorand Local SDK is not set for this module.", "TEAL Compilation");
                        return;
                    }
                } else if (remoteCompilerSDK != null) {
                    compileService = new RemoteCompileService(project, remoteCompilerSDK);
                }

                console.showInfoMessage("Start compilation ..");
                compileService.compile(generatedSourcePath, finalOutputFilePath, compilationResultListener);
            }
        };

        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));
    }

    private void deleteFile(Project project, String outputFilePath) {
        VirtualFile outputVfsFile = VfsUtil.findFileByIoFile(new File(outputFilePath), true);
        if(outputVfsFile != null && outputVfsFile.exists()) {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    outputVfsFile.delete(this);
                } catch (IOException ioException) {
                    if(LOG.isDebugEnabled()) {
                        LOG.warn(ioException);
                    }
                }
            });
        }
    }

    @NotNull
    private String convertExtensionPyToTEAL(String filePath) {
        if(filePath == null)
            return null;
        if(filePath.endsWith(".py") || filePath.endsWith(".PY")) {
            filePath = filePath.substring(0, filePath.lastIndexOf(".")) + ".teal";
        }
        return filePath;
    }
}
