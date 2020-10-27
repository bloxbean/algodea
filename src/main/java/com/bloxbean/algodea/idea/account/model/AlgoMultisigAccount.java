package com.bloxbean.algodea.idea.account.model;

import java.util.List;
import java.util.Objects;

public class AlgoMultisigAccount {
    private int version = 1;
    private String address;
    private List<String> accounts;
    private int threshold;
    private Long balance;

    public AlgoMultisigAccount() {

    }

    public AlgoMultisigAccount(int version, String address, int threshold) {
        this.version = version;
        this.address = address;
        this.threshold = threshold;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlgoMultisigAccount that = (AlgoMultisigAccount) o;
        return version == that.version &&
                address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, address);
    }
}
