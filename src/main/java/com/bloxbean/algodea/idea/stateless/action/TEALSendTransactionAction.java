package com.bloxbean.algodea.idea.stateless.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.common.AlgoIcons;
import com.bloxbean.algodea.idea.common.Tuple;
import com.bloxbean.algodea.idea.compile.service.CompilationResultListener;
import com.bloxbean.algodea.idea.compile.service.CompileService;
import com.bloxbean.algodea.idea.compile.service.RemoteCompileService;
import com.bloxbean.algodea.idea.configuration.action.ConfigurationAction;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.core.action.BaseTxnAction;
import com.bloxbean.algodea.idea.core.action.util.AlgoContractModuleHelper;
import com.bloxbean.algodea.idea.language.psi.TEALFile;
import com.bloxbean.algodea.idea.nodeint.AlgoServerConfigurationHelper;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.AccountAsset;
import com.bloxbean.algodea.idea.nodeint.model.LogicSigType;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.service.LogicSigTransactionService;
import com.bloxbean.algodea.idea.stateless.model.LogicSigParams;
import com.bloxbean.algodea.idea.stateless.ui.LogicSigSigningAccountForm;
import com.bloxbean.algodea.idea.stateless.ui.TEALSendTransactionDialog;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.util.AlgoModuleUtils;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class TEALSendTransactionAction extends BaseTxnAction {
    private final static Logger LOG = Logger.getInstance(TEALSendTransactionAction.class);

    public TEALSendTransactionAction() {
        super(AlgoIcons.LOGIC_SIG_RUN_ICON);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        if (isAlgoProject(e)) {
            e.getPresentation().setEnabledAndVisible(true);
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }

    }

    @Override
    protected String getTitle() {
        return "Stateless TEAL Transaction";
    }

    @Override
    protected String getTxnCommand() {
        return "Stateless TEAL";
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (LOG.isDebugEnabled())
            LOG.debug("Logic sig transaction");

        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        if (project == null)
            return;
        final Module module = LangDataKeys.MODULE.getData(e.getDataContext());

        //Save the document
        FileDocumentManager.getInstance().saveAllDocuments();

        final AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();

        //Check deployment target is set
        final NodeInfo remoteCompilerSDK = AlgoServerConfigurationHelper.getDeploymentNodeInfo(project);
        if (remoteCompilerSDK == null) {
            IdeaUtil.showNotification(project, "Compilation configuration",
                    "Algorand Node configuration is not done for this module. Please select a deployment node.", NotificationType.ERROR, ConfigurationAction.ACTION_ID);
            return;
        }

        //Check if TEAL file
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (!(psiFile instanceof TEALFile)) {
            Messages.showErrorDialog("Not a TEAL fille", "TEAL Compilation");
            return;
        }

        Tuple<String, String> sourceOutputPaths = getFinalSourceAndOutputPath(project, module, console, psiFile);

        final String sourcePath = sourceOutputPaths._1(); //sourcePath;
        final String compiledOutputPath = sourceOutputPaths._2(); //outputFilePath;

        TEALLogicSigTxnHandler compilationResultListener = new TEALLogicSigTxnHandler(project, module, remoteCompilerSDK, console);
        compilationResultListener.compile(sourcePath, compiledOutputPath);
    }

    @NotNull
    private Tuple<String, String> getFinalSourceAndOutputPath(Project project, Module module, AlgoConsole console, PsiFile psiFile) {
        VirtualFile sourceFile = psiFile.getVirtualFile();
        String relativeSourcePath = AlgoModuleUtils.getRelativePathFromSourceRoot(project, sourceFile);

        //module output folder
        VirtualFile moduleOutFolder = AlgoContractModuleHelper.getModuleOutputTokFolder(console, module);
        if (StringUtil.isEmpty(relativeSourcePath))
            relativeSourcePath = psiFile.getVirtualFile().getName();

        File mergedSource = AlgoContractModuleHelper.generateMergeSourceWithVariables(project, module, console, moduleOutFolder, sourceFile, relativeSourcePath);

        //Compilation configuration setup
        String outputFilePath = null;
        if (moduleOutFolder != null) {
            if (!StringUtil.isEmpty(relativeSourcePath))
                outputFilePath = moduleOutFolder.getCanonicalPath() + File.separator + relativeSourcePath + ".tok";
            else
                outputFilePath = moduleOutFolder.getCanonicalPath() + File.separator + sourceFile.getName() + ".tok";
        }

        //Delete if previous compiled file exists
        VirtualFile outputVfsFile = VfsUtil.findFileByIoFile(new File(outputFilePath), true);
        if (outputVfsFile != null && outputVfsFile.exists()) {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    outputVfsFile.delete(this);
                } catch (IOException ioException) {
                    if (LOG.isDebugEnabled())
                        LOG.warn(ioException);
                }
            });
        }

        String sourcePath = sourceFile.getCanonicalPath();
        if (mergedSource != null)
            sourcePath = mergedSource.getAbsolutePath();

        return new Tuple<String, String>(sourcePath, outputFilePath);
    }

    class TEALLogicSigTxnHandler implements CompilationResultListener {
        private boolean isAlgoTransfer;
        private LogicSigParams logicSigParams;
        private Address senderAddress;
        private Address receiverAddress;
        private Tuple<BigDecimal, BigInteger> amounts;
        private Address closeReminderTo;
        private TxnDetailsParameters txnDetailsParams;
        private RequestMode requestMode;
        private AccountAsset asset;

        private Project project;
        private Module module;
        private AlgoConsole console;
        private NodeInfo remoteCompilerSDK;

        public TEALLogicSigTxnHandler(Project project, Module module, NodeInfo remoteCompilerSDK, AlgoConsole console) {
            this.project = project;
            this.module = module;
            this.remoteCompilerSDK = remoteCompilerSDK;
            this.console = console;
        }

        @Override
        public void attachProcess(OSProcessHandler handler) {
            ApplicationManager.getApplication().invokeLater(() -> {
                try {
                    console.getView().attachToProcess(handler);
                } catch (IncorrectOperationException ex) {
                    //This should not happen
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
        public void onSuccessfulCompile(String sourceFile, String outputFile, String contractHash) {
            ProgressManager.getGlobalProgressIndicator().setText("Waiting to capture input ...");
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        captureInputGenerateLogicSig(sourceFile, outputFile, contractHash, console, TEALLogicSigTxnHandler.this);
                    } catch (Exception exception) {
                        console.showErrorMessage("Error generating logic sig and sending txn", exception);
                    }
                }
            }, ModalityState.NON_MODAL);
        }

        @Override
        public void onSuccessfulLogicSig(byte[] logicSig) {
            console.showSuccessMessage("Logic sig generation was successful");
            System.out.println(logicSig);

            sendLogicSigTransaction(logicSig, isAlgoTransfer, senderAddress, receiverAddress, amounts, closeReminderTo, txnDetailsParams, requestMode, asset, module);
        }

        @Override
        public void onSuccessful(String sourceFile, String outputFile) {
//
        }

        @Override
        public void onFailure(String sourceFile, Throwable t) {
            console.showErrorMessage(String.format("Logic sig generation failed for %s", sourceFile), t);
            IdeaUtil.showNotification(project, "Create Logic Sig", "Logic Sig creation failed", NotificationType.ERROR, null);
        }

        //To generate contract address
        public void compile(String sourcePath, String outputPath) {
            Task.Backgroundable task = new Task.Backgroundable(project, "Compiling TEAL file ...") {

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    try {
                        CompileService compileService = null;
                        if (remoteCompilerSDK != null) {
                            compileService = new RemoteCompileService(project, remoteCompilerSDK);
                        }

                        compileService.compile(sourcePath, outputPath, TEALLogicSigTxnHandler.this);
                    } catch (Exception exception) {
                        console.showErrorMessage(String.format("%s failed", getTxnCommand()), exception);
                        IdeaUtil.showNotification(project, getTitle(), String.format("%s failed, Reason: %s", getTxnCommand(), exception.getMessage()), NotificationType.ERROR, null);
                    }
                }
            };

            ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));
        }

        private void captureInputGenerateLogicSig(String sourcePath, String outputPath, String contractHash, AlgoConsole console, CompilationResultListener compilationResultListener) throws DeploymentTargetNotConfigured {
            //Catpure inputs
            TEALSendTransactionDialog dialog = new TEALSendTransactionDialog(project, module, contractHash);
            dialog.enableDryRun();
            boolean ok = dialog.showAndGet();
            if (!ok) {
                return;
            }

            LogicSigSigningAccountForm accountForm = dialog.getLogicSigSignAccountForm();
            Account account = accountForm.getAccount();

            List<byte[]> args = null;
            try {
                args = dialog.getArgsInputForm().getArgsAsBytes();
            } catch (Exception exception) {
                console.showErrorMessage(String.format("Compilation failed for %s", sourcePath));
                IdeaUtil.showNotification(project, "Create Logic Sig", "Exception getting args value.", NotificationType.ERROR, null);
                return;
            }

            logicSigParams = new LogicSigParams();
            logicSigParams.addSigningAccount(account); //Single account
            if (args != null) {
                logicSigParams.setArgs(args);
            }

            if (accountForm.isContractAccountType()) {
                logicSigParams.setType(LogicSigType.CONTRACT_ACCOUNT);
            } else {
                logicSigParams.setType(LogicSigType.DELEGATION_ACCOUNT);
                logicSigParams.setAccountDelegationType(true);
            }

            if (logicSigParams.isAccountDelegationType()) {
                if(logicSigParams.getSigningAccounts().size() == 0 ||
                        logicSigParams.getSigningAccounts().get(0) == null) {
                    console.showErrorMessage("Invalid signing account");
                    return;
                }
                senderAddress = logicSigParams.getSigningAccounts().get(0).getAddress();
            }

            receiverAddress = dialog.getReceiverAddress();
            amounts = dialog.getAmount(); //Algo, mAlgo

            try {
                closeReminderTo = dialog.getCloseReminderTo();
            } catch (Exception ex) {
            }

            try {
                txnDetailsParams = dialog.getTransactionDtlsEntryForm().getTxnDetailsParameters();
            } catch (Exception exception) {
                console.showErrorMessage("Invalid transaction details input parameters : " + exception.getMessage());
                IdeaUtil.showNotification(project, "Logic Sig transaction",
                        "Invalid transaction details input parameters : " + exception.getMessage(), NotificationType.WARNING, null);
                return;
            }

            asset = dialog.getAsset();
            isAlgoTransfer = dialog.isAlgoTransfer();
            requestMode = dialog.getRequestMode();

            generateLogicSig(project, console, remoteCompilerSDK, sourcePath, outputPath, logicSigParams, compilationResultListener);
        }

        private void generateLogicSig(Project project, AlgoConsole console, NodeInfo remoteCompilerSDK, String finalSourcePath, String finalOutputFilePath, LogicSigParams logicSigParams, CompilationResultListener compilationResultListener) {
            Task.Backgroundable task = new Task.Backgroundable(project, "Generating Logic-sig ...") {

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    try {
                        CompileService compileService = null;
                        compileService = new RemoteCompileService(project, remoteCompilerSDK);

                        compileService.generateLogicSig(finalSourcePath, finalOutputFilePath, null, logicSigParams, compilationResultListener);
                    } catch (Exception exception) {
                        console.showErrorMessage(String.format("%s failed", getTxnCommand()), exception);
                        IdeaUtil.showNotification(project, getTitle(), String.format("%s failed, Reason: %s", getTxnCommand(), exception.getMessage()), NotificationType.ERROR, null);
                    }
                }
            };

            ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));
        }

        private void sendLogicSigTransaction(byte[] logicSig, boolean isAlgoTransfer, Address finalSenderAccount, Address receiverAddress, Tuple<BigDecimal, BigInteger> amounts, Address finalCloseReminderTo, TxnDetailsParameters finalTxnDetailsParams, RequestMode requestMode, AccountAsset asset, Module module) {
            ProgressManager.getInstance().getProgressIndicator().setText("Sending Logic-sig transaction...");
            try {
                LogListener logListener = new LogListenerAdapter(console);
                LogicSigTransactionService transactionService = new LogicSigTransactionService(project, logListener);

                console.showInfoMessage(String.format("Starting %s ...\n", getTxnCommand()));
                try {
                    Result result = null;
                    if (isAlgoTransfer) {
                        result = transactionService.logicSigTransaction(logicSig, finalSenderAccount, receiverAddress, null, amounts._2(), finalCloseReminderTo, finalTxnDetailsParams, requestMode);
                    } else {
                        result = transactionService.logicSigTransaction(logicSig, finalSenderAccount, receiverAddress, asset, amounts._2(), finalCloseReminderTo, finalTxnDetailsParams, requestMode);
                    }

                    processResult(project, module, result, requestMode, logListener);
                } catch (Exception exception) {
                    console.showErrorMessage(String.format("%s failed", getTxnCommand()), exception);
                    IdeaUtil.showNotification(project, getTitle(), String.format("%s failed, Reason: %s", getTxnCommand(), exception.getMessage()), NotificationType.ERROR, null);
                }
            } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
                warnDeploymentTargetNotConfigured(project, getTitle());
            } catch (Exception ex) {
                if (LOG.isDebugEnabled()) {
                    LOG.error(ex);
                }
                console.showErrorMessage(ex.getMessage(), ex);
                IdeaUtil.showNotification(project, getTitle(), String.format("Logic sig transaction failed, reason: %s", ex.getMessage()), NotificationType.ERROR, null);
            }
        }
    }

}
