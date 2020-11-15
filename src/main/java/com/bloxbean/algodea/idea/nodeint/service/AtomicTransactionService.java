package com.bloxbean.algodea.idea.nodeint.service;

import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.transaction.TxGroup;
import com.algorand.algosdk.util.Encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class AtomicTransactionService {
//    public AtomicTransactionService(Project project, LogListener logListener) throws DeploymentTargetNotConfigured {
//        super(project, logListener);
//    }

    public static Digest assignGroup(List<Transaction> transactions) throws IOException {
        if(transactions == null || transactions.size() == 0)
            return null;

        Digest gid = TxGroup.computeGroupID(transactions.toArray(new Transaction[0]));

        for(Transaction txn: transactions) {
            txn.assignGroupID(gid);
        }

        return gid;
    }

    public static byte[] assembleTransactionGroup(List<SignedTransaction> signTransactions) throws IOException {
        if(signTransactions == null || signTransactions.size() == 0)
            return new byte[0];

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream( );

        for(SignedTransaction signedTransaction: signTransactions) {
            byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTransaction);
            byteOutputStream.write(encodedTxBytes);
        }
        byte groupTransactionBytes[] = byteOutputStream.toByteArray();

        return groupTransactionBytes;
    }
}
