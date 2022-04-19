package com.bloxbean.algodea.idea.codegen.service.detector;

import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.bloxbean.algodea.idea.codegen.service.util.TxnType;

import java.util.ArrayList;
import java.util.List;

public enum TypeDetectorFactory {
    INSTANCE();

    private List<TransactionTypeDetector> detectors;

    TypeDetectorFactory() {
        detectors = new ArrayList<>();
        detectors.add(new AppCallTypeDetector());
        detectors.add(new StatelessCallTypeDetector());
        detectors.add(new AssetTxnTypeDetector());
        detectors.add(new TransferTxnTypeDetector());
    }

    public TxnType deletectType(SignedTransaction signedTransaction, Transaction transaction) {
        for (TransactionTypeDetector detector: detectors) {
            TxnType type = detector.detect(signedTransaction, transaction);
            if (type != null)
                return type;
        }

        return null;
    }
}
