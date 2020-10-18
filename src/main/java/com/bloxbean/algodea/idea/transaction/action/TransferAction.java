package com.bloxbean.algodea.idea.transaction.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.common.Tuple;
import com.bloxbean.algodea.idea.core.action.AlgoBaseAction;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.service.TransactionService;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
import com.bloxbean.algodea.idea.transaction.ui.TransferDialog;
import com.bloxbean.algodea.idea.transaction.ui.TransferTxnParamEntryForm;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public class TransferAction extends AlgoBaseAction {
    private final static Logger LOG = Logger.getInstance(TransferAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(project == null)
            return;

        AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();

        TransferDialog transferDialog = new TransferDialog(project);
        boolean ok = transferDialog.showAndGet();

        if(!ok) {
            IdeaUtil.showNotification(project, getTitle(), String.format("%s cancelled", getTxnCommand()), NotificationType.WARNING, null);
            return;
        }

        TransferTxnParamEntryForm txnEntryForm = transferDialog.getTransferTxnEntryForm();
        Account fromAccount = txnEntryForm.getFromAccount();
        Address toAddress = txnEntryForm.getToAccount();
        Tuple<Double, BigInteger> amountTuple = txnEntryForm.getAmount();
        if(amountTuple == null) {
            IdeaUtil.showNotification(project, getTitle(), "Invalid amount", NotificationType.ERROR, null);
            return;
        }

        try {
            TransactionDtlsEntryForm transactionDtlsEntryForm = transferDialog.getTransactionDtlsEntryForm();

            byte[] note = transactionDtlsEntryForm.getNoteBytes();
            byte[] lease = transactionDtlsEntryForm.getLeaseBytes();

            TxnDetailsParameters txnDetailsParameters = new TxnDetailsParameters();
            txnDetailsParameters.setNote(note);
            txnDetailsParameters.setLease(lease);

            TransactionService transactionService = new TransactionService(project, new LogListenerAdapter(console));

            Task.Backgroundable task = new Task.Backgroundable(project, getTxnCommand()) {

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    console.showInfoMessage(String.format("Starting %s ...\n", getTxnCommand()));
                    try {
                        boolean status = transactionService.transfer(fromAccount, toAddress.toString(), amountTuple._2().longValue(), txnDetailsParameters);

                        if(status) {
                            console.showInfoMessage(String.format("Successfully transferred %f Algo from %s to %s  ", amountTuple._1(), fromAccount.getAddress().toString(), toAddress.toString()));
                            IdeaUtil.showNotification(project, getTitle(), String.format("%s was successful", getTxnCommand()), NotificationType.INFORMATION, null);
                        } else {
                            console.showErrorMessage(String.format("%s failed", getTxnCommand()));
                            IdeaUtil.showNotification(project, getTitle(), String.format("%s failed", getTxnCommand()), NotificationType.ERROR, null);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        console.showErrorMessage(String.format("%s failed", getTxnCommand()));
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
            console.showErrorMessage(ex.getMessage());
            IdeaUtil.showNotification(project, getTitle(), String.format("Transfer transaction failed, reason: %s", ex.getMessage()), NotificationType.ERROR, null);
        }
    }



    public String getTitle() {
        return "Transfer Transaction";
    }

    public String getTxnCommand() {
        return "Transfer Transaction";
    }
}
