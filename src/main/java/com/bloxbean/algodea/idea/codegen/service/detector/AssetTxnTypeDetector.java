package com.bloxbean.algodea.idea.codegen.service.detector;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.transaction.AssetParams;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.bloxbean.algodea.idea.codegen.service.util.TxnType;

import java.math.BigInteger;

import static com.bloxbean.algodea.idea.codegen.service.util.TxnType.*;

public class AssetTxnTypeDetector implements TransactionTypeDetector {
    private AssetParams defaultAssetParams = new AssetParams();
    private LogicsigSignature defaultLogicSig = new LogicsigSignature();

    @Override
    public TxnType detect(SignedTransaction signedTransaction, Transaction transaction) {
        if (transaction.type == Transaction.Type.AssetConfig) {
            if (transaction.assetIndex == null || transaction.assetIndex.equals(BigInteger.ZERO))
                return ASSET_CFG_CREATE;
            else if (transaction.assetParams == null || (defaultAssetParams.equals(transaction.assetParams)))
                return ASSET_CFG_DESTROY;
            else if (transaction.assetParams != null)
                return ASSET_CFG_MODIFY;
            else
                return null;
        } else if (transaction.type == Transaction.Type.AssetTransfer
                && (signedTransaction.lSig == null || defaultLogicSig.equals(signedTransaction.lSig))) {

            if ((transaction.sender.equals(transaction.assetReceiver)) &&
                    (transaction.assetAmount == null || transaction.assetAmount.equals(BigInteger.ZERO)))
                return ASSET_OPTIN;
            else if (transaction.assetSender != null && !transaction.assetSender.equals(new Address()))
                return ASSET_REVOKE;

        } else if (transaction.type == Transaction.Type.AssetFreeze) {
            if (transaction.freezeState)
                return ASSET_FRZ;
            else
                return ASSET_UNFRZ;
        }

        //Nothing matched
        return null;
    }
}
