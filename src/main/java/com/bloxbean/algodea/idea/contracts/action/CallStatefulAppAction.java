package com.bloxbean.algodea.idea.contracts.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.StatefulContractService;
import com.intellij.icons.AllIcons;

public class CallStatefulAppAction extends BaseStatefulAppAction {

    public CallStatefulAppAction() {
        super("Call App", "Call Application", AllIcons.Actions.Execute);
    }

    @Override
    public String getInputDialogTitle() {
        return "Application - Call (NoOp)";
    }

    @Override
    public String getApplicationTxnDescription() {
        return "Application - Call (NoOp)";
    }

    @Override
    public String getTxnCommand() {
        return "Call (NoOp)";
    }

    @Override
    public Result invokeTransaction(StatefulContractService sfService, Long appId, Account signer, Address sender,
                                    TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        return sfService.call(appId, signer, sender, txnDetailsParameters, requestMode);
    }
}
