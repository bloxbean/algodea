package com.bloxbean.algodea.idea.stateless.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.MultisigAddress;
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
import com.bloxbean.algodea.idea.stateless.model.LogicSigParams;
import com.bloxbean.algodea.idea.stateless.ui.ArgsInputForm;
import com.bloxbean.algodea.idea.stateless.ui.MultiSigLogicSigCreateInputForm;
import com.bloxbean.algodea.idea.stateless.ui.MultiSigLogicSigDialog;
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
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class MultisigLogicSigGenerateAction extends AnAction {

    private final static Logger LOG = Logger.getInstance(MultisigLogicSigGenerateAction.class);

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
        VirtualFile lsigOutFolder = AlgoContractModuleHelper.getModuleLSigOutputFolder(console, module);

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

        String lsigOutputFilePath = null;
        if(lsigOutFolder != null) {
            if(!StringUtil.isEmpty(relativeSourcePath))
                lsigOutputFilePath = lsigOutFolder.getCanonicalPath() + File.separator + relativeSourcePath;
            else
                lsigOutputFilePath = lsigOutFolder.getCanonicalPath() + File.separator + sourceFile.getName();
        }

        //Replace .teal with .lsig
        if(!StringUtil.isEmpty(lsigOutputFilePath)) {
            int index = lsigOutputFilePath.lastIndexOf(".");
            if(index != -1) {
                lsigOutputFilePath = lsigOutputFilePath.substring(0, index);
                lsigOutputFilePath += ".lsig";
            }
        }

        //Delete if previous compiled file exists
        VirtualFile outputVfsFile = VfsUtil.findFileByIoFile(new File(outputFilePath), true);
        if(outputVfsFile != null && outputVfsFile.exists()) {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    outputVfsFile.delete(this);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
        }

        String sourcePath = sourceFile.getCanonicalPath();
        if (mergedSource != null)
            sourcePath = mergedSource.getAbsolutePath();


        final VirtualFile folderToRefresh = moduleOutFolder;
        final VirtualFile moduleOutputFolderToRefresh = moduleOutFolder;
        final VirtualFile lsigOutputFolderToRefresh = lsigOutFolder;

        final String finalSourcePath = sourcePath;
        final String finalOutputFilePath = outputFilePath;
        final String finalLogicSigFilePath = lsigOutputFilePath;

        CompilationResultListener compilationResultListener = new CompilationResultListener() {
            @Override
            public void attachProcess(OSProcessHandler handler) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    try {
                        console.getView().attachToProcess(handler);
                    } catch (IncorrectOperationException ex) {
                        //This should not happen
                        ex.printStackTrace();
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
                console.showSuccessMessage("Logic sig generation was successful");

                if (folderToRefresh != null) {
                    folderToRefresh.refresh(false, false);
                }

                if (moduleOutputFolderToRefresh != null) {
                    moduleOutputFolderToRefresh.refresh(false, true);
                }

                if(lsigOutputFolderToRefresh != null) {
                    lsigOutputFolderToRefresh.refresh(false, true);
                }

                VirtualFile outputVf = VfsUtil.findFileByIoFile(new File(outputFile), true);
                if(outputVf != null && outputVf.exists())
                    outputVf.refresh(true, true);

                IdeaUtil.showNotification(project, "Create Logic Sig", "Logic Sig created successfully", NotificationType.INFORMATION, null);

            }

            @Override
            public void onFailure(String sourceFile) {
                console.showErrorMessage(String.format("Compilation failed for %s", sourceFile));
                IdeaUtil.showNotification(project, "Create Logic Sig", "Logic Sig creation failed", NotificationType.ERROR, null);
            }
        };

        MultiSigLogicSigDialog dialog = new MultiSigLogicSigDialog(project);

        boolean ok = dialog.showAndGet();
        if(!ok) {
            return;
        }

        MultiSigLogicSigCreateInputForm mslsInputForm = dialog.getMultiSigLSigInputForm();

        MultisigAddress multisigAddress = null;
        List<Account> accounts = null;
        try {
            multisigAddress = mslsInputForm.getMultisigAddress();
            accounts = mslsInputForm.getAccounts();
        } catch (NoSuchAlgorithmException ex) {
            IdeaUtil.showNotification(project, "Create Logic Sig", "Error getting MultisigAddress : " + ex.getMessage(), NotificationType.ERROR, null);
            return;
        } catch (GeneralSecurityException generalSecurityException) {
            IdeaUtil.showNotification(project, "Create Logic Sig", "Error getting account info", NotificationType.ERROR, null);
            return;
        }

        if(multisigAddress == null) {
            IdeaUtil.showNotification(project, "Create Logic Sig", "Error getting MultisigAddress", NotificationType.ERROR, null);
            console.showErrorMessage("Error generating Logic sig. MultisigAddress is null");
            return;
        }

        List<byte[]> args = null;
        try {
            args = dialog.getArgsInputForm().getArgsAsBytes();
        } catch (Exception exception) {

            console.showErrorMessage(String.format("Compilation failed for %s", sourceFile));
            IdeaUtil.showNotification(project, "Create Logic Sig", "Exception getting args value.", NotificationType.ERROR, null);
            return;
        }

        LogicSigParams logicSigParams = new LogicSigParams();
        logicSigParams.setSigningAccounts(accounts);
        logicSigParams.setMultisigAddress(multisigAddress);
        if(args != null) {
            logicSigParams.setArgs(args);
        }

        Task.Backgroundable task = new Task.Backgroundable(project, "TEAL Compile and Logic Sig") {

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                CompileService compileService = null;
                if (localSDK != null) {
                    try {
                        compileService = new GoalCompileService(project);
                    } catch (LocalSDKNotConfigured localSDKNotConfigured) {
                        Messages.showErrorDialog("Algorand Local SDK is not set for this module.", "TEAL Compilation and Logic Sig");
                        return;
                    }
                } else if (remoteCompilerSDK != null) {
                    compileService = new RemoteCompileService(project, remoteCompilerSDK);
                }

                compileService.lsig(finalSourcePath, finalOutputFilePath, finalLogicSigFilePath, logicSigParams, compilationResultListener);
            }
        };

        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));
    }
}
