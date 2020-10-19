package com.bloxbean.algodea.idea.stateless.action;

import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.common.AlgoIcons;
import com.bloxbean.algodea.idea.common.Tuple;
import com.bloxbean.algodea.idea.core.action.AlgoBaseAction;
import com.bloxbean.algodea.idea.module.filetypes.LSigFileType;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.service.LogicSigTransactionService;
import com.bloxbean.algodea.idea.stateless.ui.LogicSigSendTransactionDialog;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigInteger;

public class LogicSigSendTransactionAction extends AlgoBaseAction {
    private final static Logger LOG = Logger.getInstance(LogicSigSendTransactionAction.class);

    public LogicSigSendTransactionAction() {
        super(AlgoIcons.LOGIC_SIG_RUN_ICON);
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        VirtualFile file = e.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);

        if (file != null && LSigFileType.EXTENSION.equals(file.getExtension())) {
            e.getPresentation().setEnabled(true);
        } else {
//            e.getPresentation().setVisible(false);
            e.getPresentation().setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (LOG.isDebugEnabled())
            LOG.debug("Logic sig transaction");

        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        if (project == null)
            return;

        final AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();

        VirtualFile lsigVfs = e.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);
        if(lsigVfs == null || !lsigVfs.exists()) {
            IdeaUtil.showNotification(project, "Stateless contract transaction", "Logic Sig file doesn't exist or not readable", NotificationType.ERROR, null);
            return;
        }

        byte[] logicSig = null;
        try {
            logicSig = VfsUtil.loadBytes(lsigVfs);
            System.out.println(logicSig);
        } catch (IOException ioException) {
            console.showErrorMessage("Error loading Logic sig file : " + lsigVfs.getCanonicalPath());
            IdeaUtil.showNotification(project, "Logic Sig transaction",
                    "Logic Sig file is not readable : " + ioException.getMessage(), NotificationType.ERROR, null);
            return;
        }

        //Open dialog and get transaction inputs
        LogicSigSendTransactionDialog dialog = new LogicSigSendTransactionDialog(project, lsigVfs.getCanonicalPath());
        TransactionDtlsEntryForm transactionDtlsEntryForm = dialog.getTransactionDtlsEntryForm();

        boolean ok = dialog.showAndGet();
        if(!ok) {
            IdeaUtil.showNotification(project, "Logic Sig transaction",
                    "Stateless contract transaction was cancelled", NotificationType.WARNING, null);
            return;
        }

        //Get input values
        boolean isContractAccountTxnType = dialog.isContractAccountType();
        boolean isAccountDelegationTxnType = dialog.isAccountDelegationType();

        String lsigPath = lsigVfs.getCanonicalPath();
        Address senderAddress = null;
        if(isAccountDelegationTxnType) {
            senderAddress = dialog.getSenderAddress();
        }

        Address receiverAddress = dialog.getReceiverAddress();

        Tuple<Double, BigInteger> amounts = dialog.getAmount(); //Algo, mAlgo

        //txn detals params
        TxnDetailsParameters txnDetailsParameters = null;
        try {
            txnDetailsParameters = transactionDtlsEntryForm.getTxnDetailsParameters();
        } catch (Exception exception) {
            console.showErrorMessage("Invalid transaction details input parameters : " +  exception.getMessage());
            IdeaUtil.showNotification(project, "Logic Sig transaction",
                    "Invalid transaction details input parameters : " +  exception.getMessage(), NotificationType.WARNING, null);
            return;
        }

        Address finalSenderAccount = senderAddress;
        TxnDetailsParameters finalTxnDetailsParams = txnDetailsParameters;

        try {
            LogicSigTransactionService transactionService = new LogicSigTransactionService(project, new LogListenerAdapter(console));

            Task.Backgroundable task = new Task.Backgroundable(project, getTxnCommand()) {

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    console.showInfoMessage(String.format("Starting %s ...\n", getTxnCommand()));
                    try {
                        boolean status = transactionService.logicSigTransaction(lsigVfs.getCanonicalPath(), finalSenderAccount, receiverAddress, amounts._2(), finalTxnDetailsParams);

                        if (status) {
                            console.showInfoMessage(String.format("%s transaction executed successfully", getTxnCommand()));
                            IdeaUtil.showNotification(project, getTitle(), String.format("%s was successful", getTxnCommand()), NotificationType.INFORMATION, null);
                        } else {
                            console.showErrorMessage(String.format("%s failed", getTxnCommand()));
                            IdeaUtil.showNotification(project, getTitle(), String.format("%s failed", getTxnCommand()), NotificationType.ERROR, null);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        console.showErrorMessage(String.format("%s failed", getTxnCommand()), exception);
                        IdeaUtil.showNotification(project, getTitle(), String.format("%s failed, Reason: %s", getTxnCommand(), exception.getMessage()), NotificationType.ERROR, null);
                    }
                }
            };

            ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            deploymentTargetNotConfigured.printStackTrace();
            warnDeploymentTargetNotConfigured(project, getTitle());
        } catch (Exception ex) {
            LOG.error(ex);
            console.showErrorMessage(ex.getMessage(), ex);
            IdeaUtil.showNotification(project, getTitle(), String.format("Logic sig transaction failed, reason: %s", ex.getMessage()), NotificationType.ERROR, null);
        }

    }

    public String getTitle() {
        return "Stateless Contract Transaction";
    }

    public String getTxnCommand() {
        return "Stateless Contract Transaction";
    }
}
