package com.bloxbean.algodea.idea.codegen.service.detector;

import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.bloxbean.algodea.idea.codegen.service.util.TxnType;

public interface TransactionTypeDetector {
    TxnType detect(SignedTransaction signedTransaction, Transaction transaction);
}
