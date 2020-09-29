package com.bloxbean.algorand.idea.action.account.cache;

import com.bloxbean.algorand.idea.action.account.model.AlgoAccount;
import com.bloxbean.algorand.idea.action.account.model.AlgoMultisigAccount;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to read from result json file
 */
@JsonIgnoreProperties
public class AccountCache {
    private List<AlgoAccount> accounts;
    private List<AlgoMultisigAccount> multisigAccounts;

    public AccountCache() {
        accounts = new ArrayList<>();
    }

    public List<AlgoAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AlgoAccount> accounts) {
        this.accounts = accounts;
    }

    public List<AlgoMultisigAccount> getMultisigAccounts() {
        return multisigAccounts;
    }

    public void setMultisigAccounts(List<AlgoMultisigAccount> multisigAccounts) {
        this.multisigAccounts = multisigAccounts;
    }

    public void addAccount(AlgoAccount account) {
        if(accounts == null)
            accounts = new ArrayList<>();

        if(account == null)
            return;

        accounts.add(account);
    }

    public void addMultisigAccount(AlgoMultisigAccount multisigAccount) {
        if(multisigAccounts == null)
            multisigAccounts = new ArrayList<>();

        if(multisigAccount == null)
            return;

        multisigAccounts.add(multisigAccount);
    }
}