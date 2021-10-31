package com.bloxbean.algodea.idea.nodeint.model;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.Transaction;

import java.math.BigInteger;
import java.util.List;

public class TxnDetailsParameters {
    private List<byte[]> appArgs;
    private byte[] note;
    private byte[] lease;
    private BigInteger fee;
    private BigInteger flatFee;
    private BigInteger firstValid;
    private BigInteger lastValid;
    private Address closeRemainderTo;
    private Address rekey;

    private List<Address> accounts;
    private List<Long> foreignApps;
    private List<Long> foreignAssets;

    //Only used from create app operation.
    private Transaction.OnCompletion onCompletion;

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

    public BigInteger getFirstValid() {
        return firstValid;
    }

    public void setFirstValid(BigInteger firstValid) {
        this.firstValid = firstValid;
    }

    public BigInteger getLastValid() {
        return lastValid;
    }

    public void setLastValid(BigInteger lastValid) {
        this.lastValid = lastValid;
    }

    public Address getCloseRemainderTo() {
        return closeRemainderTo;
    }

    public void setCloseRemainderTo(Address closeRemainderTo) {
        this.closeRemainderTo = closeRemainderTo;
    }

    public Address getRekey() {
        return rekey;
    }

    public void setRekey(Address rekey) {
        this.rekey = rekey;
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

    public Transaction.OnCompletion getOnCompletion() {
        return onCompletion;
    }

    public void setOnCompletion(Transaction.OnCompletion onCompletion) {
        this.onCompletion = onCompletion;
    }
}
