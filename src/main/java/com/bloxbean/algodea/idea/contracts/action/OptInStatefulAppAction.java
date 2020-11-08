package com.bloxbean.algodea.idea.contracts.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.StatefulContractService;
import com.intellij.icons.AllIcons;

import java.util.List;

public class OptInStatefulAppAction extends BaseStatefulAppAction {

    public OptInStatefulAppAction() {
        super(AllIcons.Actions.ShowReadAccess);
    }

    @Override
    public String getInputDialogTitle() {
        return "Application - OptIn";
    }

    @Override
    public String getApplicationTxnDescription() {
        return "Application - OptIn";
    }

    @Override
    public String getApplicationTxnCommand() {
        return "OptIn";
    }

    @Override
    public Result invokeTransaction(StatefulContractService sfService, Long appId, Account fromAccount,
                                    TxnDetailsParameters txnDetailsParameters) throws Exception {
        return sfService.optIn(appId, fromAccount, txnDetailsParameters);
    }
}
