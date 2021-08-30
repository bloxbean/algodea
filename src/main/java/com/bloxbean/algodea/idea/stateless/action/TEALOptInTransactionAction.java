package com.bloxbean.algodea.idea.stateless.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.bloxbean.algodea.idea.common.AlgoIcons;
import com.bloxbean.algodea.idea.common.Tuple;
import com.bloxbean.algodea.idea.compile.service.CompilationResultListener;
import com.bloxbean.algodea.idea.compile.service.CompileService;
import com.bloxbean.algodea.idea.compile.service.RemoteCompileService;
import com.bloxbean.algodea.idea.configuration.action.ConfigurationAction;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.core.action.BaseTxnAction;
import com.bloxbean.algodea.idea.core.action.util.AlgoContractModuleHelper;
import com.bloxbean.algodea.idea.debugger.service.DebugHandler;
import com.bloxbean.algodea.idea.language.psi.TEALFile;
import com.bloxbean.algodea.idea.nodeint.AlgoServerConfigurationHelper;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.AssetTxnParameters;
import com.bloxbean.algodea.idea.nodeint.model.LogicSigType;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.service.LogicSigTransactionService;
import com.bloxbean.algodea.idea.stateless.model.LogicSigParams;
import com.bloxbean.algodea.idea.stateless.ui.LogicSigSigningAccountForm;
import com.bloxbean.algodea.idea.stateless.ui.TEALOptInAssetDialog;
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
import java.util.List;

public class TEALOptInTransactionAction extends BaseTxnAction {
    private final static Logger LOG = Logger.getInstance(TEALOptInTransactionAction.class);

    public TEALOptInTransactionAction() {
        super(AlgoIcons.LOGIC_SIG_RUN_ICON);
    }

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
    protected String getTitle() {
        return "Stateless TEAL OptIn Transaction";
    }

    @Override
    protected String getTxnCommand() {
        return "Stateless TEAL - OptIn";
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (LOG.isDebugEnabled())
            LOG.debug("OptIn Logic sig transaction");

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

        TEALOptInLogicSigTxnHandler compilationResultListener = new TEALOptInLogicSigTxnHandler(project, module, remoteCompilerSDK, console, sourcePath);
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

    class TEALOptInLogicSigTxnHandler implements CompilationResultListener {
        private LogicSigParams logicSigParams;
        private Address senderAddress;
        private AssetTxnParameters assetTxnParameters;
        private TxnDetailsParameters txnDetailsParams;
        private RequestMode requestMode;

        private Project project;
        private Module module;
        private AlgoConsole console;
        private NodeInfo remoteCompilerSDK;
        private String sourcePath;

        public TEALOptInLogicSigTxnHandler(Project project, Module module, NodeInfo remoteCompilerSDK, AlgoConsole console, String sourcePath) {
            this.project = project;
            this.module = module;
            this.remoteCompilerSDK = remoteCompilerSDK;
            this.console = console;
            this.sourcePath = sourcePath;
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
                        captureInputGenerateLogicSig(sourceFile, outputFile, contractHash, console, TEALOptInLogicSigTxnHandler.this);
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

            sendOptInLogicSigTransaction(logicSig);
        }

        @Override
        public void onSuccessful(String sourceFile, String outputFile) {
//
        }

        @Override
        public void onFailure(String sourceFile, Throwable t) {
            console.showErrorMessage(String.format("Logic sig OptIn generation failed for %s", sourceFile), t);
            IdeaUtil.showNotification(project, "Create Logic Sig", "Logic Sig creation failed", NotificationType.ERROR, null);
        }

        //To generate contract address
        public void compile(String sourcePath, String outputPath) {
            Task.Backgroundable task = new Task.Backgroundable(project, "Compiling TEAL file ...") {

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    try {
                        console.showInfoMessage("TEAL compilation starting. Please wait...");
                        CompileService compileService = null;
                        if (remoteCompilerSDK != null) {
                            compileService = new RemoteCompileService(project, remoteCompilerSDK);
                        }

                        compileService.compile(sourcePath, outputPath, TEALOptInLogicSigTxnHandler.this);
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
            TEALOptInAssetDialog dialog = new TEALOptInAssetDialog(project, module, contractHash);
            dialog.enableDryRun();
            dialog.enableDebug();
            boolean ok = dialog.showAndGet();
            if (!ok) {
                return;
            }

            Long assetId = dialog.getAssetId();
            assetTxnParameters = new AssetTxnParameters();
            assetTxnParameters.assetId = assetId;

            senderAddress = dialog.getSenderAddress();

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

            try {
                txnDetailsParams = dialog.getTransactionDtlsEntryForm().getTxnDetailsParameters();
            } catch (Exception exception) {
                console.showErrorMessage("Invalid transaction details input parameters : " + exception.getMessage());
                IdeaUtil.showNotification(project, "Logic Sig transaction",
                        "Invalid transaction details input parameters : " + exception.getMessage(), NotificationType.WARNING, null);
                return;
            }

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

        private void sendOptInLogicSigTransaction(byte[] logicSig) {
            ProgressManager.getInstance().getProgressIndicator().setText("Sending Logic-sig OptIn transaction...");
            try {
                LogListener logListener = new LogListenerAdapter(console);
                LogicSigTransactionService transactionService = new LogicSigTransactionService(project, logListener);

                RequestMode originalReqMode = requestMode;
                if(requestMode.equals(RequestMode.DEBUG)) {
                    requestMode = RequestMode.EXPORT_SIGNED;
                }

                console.showInfoMessage(String.format("Starting %s ...\n", getTxnCommand()));
                try {

                    Result result = transactionService.logicSigOptInAssetTransaction(logicSig, senderAddress, assetTxnParameters, txnDetailsParams, requestMode);

                    if(originalReqMode.equals(RequestMode.DEBUG)) {//Debug call
                        DebugHandler debugHandler = new DebugHandler();
                        debugHandler.startDebugger(project, console, sourcePath, (SignedTransaction) result.getValue(), logicSigParams);
                    } else {
                        processResult(project, module, result, requestMode, logListener);
                    }
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
                IdeaUtil.showNotification(project, getTitle(), String.format("OptIn Logic sig transaction failed, reason: %s", ex.getMessage()), NotificationType.ERROR, null);
            }
        }
    }
}
