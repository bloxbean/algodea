package com.bloxbean.algodea.idea.contracts.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.nodeint.service.StatefulContractService;

import java.util.List;

public class ClearStateStatefulAppAction extends BaseStatefulAppAction {
    @Override
    public String getInputDialogTitle() {
        return "Application - ClearState";
    }

    @Override
    public String getApplicationTxnDescription() {
        return "Application - ClearState";
    }

    @Override
    public String getApplicationTxnCommand() {
        return "ClearState";
    }

    @Override
    public boolean invokeTransaction(StatefulContractService sfService, Long appId, Account fromAccount,
                                     List<byte[]> appArgs, byte[] note, byte[] lease, List<Address> accounts,
                                     List<Long> foreignApps, List<Long> foreignAssets) throws Exception {
        return sfService.clear(appId, fromAccount, appArgs, note, lease, accounts, foreignApps, foreignAssets);
    }
}
