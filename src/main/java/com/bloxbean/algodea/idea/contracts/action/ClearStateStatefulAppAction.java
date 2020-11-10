package com.bloxbean.algodea.idea.contracts.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.StatefulContractService;
import com.intellij.icons.AllIcons;

import java.util.List;

public class ClearStateStatefulAppAction extends BaseStatefulAppAction {

    public ClearStateStatefulAppAction() {
        super(AllIcons.Actions.Redo);
    }

    @Override
    public String getInputDialogTitle() {
        return "Application - ClearState";
    }

    @Override
    public String getApplicationTxnDescription() {
        return "Application - ClearState";
    }

    @Override
    public String getTxnCommand() {
        return "ClearState";
    }

    @Override
    public Result invokeTransaction(StatefulContractService sfService, Long appId, Account fromAccount,
                                    TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        return sfService.clear(appId, fromAccount, txnDetailsParameters, requestMode);
    }
}
