package com.bloxbean.algodea.idea.codegen.service.detector;

import com.algorand.algosdk.transaction.Transaction;
import com.bloxbean.algodea.idea.codegen.service.TxnType;

import java.util.ArrayList;
import java.util.List;

public enum TypeDetectorFactory {
    INSTANCE();

    private List<TransactionTypeDetector> detectors;

    TypeDetectorFactory() {
        detectors = new ArrayList<>();
        detectors.add(new AppCallTypeDetector());
    }

    public TxnType deletectType(Transaction transaction) {
        for (TransactionTypeDetector detector: detectors) {
            TxnType type = detector.detect(transaction);
            if (type != null)
                return type;
        }

        return null;
    }
}
