package com.bloxbean.algodea.idea.assets.action;

import com.algorand.algosdk.account.Account;
import com.bloxbean.algodea.idea.nodeint.model.AssetTxnParameters;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.AssetTransactionService;

public class FreezeAssetAction extends BaseAssetOperationAction {
    //private final static Logger LOG = Logger.getInstance(FreezeAssetAction.class);

    public FreezeAssetAction() {
        super();
    }

    @Override
    protected AssetActionType getActionType() {
        return AssetActionType.FREEZE;
    }

    @Override
    protected String getTxnCommand() {
        return "AssetFreeze";
    }

    @Override
    protected String getTitle() {
        return "Asset Freeze";
    }

    @Override
    protected boolean invokeAssetOperation(AssetTransactionService assetTransactionService, Account sender, AssetTxnParameters finalAssetTxnPrameters, TxnDetailsParameters txnDetailsParameters) throws Exception {
        return assetTransactionService.freezeAsset(sender, finalAssetTxnPrameters, txnDetailsParameters);
    }
}
