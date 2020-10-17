package com.bloxbean.algodea.idea.contracts.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.StatefulContractService;
import com.intellij.icons.AllIcons;

import java.util.List;

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
    public String getApplicationTxnCommand() {
        return "DeleteApplication";
    }

    @Override
    public boolean invokeTransaction(StatefulContractService sfService, Long appId, Account fromAccount,
                                     TxnDetailsParameters txnDetailsParameters) throws Exception {
        return sfService.delete(appId, fromAccount, txnDetailsParameters);
    }
}
