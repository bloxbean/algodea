package com.bloxbean.algodea.idea.atomic.model;

import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.bloxbean.algodea.idea.nodeint.model.DryRunContext;
import com.intellij.openapi.util.io.FileUtil;

import java.io.File;
import java.io.IOException;

public class AtomicTransaction {
    private String txnFile;
    private Transaction transaction;
    private SignedTransaction signedTransaction;

    //DryRunSource required for Dryrun
    private DryRunContext.Source dryRunSource;

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

    public DryRunContext.Source getDryRunSource() {
        return dryRunSource;
    }

    public void setDryRunSource(DryRunContext.Source dryRunSource) {
        this.dryRunSource = dryRunSource;
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

        String content = FileUtil.loadFile(file);
        Transaction transaction = Encoder.decodeFromJson(content, Transaction.class);

        AtomicTransaction atomicTransaction = new AtomicTransaction(file.getAbsolutePath());
        atomicTransaction.setTransaction(transaction);

        return atomicTransaction;
    }
}
