package com.bloxbean.algorand.idea.action.account.model;

import java.util.List;

public class AlgoMultisigAccount {
    public String address;
    public List<String> accounts;
    public int threshold;

    public AlgoMultisigAccount() {

    }

    public AlgoMultisigAccount(String address, int threshold) {
        this.address = address;
        this.threshold = threshold;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<String> accounts) {
        this.accounts = accounts;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
