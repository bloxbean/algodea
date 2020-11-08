package com.bloxbean.algodea.idea.nodeint.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.PaymentTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.util.AlgoConversionUtil;
import com.bloxbean.algodea.idea.util.JsonUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;

import java.math.BigInteger;

public class TransactionService extends AlgoBaseService {

    public TransactionService(Project project, LogListener logListener) throws DeploymentTargetNotConfigured {
        super(project, logListener);
    }

    public Result transfer(Account sender, String receiver, Long amount, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        if (sender == null) {
            logListener.error("Sender cannot be null");
            return Result.error();
        }

        PaymentTransactionBuilder paymentTransactionBuilder = Transaction.PaymentTransactionBuilder();
        Transaction txn = populatePaymentTransaction(paymentTransactionBuilder, sender.getAddress(), receiver, amount, txnDetailsParameters);

        if (txn == null) {
            logListener.error("Transaction could not be built");
            return Result.error();
        }

        SignedTransaction stxn = signTransaction(sender, txn);

        if (requestMode == null || requestMode.equals(RequestMode.TRANSACTION)) {
            return postApplicationTransaction(sender, stxn);
        } else if (requestMode.equals(RequestMode.EXPORT_SIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(stxn));
        } else if(requestMode.equals(RequestMode.EXPORT_UNSIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(txn));
        } else {
            return Result.error("Invalid request mode : " + requestMode);
        }
    }

    protected Transaction populatePaymentTransaction(PaymentTransactionBuilder paymentTransactionBuilder, Address fromAccount,
                                                     String receiver, Long amount, TxnDetailsParameters txnDetailsParameters) throws Exception {
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

        populateBaseTransactionDetails(paymentTransactionBuilder, fromAccount, txnDetailsParameters);

        return paymentTransactionBuilder.build();
    }
}
