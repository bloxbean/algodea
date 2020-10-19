package com.bloxbean.algodea.idea.nodeint.service;

import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;

public interface TransactionSigner {
    SignedTransaction signTransaction(Transaction transaction) throws Exception;
}
