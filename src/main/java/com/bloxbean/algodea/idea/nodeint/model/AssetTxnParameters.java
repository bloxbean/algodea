package com.bloxbean.algodea.idea.nodeint.model;

import com.algorand.algosdk.crypto.Address;

import java.math.BigInteger;

public class AssetTxnParameters {
    public Long assetId;
    public BigInteger total;
    public int decimal;
    public boolean defaultFrozen;
    public String unitName;
    public String assetName;
    public String url;
    public byte[] metadataHash;
    public Address creator;
    public Address managerAddres;
    public Address reserveAddress;
    public Address freezeAddress;
    public Address clawbackAddress;

    public Address freezeTarget;
    public boolean freezeState; //Needed for freeze or unfreeze transaction.

    public Address revokeAddress; //Needed for revoke transaction
    public Address receiverAddress;
    public BigInteger assetAmount;
}
