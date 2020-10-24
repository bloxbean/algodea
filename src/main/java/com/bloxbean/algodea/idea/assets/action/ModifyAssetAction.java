package com.bloxbean.algodea.idea.assets.action;

import com.algorand.algosdk.account.Account;
import com.bloxbean.algodea.idea.nodeint.model.AssetTxnParameters;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.AssetTransactionService;

public class ModifyAssetAction extends BaseAssetOperationAction {
//    private final static Logger LOG = Logger.getInstance(ModifyAssetAction.class);

    public ModifyAssetAction() {
        super();
    }

    @Override
    protected AssetActionType getActionType() {
        return AssetActionType.MODIFY;
    }

    protected String getTxnCommand() {
        return "AssetModify";
    }

    protected String getTitle() {
        return "Asset Modify";
    }

    @Override
    protected boolean invokeAssetOperation(AssetTransactionService assetTransactionService, Account sender, AssetTxnParameters finalAssetTxnPrameters, TxnDetailsParameters txnDetailsParameters) throws Exception {
        return assetTransactionService.modifyAsset(sender, finalAssetTxnPrameters, txnDetailsParameters);
    }

}
