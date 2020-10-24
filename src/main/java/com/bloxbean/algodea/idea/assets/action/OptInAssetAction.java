package com.bloxbean.algodea.idea.assets.action;

import com.algorand.algosdk.account.Account;
import com.bloxbean.algodea.idea.nodeint.model.AssetTxnParameters;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.AssetTransactionService;

public class OptInAssetAction extends BaseAssetOperationAction {
    //private final static Logger LOG = Logger.getInstance(OptInAssetAction.class);
    public OptInAssetAction() {
        super();
    }

    @Override
    protected AssetActionType getActionType() {
        return AssetActionType.OPT_IN;
    }

    protected String getTxnCommand() {
        return "AssetOptIn";
    }

    protected String getTitle() {
        return "Asset OptIn";
    }

    @Override
    protected boolean invokeAssetOperation(AssetTransactionService assetTransactionService, Account sender, AssetTxnParameters finalAssetTxnPrameters, TxnDetailsParameters txnDetailsParameters) throws Exception {
        return assetTransactionService.optInAsset(sender, finalAssetTxnPrameters, txnDetailsParameters);
    }
}
