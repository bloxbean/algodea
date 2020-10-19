package com.bloxbean.algodea.idea.nodeint.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.PaymentTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;

import java.io.File;
import java.math.BigInteger;

public class LogicSigTransactionService extends TransactionService{

    public LogicSigTransactionService(Project project, LogListener logListener) throws DeploymentTargetNotConfigured {
        super(project, logListener);
    }

    public boolean logicSigTransaction(String lsigPath, Address sender, Address receiver, BigInteger amount, TxnDetailsParameters txnDetailsParameters)
            throws Exception {

        byte[] logicSigBytes = FileUtil.loadFileBytes(new File(lsigPath));
        if(sender == null) {
            return contractAccountTransaction(logicSigBytes, receiver, amount, txnDetailsParameters);
        } else {
            return accountDelegationTransaction(logicSigBytes, sender, receiver, amount, txnDetailsParameters);
        }

    }

    public boolean contractAccountTransaction(byte[] logicSigBytes, Address receiver, BigInteger amount, TxnDetailsParameters txnDetailsParameters)
            throws Exception {

        if(receiver == null) {
            logListener.error("Receiver account cannot be null");
            return false;
        }

        if(amount == null) {
            logListener.error("Amount cannot be null");
            return false;
        }

        LogicsigSignature logicsigSignature = Encoder.decodeFromMsgPack(logicSigBytes, LogicsigSignature.class);
        Address fromAddress = logicsigSignature.toAddress();

        logListener.info("Starting Contract Account transaction ...\n");

        logListener.info("From Contract Address     : " + fromAddress.toString());
//        logListener.info("Receiver Address          : " + receiver);
//        logListener.info(String.format("Amount           : %f Algo ( %d )\n", AlgoConversionUtil.mAlgoToAlgo(amount), amount));

        PaymentTransactionBuilder paymentTransactionBuilder = Transaction.PaymentTransactionBuilder();
        Transaction txn = populatePaymentTransaction(paymentTransactionBuilder, fromAddress, receiver.toString(), amount.longValue(), txnDetailsParameters);

        if(txn == null) {
            logListener.error("Transaction could not be built");
            return false;
        }

        TransactionSigner txnSigner = new TransactionSigner() {
            @Override
            public SignedTransaction signTransaction(Transaction transaction) throws Exception {
                return Account.signLogicsigTransaction(logicsigSignature, transaction);
            }
        };

       return postTransaction(txnSigner, txn);

    }

    public boolean accountDelegationTransaction(byte[] logicSigBytes, Address sender, Address receiver, BigInteger amount,
                                                TxnDetailsParameters txnDetailsParameters) throws Exception {
        if(sender == null) {
            logListener.error("Sendre address cannot be empty");
            return false;
        }

        if(receiver == null) {
            logListener.error("Receiver address cannot be empty");
            return false;
        }

        if(amount == null) {
            logListener.error("Amount cannot be null");
            return false;
        }

        LogicsigSignature logicsigSignature = Encoder.decodeFromMsgPack(logicSigBytes, LogicsigSignature.class);

        logListener.info("Starting Account Delegation transaction ...\n");

//        logListener.info("From Address     : " + fromAccount.getAddress().toString());
//        logListener.info("Receiver Address : " + receiver);
//        logListener.info(String.format("Amount           : %f Algo ( %d )\n", AlgoConversionUtil.mAlgoToAlgo(amount), amount));

        PaymentTransactionBuilder paymentTransactionBuilder = Transaction.PaymentTransactionBuilder();
        Transaction txn = populatePaymentTransaction(paymentTransactionBuilder, sender, receiver.toString(), amount.longValue(), txnDetailsParameters);

        if(txn == null) {
            logListener.error("Transaction could not be built");
            return false;
        }

        TransactionSigner txnSigner = new TransactionSigner() {
            @Override
            public SignedTransaction signTransaction(Transaction transaction) throws Exception {
                return Account.signLogicsigTransaction(logicsigSignature, txn);
            }
        };

        return postTransaction(txnSigner, txn);
    }
}
