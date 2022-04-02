package com.bloxbean.algodea.idea.codegen.service.detector;

import com.algorand.algosdk.transaction.Transaction;
import com.bloxbean.algodea.idea.codegen.service.TxnType;

public interface TransactionTypeDetector {
    TxnType detect(Transaction transaction);
}
