package com.bloxbean.algodea.idea.assets.action;

import com.algorand.algosdk.account.Account;
import com.bloxbean.algodea.idea.nodeint.model.AssetTxnParameters;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.AssetTransactionService;

public class DestroyAssetAction extends BaseAssetOperationAction {
   // private final static Logger LOG = Logger.getInstance(DestroyAssetAction.class);

    public DestroyAssetAction() {
        super();
    }

    @Override
    protected AssetActionType getActionType() {
        return AssetActionType.DESTROY;
    }

    @Override
    protected String getTxnCommand() {
        return "AssetDestroy";
    }

    @Override
    protected String getTitle() {
        return "Asset Destroy";
    }

    @Override
    protected boolean invokeAssetOperation(AssetTransactionService assetTransactionService, Account sender, AssetTxnParameters finalAssetTxnPrameters, TxnDetailsParameters txnDetailsParameters) throws Exception {
        return assetTransactionService.destroyAsset(sender, finalAssetTxnPrameters, txnDetailsParameters);
    }
}
