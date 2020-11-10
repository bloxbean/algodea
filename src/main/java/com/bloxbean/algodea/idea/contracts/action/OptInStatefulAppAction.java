package com.bloxbean.algodea.idea.contracts.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
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
    public String getTxnCommand() {
        return "OptIn";
    }

    @Override
    public Result invokeTransaction(StatefulContractService sfService, Long appId, Account fromAccount,
                                    TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        return sfService.optIn(appId, fromAccount, txnDetailsParameters, requestMode);
    }
}
