package com.bloxbean.algodea.idea.contracts.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.StatefulContractService;
import com.intellij.icons.AllIcons;

public class DeleteStatefulAppAction extends BaseStatefulAppAction {

    public DeleteStatefulAppAction() {
        super(AllIcons.Actions.Uninstall);
    }
    @Override
    public String getInputDialogTitle() {
        return "Application - DeleteApplication";
    }

    @Override
    public String getApplicationTxnDescription() {
        return "Application - DeleteApplication";
    }

    @Override
    public String getTxnCommand() {
        return "DeleteApplication";
    }

    @Override
    public Result invokeTransaction(StatefulContractService sfService, Long appId, Account signer, Address sender,
                                    TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        return sfService.delete(appId, signer, sender, txnDetailsParameters, requestMode);
    }
}
