package com.bloxbean.algorand.idea.account.model;

import java.math.BigInteger;

public class AlgoAccount {
    private String address;
    private String mnemonic;
    private BigInteger balance;

    public AlgoAccount() {

    }

    public AlgoAccount(String address, String mnemonic) {
        this.address = address;
        this.mnemonic = mnemonic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public BigInteger getBalance() {
        return balance;
    }

    public void setBalance(BigInteger balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return address;
    }
}
