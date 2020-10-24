package com.bloxbean.algodea.idea.assets.action;

import com.algorand.algosdk.account.Account;
import com.bloxbean.algodea.idea.nodeint.model.AssetTxnParameters;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.AssetTransactionService;

public class RevokeAssetAction extends BaseAssetOperationAction {
//    private final static Logger LOG = Logger.getInstance(RevokeAssetAction.class);

    public RevokeAssetAction() {
        super();
    }

    @Override
    protected AssetActionType getActionType() {
        return AssetActionType.REVOKE;
    }

    @Override
    protected String getTxnCommand() {
        return "Asset Revoke";
    }

    @Override
    protected String getTitle() {
        return "AssetRevoke";
    }

    @Override
    protected boolean invokeAssetOperation(AssetTransactionService assetTransactionService, Account sender, AssetTxnParameters finalAssetTxnPrameters, TxnDetailsParameters txnDetailsParameters) throws Exception {
        return assetTransactionService.revokeAsset(sender, finalAssetTxnPrameters, txnDetailsParameters);
    }

}
