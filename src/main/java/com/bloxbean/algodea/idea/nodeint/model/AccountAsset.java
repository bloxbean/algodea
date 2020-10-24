package com.bloxbean.algodea.idea.nodeint.model;

import com.bloxbean.algodea.idea.util.AlgoConversionUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;

public class AccountAsset {
    private Long assetId;
    private BigInteger amount;
    private long decimals;
    private String assetName;
    private String assetUnit;

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetUnit() {
        return assetUnit;
    }

    public void setAssetUnit(String assetUnit) {
        this.assetUnit = assetUnit;
    }

    public long getDecimals() {
        return decimals;
    }

    public void setDecimals(long decimals) {
        this.decimals = decimals;
    }

    public String toString() {
        String result = "";
        result += assetId;
        result += "(" + assetName +")";

        if(amount != null) {
            String formattedAmount = AlgoConversionUtil.toAssetDecimalAmtFormatted(amount, (int)decimals);
            result += " Balance: " + formattedAmount + " " + assetUnit;
        }

        return result;
    }
}
