package com.bloxbean.algodea.idea.codegen.model;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.AssetParams;

import java.math.BigInteger;

public class AssetParamsEx {
    private AssetParams assetParams;

    public AssetParamsEx(AssetParams assetParams) {
        this.assetParams = assetParams;
    }

    public BigInteger getAssetTotal() {
        return assetParams.assetTotal;
    }

    public Integer getAssetDecimals() {
        return assetParams.assetDecimals;
    }

    public boolean isAssetDefaultFrozen() {
        return assetParams.assetDefaultFrozen;
    }

    public String getAssetUnitName() {
        return assetParams.assetUnitName;
    }

    public String getAssetName() {
        return assetParams.assetName;
    }

    public String getUrl() {
        return assetParams.url;
    }

    public byte[] getMetadataHash() {
        return assetParams.metadataHash;
    }

    public String getAssetManager() {
        return toAddressString(assetParams.assetManager);
    }

    public String getAssetReserve() {
        return toAddressString(assetParams.assetReserve);
    }

    public String getAssetFreeze() {
        return toAddressString(assetParams.assetFreeze);
    }

    public String getAssetClawback() {
        return toAddressString(assetParams.assetClawback);
    }

    private String toAddressString(Address address) {
        try {
            if (address == null || address.encodeAsString().startsWith("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
                return null;
            } else {
                return address.encodeAsString();
            }
        } catch (Exception e) {
            return null;
        }
    }

}
