package com.bloxbean.algodea.idea.nodeint.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.PaymentTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.v2.client.model.DryrunResponse;
import com.algorand.algosdk.v2.client.model.DryrunTxnResult;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.util.NetworkHelper;
import com.bloxbean.algodea.idea.util.AlgoConversionUtil;
import com.bloxbean.algodea.idea.util.JsonUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;

import java.math.BigInteger;
import java.util.List;

public class TransactionService extends AlgoBaseService {

    public TransactionService(Project project, LogListener logListener) throws DeploymentTargetNotConfigured {
        super(project, logListener);
    }

    public Result transfer(Account signer, Address sender, String receiver, Long amount, Address closeReminderTo, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        if (signer == null && sender == null) {
            logListener.error("Sender cannot be null");
            return Result.error();
        }

        if(signer == null && !requestMode.equals(RequestMode.EXPORT_UNSIGNED)) {
            logListener.error("Signing account cannot be null");
            return Result.error();
        }

        PaymentTransactionBuilder paymentTransactionBuilder = Transaction.PaymentTransactionBuilder();
        Transaction txn = populatePaymentTransaction(paymentTransactionBuilder, sender, receiver, amount, closeReminderTo, txnDetailsParameters);

        if (txn == null) {
            logListener.error("Transaction could not be built");
            return Result.error();
        }

        if (requestMode == null || requestMode.equals(RequestMode.TRANSACTION)) {
            SignedTransaction stxn = signTransaction(signer, txn);
            return postApplicationTransaction(signer, stxn);
        } else if (requestMode.equals(RequestMode.EXPORT_SIGNED)) {
            SignedTransaction stxn = signTransaction(signer, txn);
            return Result.success(JsonUtil.getPrettyJson(stxn));
        } else if(requestMode.equals(RequestMode.EXPORT_UNSIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(txn));
        } else {
            return Result.error("Invalid request mode : " + requestMode);
        }
    }

    public Result atomicTransfer(String group, byte[] groupTransactions) throws Exception{
        if(groupTransactions == null || groupTransactions.length == 0)
            return Result.error("Empty group transaction bytes");

        Result result = postRawTransaction(groupTransactions);

        if(result.isSuccessful()) {
            if (NetworkHelper.getInstance().getExplorerBaseUrl(getNetworkGenesisHash()) != null)
                logListener.info("Check group details here : " + NetworkHelper.getInstance().getGroupUrl(getNetworkGenesisHash(), group));
        }

        return result;
    }

    public Result atomicTransferDryRun(String group, List<SignedTransaction> stxns) throws Exception {
        if(stxns == null || stxns.size() == 0) {
            return Result.error("No transaction found");
        }

        DryrunResponse dryrunResponse = postStatefulDryRunTransaction(stxns);
        if(dryrunResponse == null) {
            return Result.error("Dry run failed");
        } else {
            List<DryrunTxnResult> dryrunTxnResults = dryrunResponse.txns;
            if(dryrunTxnResults == null || dryrunTxnResults.size() == 0)
                return Result.error("Dry run failed");

            return Result.success(JsonUtil.getPrettyJson(dryrunTxnResults.get(0)));
        }
    }

    protected Transaction populatePaymentTransaction(PaymentTransactionBuilder paymentTransactionBuilder, Address fromAccount,
                                                     String receiver, Long amount, Address closeReminderTo, TxnDetailsParameters txnDetailsParameters) throws Exception {
        if(fromAccount == null) {
            logListener.error("From account cannot be null");
            return null;
        }

        if(StringUtil.isEmpty(receiver)) {
            logListener.error("Receiver account cannot be null");
        }

        if(amount == null) {
            logListener.error("Amount cannot be null");
            return null;
        }

        logListener.info("From Address     : " + fromAccount.toString());
        logListener.info("Receiver Address : " + receiver);
        logListener.info(String.format("Amount           : %s Algo ( %d )\n",
                AlgoConversionUtil.mAlgoToAlgoFormatted(BigInteger.valueOf(amount)), amount));

        //TODO .. Let's check if the amount setting has to be here
        paymentTransactionBuilder.amount(amount)
                .receiver(receiver);

        if(closeReminderTo != null)
            paymentTransactionBuilder.closeRemainderTo(closeReminderTo);

        populateBaseTransactionDetails(paymentTransactionBuilder, fromAccount, txnDetailsParameters);

        return paymentTransactionBuilder.build();
    }
}
