package com.bloxbean.algodea.idea.account.cache;

import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.intellij.openapi.util.text.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to read from result json file
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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

    public boolean deleteAccount(AlgoAccount account) {
        if(accounts == null || account == null)
            return false;

        if(accounts.contains(account)) {
            accounts.remove(account);
            return true;
        } else {
            return false;
        }
    }

    public boolean updateAccountName(String address, String name) {
        if(accounts == null || StringUtil.isEmpty(address))
            return false;

        for(AlgoAccount account: accounts) {
            if(address.equals(account.getAddress())) {
                account.setName(name);
                return true;
            }
        }

        return false;
    }

    public void addMultisigAccount(AlgoMultisigAccount multisigAccount) {
        if(multisigAccounts == null)
            multisigAccounts = new ArrayList<>();

        if(multisigAccount == null)
            return;

        multisigAccounts.add(multisigAccount);
    }

    public boolean deleteMultisigAccount(AlgoMultisigAccount multisigAccount) {
        if(multisigAccounts == null || multisigAccount == null)
            return false;

        if(multisigAccounts.contains(multisigAccount)) {
            multisigAccounts.remove(multisigAccount);
            return true;
        } else {
            return false;
        }
    }
}