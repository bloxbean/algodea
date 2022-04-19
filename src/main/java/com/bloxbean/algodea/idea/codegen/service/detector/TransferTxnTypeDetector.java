package com.bloxbean.algodea.idea.codegen.service.detector;

import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.bloxbean.algodea.idea.codegen.service.util.TxnType;

public class TransferTxnTypeDetector implements TransactionTypeDetector {
    private LogicsigSignature defaultLogicSig = new LogicsigSignature();

    @Override
    public TxnType detect(SignedTransaction signedTransaction, Transaction transaction) {
        if (transaction.type != Transaction.Type.Payment
                && transaction.type != Transaction.Type.AssetTransfer)
            return null;

        if (signedTransaction != null && (signedTransaction.lSig == null
                || defaultLogicSig.equals(signedTransaction.lSig))) { //No lsig found, so regular payment

            if (transaction.type == Transaction.Type.Payment) {
                return TxnType.TRANSFER_ALGO;
            } else if (transaction.type == Transaction.Type.AssetTransfer) {
                return TxnType.TRANSFER_ASA;
            }
        }

        return null;
    }
}
