package com.bloxbean.algodea.idea.atomic.model;

import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class AtomicTransaction {
    private String txnFile;
    private Transaction transaction;
    private SignedTransaction signedTransaction;

    public AtomicTransaction(String txnFile) {
        this.txnFile = txnFile;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public SignedTransaction getSignedTransaction() {
        return signedTransaction;
    }

    public void setSignedTransaction(SignedTransaction signedTransaction) {
        this.signedTransaction = signedTransaction;
    }

    @Override
    public String toString() {
        String msg = txnFile;
        if(getSignedTransaction() != null)
            msg += "  (Signed)";

        return msg;
    }

    public static AtomicTransaction loadTransaction(File file) throws IOException {
        if(file == null || !file.exists())
            return null;

        ObjectMapper objectMapper = new ObjectMapper();
        Transaction transaction = objectMapper.readValue(file, Transaction.class);

        AtomicTransaction atomicTransaction = new AtomicTransaction(file.getAbsolutePath());
        atomicTransaction.setTransaction(transaction);

        return atomicTransaction;
    }
}
