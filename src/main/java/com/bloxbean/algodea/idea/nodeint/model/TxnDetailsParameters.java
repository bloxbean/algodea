package com.bloxbean.algodea.idea.nodeint.model;

import com.algorand.algosdk.crypto.Address;

import java.math.BigInteger;
import java.util.List;

public class TxnDetailsParameters {
    private List<byte[]> appArgs;
    private byte[] note;
    private byte[] lease;
    private BigInteger fee;
    private BigInteger flatFee;

    private List<Address> accounts;
    private List<Long> foreignApps;
    private List<Long> foreignAssets;

    public List<byte[]> getAppArgs() {
        return appArgs;
    }

    public void setAppArgs(List<byte[]> appArgs) {
        this.appArgs = appArgs;
    }

    public byte[] getNote() {
        return note;
    }

    public void setNote(byte[] note) {
        this.note = note;
    }

    public byte[] getLease() {
        return lease;
    }

    public void setLease(byte[] lease) {
        this.lease = lease;
    }

    public BigInteger getFee() {
        return fee;
    }

    public void setFee(BigInteger fee) {
        this.fee = fee;
    }

    public BigInteger getFlatFee() {
        return flatFee;
    }

    public void setFlatFee(BigInteger flatFee) {
        this.flatFee = flatFee;
    }

    public List<Address> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Address> accounts) {
        this.accounts = accounts;
    }

    public List<Long> getForeignApps() {
        return foreignApps;
    }

    public void setForeignApps(List<Long> foreignApps) {
        this.foreignApps = foreignApps;
    }

    public List<Long> getForeignAssets() {
        return foreignAssets;
    }

    public void setForeignAssets(List<Long> foreignAssets) {
        this.foreignAssets = foreignAssets;
    }
}
