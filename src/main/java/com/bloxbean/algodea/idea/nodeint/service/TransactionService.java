package com.bloxbean.algodea.idea.nodeint.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.PaymentTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.util.AlgoConversionUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;

import java.math.BigInteger;

public class TransactionService extends AlgoBaseService {

    public TransactionService(Project project, LogListener logListener) throws DeploymentTargetNotConfigured {
        super(project, logListener);
    }

    public boolean transfer(Account sender, String receiver, Long amount, TxnDetailsParameters txnDetailsParameters) throws Exception {
        if(sender == null) {
            logListener.error("Sender cannot be null");
            return false;
        }

        PaymentTransactionBuilder paymentTransactionBuilder = Transaction.PaymentTransactionBuilder();
        Transaction txn = populatePaymentTransaction(paymentTransactionBuilder, sender.getAddress(), receiver, amount, txnDetailsParameters);

        if(txn == null) {
            logListener.error("Transaction could not be built");
            return false;
        }

        return postApplicationTransaction(sender, txn);
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
        // define sender
        Address sender = fromAccount;

        logListener.info("Getting node suggested transaction parameters ...");
        // get node suggested parameters
        Response<TransactionParametersResponse> transactionParametersResponse = client.TransactionParams().execute();
        if(!transactionParametersResponse.isSuccessful()) {
            printErrorMessage("Unable to get Transaction Params from the node", transactionParametersResponse);
            return null;
        }

        TransactionParametersResponse params = transactionParametersResponse.body();
        logListener.info("Got node suggested transaction parameters.");

        // create unsigned transaction
        paymentTransactionBuilder
                .sender(sender)
                .suggestedParams(params);

        paymentTransactionBuilder.amount(amount)
                .receiver(receiver);

        if(txnDetailsParameters.getNote() != null) {
            paymentTransactionBuilder.note(txnDetailsParameters.getNote());
        }

        if(txnDetailsParameters.getLease() != null) {
            paymentTransactionBuilder.lease(txnDetailsParameters.getLease());
        }

        return paymentTransactionBuilder.build();
    }
}
