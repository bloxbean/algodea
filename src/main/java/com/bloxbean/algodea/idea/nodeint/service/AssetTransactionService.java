package com.bloxbean.algodea.idea.nodeint.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.AssetAcceptTransactionBuilder;
import com.algorand.algosdk.builder.transaction.AssetConfigureTransactionBuilder;
import com.algorand.algosdk.builder.transaction.AssetCreateTransactionBuilder;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.Asset;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.AssetTxnParameters;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.util.JsonUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;

public class AssetTransactionService extends AlgoBaseService {

    public AssetTransactionService(Project project, LogListener logListener) throws DeploymentTargetNotConfigured {
        super(project, logListener);
    }

    public Long createAsset(Account sender, AssetTxnParameters assetTxnParameters, TxnDetailsParameters txnDetailsParameters) throws Exception {
        AssetCreateTransactionBuilder builder = Transaction.AssetCreateTransactionBuilder();

        populateAssetCreateTransaction(builder, assetTxnParameters);
        builder = (AssetCreateTransactionBuilder) populateBaseTransactionDetails(builder, sender.getAddress(), txnDetailsParameters);

        if (builder == null) {
            logListener.error("Transaction could not be built");
            return null;
        }

        Transaction txn = builder.build();

        PendingTransactionResponse transactionResponse = postTransaction((transaction -> {
            return sender.signTransaction(transaction);
        }), txn);

        if(transactionResponse == null) return null;
        else
            return transactionResponse.assetIndex;
    }

    public boolean modifyAsset(Account sender, AssetTxnParameters finalAssetTxnPrameters, TxnDetailsParameters txnDetailsParameters) throws Exception {
        AssetConfigureTransactionBuilder builder = Transaction.AssetConfigureTransactionBuilder();

        builder.strictEmptyAddressChecking(false);
        builder.assetIndex(finalAssetTxnPrameters.assetId)
                .manager(finalAssetTxnPrameters.managerAddres)
                .reserve(finalAssetTxnPrameters.reserveAddress)
                .freeze(finalAssetTxnPrameters.freezeAddress)
                .clawback(finalAssetTxnPrameters.clawbackAddress);

        builder = (AssetConfigureTransactionBuilder) populateBaseTransactionDetails(builder, sender.getAddress(), txnDetailsParameters);

        if (builder == null) {
            logListener.error("Transaction could not be built");
            return false;
        }

        Transaction txn = builder.build();

        PendingTransactionResponse transactionResponse = postTransaction((transaction -> {
            return sender.signTransaction(transaction);
        }), txn);

        if(transactionResponse == null) return false;
        else
            return true;
    }

    public boolean optInAsset(Account sender, AssetTxnParameters finalAssetTxnPrameters, TxnDetailsParameters txnDetailsParameters) throws Exception {
        if(sender == null) {
            logListener.error("Sender cannot be null");
            return false;
        }

        AssetAcceptTransactionBuilder builder = Transaction.AssetAcceptTransactionBuilder();

        builder = (AssetAcceptTransactionBuilder) populateBaseTransactionDetails(builder, sender.getAddress(), txnDetailsParameters);

        builder.assetIndex(finalAssetTxnPrameters.assetId)
                .acceptingAccount(sender.getAddress());

        if (builder == null) {
            logListener.error("Transaction could not be built");
            return false;
        }

        Transaction txn = builder.build();

        PendingTransactionResponse transactionResponse = postTransaction((transaction -> {
            return sender.signTransaction(transaction);
        }), txn);

        if(transactionResponse == null) return false;
        else
            return true;
    }

    public Asset getAsset(Long assetId) throws Exception {
        if(assetId == null) {
            logListener.error("Asset id cannot be null");
            return null;
        }

        logListener.info("Fetching asset info ...");
        Response<Asset> assetResponse = client.GetAssetByID(assetId).execute();
        if(!assetResponse.isSuccessful()) {
            printErrorMessage("Reading asset info failed", assetResponse);
            return null;
        }

        Asset asset = assetResponse.body();

        logListener.info(JsonUtil.getPrettyJson(asset));
        logListener.info("\n");

        return asset;
    }

    private void populateAssetCreateTransaction(AssetCreateTransactionBuilder builder, AssetTxnParameters assetTxnParameters) {

        builder.assetTotal(assetTxnParameters.total);

        builder.assetDecimals(assetTxnParameters.decimal);

        builder.defaultFrozen(assetTxnParameters.defaultFrozen);

        if (!StringUtil.isEmpty(assetTxnParameters.unitName))
            builder.assetUnitName(assetTxnParameters.unitName);

        if (!StringUtil.isEmpty(assetTxnParameters.assetName))
            builder.assetName(assetTxnParameters.assetName);

        if (!StringUtil.isEmpty(assetTxnParameters.url))
            builder.url(assetTxnParameters.url);

        if (assetTxnParameters.metadataHash != null)
            builder.metadataHash(assetTxnParameters.metadataHash);

        if (assetTxnParameters.managerAddres != null) {
            builder.manager(assetTxnParameters.managerAddres);
        }

        if (assetTxnParameters.reserveAddress != null) {
            builder.reserve(assetTxnParameters.reserveAddress);
        }

        if (assetTxnParameters.freezeAddress != null) {
            builder.freeze(assetTxnParameters.freezeAddress);
        }

        if (assetTxnParameters.clawbackAddress != null) {
            builder.clawback(assetTxnParameters.clawbackAddress);
        }

    }

}


