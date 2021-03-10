package com.bloxbean.algodea.idea.assets.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.model.AssetTxnParameters;
import com.bloxbean.algodea.idea.nodeint.model.Result;
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
    protected Result invokeAssetOperation(AssetTransactionService assetTransactionService, Account signer, Address sender,
                                          AssetTxnParameters finalAssetTxnPrameters, TxnDetailsParameters txnDetailsParameters,
                                          RequestMode requestMode) throws Exception {
        return assetTransactionService.modifyAsset(signer, sender, finalAssetTxnPrameters, txnDetailsParameters, requestMode);
    }

}
