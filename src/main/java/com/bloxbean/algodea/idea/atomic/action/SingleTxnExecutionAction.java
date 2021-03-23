package com.bloxbean.algodea.idea.atomic.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountService;
import com.bloxbean.algodea.idea.atomic.ui.SigningAccountInputDialog;
import com.bloxbean.algodea.idea.atomic.ui.SingleTxnDetailsDialog;
import com.bloxbean.algodea.idea.common.AlgoIcons;
import com.bloxbean.algodea.idea.core.action.BaseTxnAction;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.service.TransactionService;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.bloxbean.algodea.idea.util.JsonUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class SingleTxnExecutionAction extends BaseTxnAction {
    private final static Logger LOG = Logger.getInstance(SingleTxnExecutionAction.class);
    private final static String EXTENSION = "tx.json";

    public SingleTxnExecutionAction() {
        super(AlgoIcons.LOGIC_SIG_RUN_ICON);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        if (isAlgoProject(e)) {
            VirtualFile file = e.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);
            if (file.getName().endsWith(EXTENSION)) {
                e.getPresentation().setEnabledAndVisible(true);
            } else {
                e.getPresentation().setEnabledAndVisible(false);
            }
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            return;

        final Module module = LangDataKeys.MODULE.getData(e.getDataContext());

        AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();

        try {

            PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
            if (psiFile == null) {
                console.showErrorMessage("Unable read txn file");
                return;
            }

            //Build txn object
            VirtualFile txnVirtualFile = psiFile.getVirtualFile();
            if (txnVirtualFile == null || !txnVirtualFile.exists()) {
                console.showErrorMessage("Unable read txn file");
                return;
            }

            File txnFile = VfsUtil.virtualToIoFile(txnVirtualFile);
            SignedTransaction signedTransaction = loadSignedTransaction(txnFile);

            if(signedTransaction == null) {
                Transaction txn = loadTransaction(txnFile);

                String content = JsonUtil.getPrettyJson(txn);

                SingleTxnDetailsDialog dialog = new SingleTxnDetailsDialog(project, txn, content);
                boolean ok = dialog.showAndGet();
                if (!ok) {
                    IdeaUtil.showNotification(project, "Post Transaction", "Post Transaction was cancelled", NotificationType.WARNING, null);
                    return;
                }

                signedTransaction = signTransaction(project, module, txn);
                if (signedTransaction == null) {
                    console.showErrorMessage("Transaction signing failed.");
                    return;
                }
            } else {
                Transaction txn = signedTransaction.tx;

                String content = JsonUtil.getPrettyJson(signedTransaction);
                SingleTxnDetailsDialog dialog = new SingleTxnDetailsDialog(project, txn, content);
                dialog.setOkButtonLabel("Send");
                boolean ok = dialog.showAndGet();
                if (!ok) {
                    IdeaUtil.showNotification(project, "Post Transaction", "Post Transaction was cancelled", NotificationType.WARNING, null);
                    return;
                }
            }

            SignedTransaction finalSignedTxn = signedTransaction;

            LogListener logListener = new LogListenerAdapter(console);
            TransactionService transactionService = new TransactionService(project, logListener);

            Task.Backgroundable task = new Task.Backgroundable(project, getTxnCommand()) {

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    console.showInfoMessage(String.format("Starting %s ...\n", getTxnCommand()));
                    try {
                        Result result = transactionService.postSignedTransaction(finalSignedTxn);

                        if (result.isSuccessful()) {
                            console.showInfoMessage("Transaction executed successfully");
                            IdeaUtil.showNotification(project, getTitle(), "Transaction executed successfully",
                                    NotificationType.INFORMATION, null);
                        } else {
                            console.showErrorMessage(String.format("%s failed: %s", getTxnCommand(), result.getResponse()));
                            IdeaUtil.showNotification(project, getTitle(), "Transaction failed : " + result.getResponse(), NotificationType.ERROR, null);
                        }
                    } catch (Exception exception) {
                        if (LOG.isDebugEnabled()) {
                            LOG.warn(exception);
                        }
                        console.showErrorMessage("Error", exception);
                        console.showErrorMessage(String.format("%s failed", getTxnCommand()));
                        IdeaUtil.showNotification(project, getTitle(), String.format("%s failed, Reason: %s", getTxnCommand(), exception.getMessage()), NotificationType.ERROR, null);
                    }
                }
            };

            ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            //deploymentTargetNotConfigured.printStackTrace();
            warnDeploymentTargetNotConfigured(project, getTitle());
        } catch (Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.error(ex);
            }
            console.showErrorMessage(ex.getMessage());
            IdeaUtil.showNotification(project, getTitle(), String.format("Transaction execution failed, reason: %s", ex.getMessage()), NotificationType.ERROR, null);
        }
    }

    private SignedTransaction signTransaction(Project project, Module module, Transaction txn) {
        Address address = txn.sender;
        AccountService accountService = AccountService.getAccountService();
        AlgoAccount algoAccount = accountService.getAccountByAddress(address.toString());

        SigningAccountInputDialog singingAccDialog = new SigningAccountInputDialog(project, module);
        if(algoAccount != null) {
            singingAccDialog.getAccountEntryInputForm().setMnemonic(algoAccount.getMnemonic());
        }

        boolean ok1 = singingAccDialog.showAndGet();

        SignedTransaction signedTransaction = null;
        if(ok1) {
            if (singingAccDialog.isAccountType()) {
                Account account = singingAccDialog.getAccount();
                if (account != null) {
                    try {
                        signedTransaction = account.signTransaction(txn);
                    } catch (Exception exception) {
                        if (LOG.isDebugEnabled())
                            LOG.warn(exception);
                        Messages.showErrorDialog("Signing by account failed. Please select a valid account. \nReason: "
                                + exception.getMessage(), "Trasaction signing");
                    }
                } else {
                    Messages.showErrorDialog("Signing by account failed. Please select a valid account", "Trasaction signing");
                }
            } else {
                LogicsigSignature logicsigSignature = singingAccDialog.getLogicSignature();
                if (logicsigSignature != null) {
                    try {
                        signedTransaction
                                = Account.signLogicsigTransaction(logicsigSignature, txn);
                    } catch (Exception exception) {
                        if (LOG.isDebugEnabled())
                            LOG.warn(exception);
                        Messages.showErrorDialog("Signing by logic sig file failed. Please select a valid logic sig file. \nReason: "
                                + exception.getMessage(), "Trasaction signing");
                    }
                } else {
                    Messages.showErrorDialog("Please select a valid logic sig file", "Trasaction signing");
                }
            }
        }

        return signedTransaction;
    }

    public String getTitle() {
        return "Execute Transaction";
    }

    public String getTxnCommand() {
        return "Execute Transaction";
    }

    private Transaction loadTransaction(File file) throws IOException {
        if (file == null || !file.exists())
            return null;

        String content = FileUtil.loadFile(file);
        Transaction transaction = Encoder.decodeFromJson(content, Transaction.class);
        return transaction;
    }

    private SignedTransaction loadSignedTransaction(File file) {
        try {
            if (file == null || !file.exists())
                return null;

            String content = FileUtil.loadFile(file);
            SignedTransaction transaction = Encoder.decodeFromJson(content, SignedTransaction.class);
            return transaction;
        } catch (Exception e) {
            return null;
        }
    }
}
