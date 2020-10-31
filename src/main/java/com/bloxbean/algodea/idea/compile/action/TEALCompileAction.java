/*
 * Copyright (c) 2020 BloxBean Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bloxbean.algodea.idea.compile.action;

import com.bloxbean.algodea.idea.compile.service.CompilationResultListener;
import com.bloxbean.algodea.idea.compile.service.CompileService;
import com.bloxbean.algodea.idea.compile.service.GoalCompileService;
import com.bloxbean.algodea.idea.compile.service.RemoteCompileService;
import com.bloxbean.algodea.idea.configuration.action.ConfigurationAction;
import com.bloxbean.algodea.idea.configuration.model.AlgoLocalSDK;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.core.action.util.AlgoContractModuleHelper;
import com.bloxbean.algodea.idea.core.exception.LocalSDKNotConfigured;
import com.bloxbean.algodea.idea.language.psi.TEALFile;
import com.bloxbean.algodea.idea.nodeint.AlgoServerConfigurationHelper;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.util.AlgoModuleUtils;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
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
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.twelvemonkeys.lang.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class TEALCompileAction extends AnAction {
    private final static Logger LOG = Logger.getInstance(TEALCompileAction.class);

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        PsiFile file = e.getDataContext().getData(CommonDataKeys.PSI_FILE);

        if (file != null && file instanceof TEALFile) {
            e.getPresentation().setEnabled(true);
        } else {
            e.getPresentation().setVisible(false);
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
        if (module == null)
            return;

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
        if (!(psiFile instanceof TEALFile)) {
            Messages.showErrorDialog("Not a TEAL fille", "TEAL Compilation");
            return;
        }


        String cwd = project.getBasePath();

        //final String outputFileName = psiFile.getVirtualFile().getName() + ".tok";

        VirtualFile sourceFile = psiFile.getVirtualFile();

        String relativeSourcePath = AlgoModuleUtils.getRelativePathFromSourceRoot(project, sourceFile);


        //module output folder
        VirtualFile moduleOutFolder = AlgoContractModuleHelper.getModuleOutputFolder(console, module);

        if(StringUtil.isEmpty(relativeSourcePath))
            relativeSourcePath = psiFile.getVirtualFile().getName();


        File mergedSource = AlgoContractModuleHelper.generateMergeSourceWithVariables(project, console, moduleOutFolder, sourceFile, relativeSourcePath);


        //Compilation configuration setup
        String outputFilePath = null;
        if (moduleOutFolder != null) {
            if(!StringUtil.isEmpty(relativeSourcePath))
                outputFilePath = moduleOutFolder.getCanonicalPath() + File.separator + relativeSourcePath + ".tok";
            else
                outputFilePath = moduleOutFolder.getCanonicalPath() + File.separator + sourceFile.getName() + ".tok";
        }

        //Delete if previous compiled file exists
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

        String sourcePath = sourceFile.getCanonicalPath();
        if (mergedSource != null)
            sourcePath = mergedSource.getAbsolutePath();


        final VirtualFile folderToRefresh = moduleOutFolder;
        final VirtualFile moduleOutputFolderToRefresh = moduleOutFolder;

        final String finalSourcePath = sourcePath;
        final String finalOutputFilePath = outputFilePath;

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
                compileService.compile(finalSourcePath, finalOutputFilePath, compilationResultListener);
            }
        };

        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));

    }
}
