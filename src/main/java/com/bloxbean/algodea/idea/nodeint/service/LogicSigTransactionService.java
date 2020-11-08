package com.bloxbean.algodea.idea.nodeint.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.PaymentTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.util.JsonUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;

import java.io.File;
import java.math.BigInteger;

public class LogicSigTransactionService extends TransactionService{

    public LogicSigTransactionService(Project project, LogListener logListener) throws DeploymentTargetNotConfigured {
        super(project, logListener);
    }

    public Result logicSigTransaction(String lsigPath, Address sender, Address receiver, BigInteger amount, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode)
            throws Exception {

        byte[] logicSigBytes = FileUtil.loadFileBytes(new File(lsigPath));
        if(sender == null) {
            return contractAccountTransaction(logicSigBytes, receiver, amount, txnDetailsParameters, requestMode);
        } else {
            return accountDelegationTransaction(logicSigBytes, sender, receiver, amount, txnDetailsParameters, requestMode);
        }

    }

    public Result contractAccountTransaction(byte[] logicSigBytes, Address receiver, BigInteger amount, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode)
            throws Exception {

        if(receiver == null) {
            logListener.error("Receiver account cannot be null");
            return Result.error();
        }

        if(amount == null) {
            logListener.error("Amount cannot be null");
            return Result.error();
        }

        LogicsigSignature logicsigSignature = Encoder.decodeFromMsgPack(logicSigBytes, LogicsigSignature.class);
        Address fromAddress = logicsigSignature.toAddress();

        logListener.info("Starting Contract Account transaction ...\n");

        logListener.info("From Contract Address     : " + fromAddress.toString());

        PaymentTransactionBuilder paymentTransactionBuilder = Transaction.PaymentTransactionBuilder();
        Transaction txn = populatePaymentTransaction(paymentTransactionBuilder, fromAddress, receiver.toString(), amount.longValue(), txnDetailsParameters);

        if(txn == null) {
            logListener.error("Transaction could not be built");
            return Result.error();
        }

        TransactionSigner txnSigner = new TransactionSigner() {
            @Override
            public SignedTransaction signTransaction(Transaction transaction) throws Exception {
                return Account.signLogicsigTransaction(logicsigSignature, transaction);
            }
        };

        SignedTransaction signTxn = signTransaction(txnSigner, txn);

        if (requestMode == null || requestMode.equals(RequestMode.TRANSACTION)) {
            PendingTransactionResponse txnResponse = postTransaction(signTxn);
            return txnResponse != null? Result.success(txnResponse.toString()): Result.error();
        } else if (requestMode.equals(RequestMode.EXPORT_SIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(signTxn));
        } else if(requestMode.equals(RequestMode.EXPORT_UNSIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(txn));
        } else {
            return Result.error("Invalid request mode : " + requestMode);
        }

    }

    public Result accountDelegationTransaction(byte[] logicSigBytes, Address sender, Address receiver, BigInteger amount,
                                                TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        if(sender == null) {
            logListener.error("Sender address cannot be empty");
            return Result.error();
        }

        if(receiver == null) {
            logListener.error("Receiver address cannot be empty");
            return Result.error();
        }

        if(amount == null) {
            logListener.error("Amount cannot be null");
            return Result.error();
        }

        LogicsigSignature logicsigSignature = Encoder.decodeFromMsgPack(logicSigBytes, LogicsigSignature.class);

        logListener.info("Starting Account Delegation transaction ...\n");

        PaymentTransactionBuilder paymentTransactionBuilder = Transaction.PaymentTransactionBuilder();
        Transaction txn = populatePaymentTransaction(paymentTransactionBuilder, sender, receiver.toString(), amount.longValue(), txnDetailsParameters);

        if(txn == null) {
            logListener.error("Transaction could not be built");
            return Result.error();
        }

        TransactionSigner txnSigner = new TransactionSigner() {
            @Override
            public SignedTransaction signTransaction(Transaction transaction) throws Exception {
                return Account.signLogicsigTransaction(logicsigSignature, txn);
            }
        };

        SignedTransaction signTxn = signTransaction(txnSigner, txn);

        if (requestMode == null || requestMode.equals(RequestMode.TRANSACTION)) {
            PendingTransactionResponse txnResponse = postTransaction(signTxn);
            return txnResponse != null? Result.success(txnResponse.toString()): Result.error();
        } else if (requestMode.equals(RequestMode.EXPORT_SIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(signTxn));
        } else if(requestMode.equals(RequestMode.EXPORT_UNSIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(txn));
        } else {
            return Result.error("Invalid request mode : " + requestMode);
        }
    }
}
