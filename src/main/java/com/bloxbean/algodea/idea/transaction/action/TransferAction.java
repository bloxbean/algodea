package com.bloxbean.algodea.idea.transaction.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.common.Tuple;
import com.bloxbean.algodea.idea.core.action.BaseTxnAction;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.AccountAsset;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.*;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
import com.bloxbean.algodea.idea.transaction.ui.TransferDialog;
import com.bloxbean.algodea.idea.transaction.ui.TransferTxnParamEntryForm;
import com.bloxbean.algodea.idea.util.AlgoConversionUtil;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TransferAction extends BaseTxnAction {
    private final static Logger LOG = Logger.getInstance(TransferAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            return;

        final Module module = LangDataKeys.MODULE.getData(e.getDataContext());

        AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();

        try {
            TransferDialog transferDialog = new TransferDialog(project);
            boolean ok = transferDialog.showAndGet();

            if (!ok) {
                IdeaUtil.showNotification(project, getTitle(), String.format("%s cancelled", getTxnCommand()), NotificationType.WARNING, null);
                return;
            }

            TransferTxnParamEntryForm txnEntryForm = transferDialog.getTransferTxnEntryForm();
            Account fromAccount = txnEntryForm.getFromAccount();
            Address toAddress = txnEntryForm.getToAccount();
            Tuple<BigDecimal, BigInteger> amountTuple = txnEntryForm.getAmount();
            if (amountTuple == null) {
                IdeaUtil.showNotification(project, getTitle(), "Invalid amount", NotificationType.ERROR, null);
                return;
            }

            final AccountAsset asset = txnEntryForm.getAsset();
            String assetName = "Algo";

            if (asset != null) {
                assetName = asset.getAssetName();
            }
            final String finalAssetName = assetName;

            Address closeReminderTo = txnEntryForm.getCloseReminderTo();

            TransactionDtlsEntryForm transactionDtlsEntryForm = transferDialog.getTransactionDtlsEntryForm();

            TxnDetailsParameters txnDetailsParameters = transactionDtlsEntryForm.getTxnDetailsParameters();

            LogListener logListener = new LogListenerAdapter(console);
            TransactionService transactionService = new TransactionService(project, logListener);
            AssetTransactionService assetTransactionService = new AssetTransactionService(project, logListener);

            RequestMode requestMode = transferDialog.getRequestMode();

            Task.Backgroundable task = new Task.Backgroundable(project, getTxnCommand()) {

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    console.showInfoMessage(String.format("Starting %s ...\n", getTxnCommand()));
                    try {
                        Result result = null;

                        String amountInDecimal = String.valueOf(amountTuple._1());
                        //Get formatted amount string
                        try {
                            if (transferDialog.isAlgoTransfer()) {
                                amountInDecimal = AlgoConversionUtil.mAlgoToAlgoFormatted(amountTuple._2());
                            } else {
                                amountInDecimal = AlgoConversionUtil.toAssetDecimalAmtFormatted(amountTuple._2(), (int) asset.getDecimals());
                            }
                        } catch (Exception e) {

                        }

                        if (transferDialog.isAlgoTransfer()) {
                            result = transactionService.transfer(fromAccount, toAddress.toString(), amountTuple._2().longValue(),
                                    closeReminderTo, txnDetailsParameters, requestMode);
                        } else { //asset transfer
                            result = assetTransactionService.assetTransfer(fromAccount, toAddress.toString(), asset, amountTuple._2(), txnDetailsParameters, requestMode);
                        }

                        processResult(project, module, result, requestMode, logListener);
//                        if(requestMode == null || requestMode.equals(RequestMode.TRANSACTION)) {
//                            if (result.isSuccessful()) {
//                                console.showInfoMessage(String.format("Successfully transferred %s %s from %s to %s  ", amountInDecimal,
//                                        finalAssetName, fromAccount.getAddress().toString(), toAddress.toString()));
//                                IdeaUtil.showNotification(project, getTitle(), String.format("%s was successful", getTxnCommand()),
//                                        NotificationType.INFORMATION, null);
//                            } else {
//                                console.showErrorMessage(String.format("%s failed", getTxnCommand()));
//                                IdeaUtil.showNotification(project, getTitle(), String.format("%s failed", getTxnCommand()), NotificationType.ERROR, null);
//                            }
//                        } else if(requestMode.equals(RequestMode.EXPORT_SIGNED) || requestMode.equals(RequestMode.EXPORT_UNSIGNED)) {
//                            exportTransaction(project, module, requestMode, result, logListener);
//                        }
                    } catch (Exception exception) {
                        if (LOG.isDebugEnabled()) {
                            LOG.warn(exception);
                        }

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
            IdeaUtil.showNotification(project, getTitle(), String.format("Transfer transaction failed, reason: %s", ex.getMessage()), NotificationType.ERROR, null);
        }
    }


    public String getTitle() {
        return "Transfer Transaction";
    }

    public String getTxnCommand() {
        return "Transfer";
    }
}
