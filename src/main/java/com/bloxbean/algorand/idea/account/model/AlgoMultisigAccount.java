package com.bloxbean.algorand.idea.account.model;

import java.math.BigInteger;
import java.util.List;

public class AlgoMultisigAccount {
    private String address;
    private List<String> accounts;
    private int threshold;
    private BigInteger balance;

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

    public BigInteger getBalance() {
        return balance;
    }

    public void setBalance(BigInteger balance) {
        this.balance = balance;
    }
}
