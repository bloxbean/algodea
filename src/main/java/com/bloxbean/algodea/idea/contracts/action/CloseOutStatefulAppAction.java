package com.bloxbean.algodea.idea.contracts.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.StatefulContractService;
import com.intellij.icons.AllIcons;

import java.util.List;

public class CloseOutStatefulAppAction extends BaseStatefulAppAction {

    public CloseOutStatefulAppAction() {
        super(AllIcons.Actions.Exit);
    }

    @Override
    public String getInputDialogTitle() {
        return "Application - CloseOut";
    }

    @Override
    public String getApplicationTxnDescription() {
        return "Application - CloseOut";
    }

    @Override
    public String getApplicationTxnCommand() {
        return "CloseOut";
    }

    @Override
    public boolean invokeTransaction(StatefulContractService sfService, Long appId, Account fromAccount,
                                     TxnDetailsParameters txnDetailsParameters) throws Exception {
        return sfService.closeOut(appId, fromAccount, txnDetailsParameters);
    }
}
