package com.bloxbean.algodea.idea.contracts.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.StatefulContractService;

import java.util.List;

public class OptInStatefulAppAction extends BaseStatefulAppAction {
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
    public boolean invokeTransaction(StatefulContractService sfService, Long appId, Account fromAccount,
                                     TxnDetailsParameters txnDetailsParameters) throws Exception {
        return sfService.optIn(appId, fromAccount, txnDetailsParameters);
    }
}
