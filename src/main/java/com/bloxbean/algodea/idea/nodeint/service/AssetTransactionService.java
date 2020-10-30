package com.bloxbean.algodea.idea.nodeint.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.*;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.indexer.LookupAssetByID;
import com.algorand.algosdk.v2.client.model.Asset;
import com.algorand.algosdk.v2.client.model.AssetResponse;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.AccountAsset;
import com.bloxbean.algodea.idea.nodeint.model.AssetTxnParameters;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.util.NetworkHelper;
import com.bloxbean.algodea.idea.util.AlgoConversionUtil;
import com.bloxbean.algodea.idea.util.JsonUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;

import java.math.BigInteger;

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

        if(transactionResponse != null) {
            if(NetworkHelper.getInstance().getExplorerBaseUrl(getNetworkGenesisHash()) != null) {
                logListener.info("Check asset details here : "
                        + NetworkHelper.getInstance().getAssetUrl(getNetworkGenesisHash(), String.valueOf(transactionResponse.assetIndex)));
            }
        }

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

        if(transactionResponse != null) {
            if(NetworkHelper.getInstance().getExplorerBaseUrl(getNetworkGenesisHash()) != null) {
                logListener.info("Check asset details here : "
                        + NetworkHelper.getInstance().getAssetUrl(getNetworkGenesisHash(), String.valueOf(finalAssetTxnPrameters.assetId)));
            }
        }

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

        if(transactionResponse != null) {
            if(NetworkHelper.getInstance().getExplorerBaseUrl(getNetworkGenesisHash()) != null) {
                logListener.info("Check asset details here : "
                        + NetworkHelper.getInstance().getAssetUrl(getNetworkGenesisHash(), String.valueOf(finalAssetTxnPrameters.assetId)));
            }
        }

        if(transactionResponse == null) return false;
        else
            return true;
    }

    public boolean freezeAsset(Account sender, AssetTxnParameters finalAssetTxnPrameters, TxnDetailsParameters txnDetailsParameters) throws Exception {
        if(sender == null) {
            logListener.error("Sender cannot be null");
            return false;
        }

        AssetFreezeTransactionBuilder builder = Transaction.AssetFreezeTransactionBuilder();

        builder = (AssetFreezeTransactionBuilder) populateBaseTransactionDetails(builder, sender.getAddress(), txnDetailsParameters);

        builder.assetIndex(finalAssetTxnPrameters.assetId)
                .freezeTarget(finalAssetTxnPrameters.freezeTarget)
                .freezeState(true);

        if (builder == null) {
            logListener.error("Transaction could not be built");
            return false;
        }

        Transaction txn = builder.build();

        PendingTransactionResponse transactionResponse = postTransaction((transaction -> {
            return sender.signTransaction(transaction);
        }), txn);

        if(transactionResponse != null) {
            if(NetworkHelper.getInstance().getExplorerBaseUrl(getNetworkGenesisHash()) != null) {
                logListener.info("Check asset details here : "
                        + NetworkHelper.getInstance().getAssetUrl(getNetworkGenesisHash(), String.valueOf(finalAssetTxnPrameters.assetId)));
            }
        }

        if(transactionResponse == null) return false;
        else
            return true;
    }

    public boolean unfreezeAsset(Account sender, AssetTxnParameters finalAssetTxnPrameters, TxnDetailsParameters txnDetailsParameters) throws Exception {
        if(sender == null) {
            logListener.error("Sender cannot be null");
            return false;
        }

        AssetFreezeTransactionBuilder builder = Transaction.AssetFreezeTransactionBuilder();

        builder = (AssetFreezeTransactionBuilder) populateBaseTransactionDetails(builder, sender.getAddress(), txnDetailsParameters);

        builder.assetIndex(finalAssetTxnPrameters.assetId)
                .freezeTarget(finalAssetTxnPrameters.freezeTarget)
                .freezeState(false);

        if (builder == null) {
            logListener.error("Transaction could not be built");
            return false;
        }

        Transaction txn = builder.build();

        PendingTransactionResponse transactionResponse = postTransaction((transaction -> {
            return sender.signTransaction(transaction);
        }), txn);

        if(transactionResponse != null) {
            if(NetworkHelper.getInstance().getExplorerBaseUrl(getNetworkGenesisHash()) != null) {
                logListener.info("Check asset details here : "
                        + NetworkHelper.getInstance().getAssetUrl(getNetworkGenesisHash(), String.valueOf(finalAssetTxnPrameters.assetId)));
            }
        }

        if(transactionResponse == null) return false;
        else
            return true;
    }

    public boolean revokeAsset(Account sender, AssetTxnParameters finalAssetTxnPrameters, TxnDetailsParameters txnDetailsParameters) throws Exception {
        if(sender == null) {
            logListener.error("Sender cannot be null");
            return false;
        }

        AssetClawbackTransactionBuilder builder = Transaction.AssetClawbackTransactionBuilder();

        builder = (AssetClawbackTransactionBuilder) populateBaseTransactionDetails(builder, sender.getAddress(), txnDetailsParameters);

        builder.assetIndex(finalAssetTxnPrameters.assetId)
                .assetClawbackFrom(finalAssetTxnPrameters.revokeAddress)
                .assetReceiver(finalAssetTxnPrameters.receiverAddress)
                .assetAmount(finalAssetTxnPrameters.assetAmount);

        if (builder == null) {
            logListener.error("Transaction could not be built");
            return false;
        }

        Transaction txn = builder.build();

        PendingTransactionResponse transactionResponse = postTransaction((transaction -> {
            return sender.signTransaction(transaction);
        }), txn);

        if(transactionResponse != null) {
            if(NetworkHelper.getInstance().getExplorerBaseUrl(getNetworkGenesisHash()) != null) {
                logListener.info("Check asset details here : "
                        + NetworkHelper.getInstance().getAssetUrl(getNetworkGenesisHash(), String.valueOf(finalAssetTxnPrameters.assetId)));
            }
        }

        if(transactionResponse == null) return false;
        else
            return true;
    }

    public boolean destroyAsset(Account sender, AssetTxnParameters finalAssetTxnPrameters, TxnDetailsParameters txnDetailsParameters) throws Exception {
        if(sender == null) {
            logListener.error("Sender cannot be null");
            return false;
        }

        AssetDestroyTransactionBuilder builder = Transaction.AssetDestroyTransactionBuilder();

        builder = (AssetDestroyTransactionBuilder) populateBaseTransactionDetails(builder, sender.getAddress(), txnDetailsParameters);

        builder.assetIndex(finalAssetTxnPrameters.assetId);

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

    public boolean assetTransfer(Account sender, String receiver, AccountAsset asset, BigInteger amount, TxnDetailsParameters txnDetailsParameters) throws Exception {
        if(sender == null) {
            logListener.error("Sender cannot be null");
            return false;
        }

        if(StringUtil.isEmpty(receiver)) {
            logListener.error("Receiver cannot be null");
            return false;
        }

        try {
            logListener.info("From Address     : " + sender.getAddress().toString());
            logListener.info("Receiver Address : " + receiver);
            logListener.info(String.format("Amount           : %s %s ( %d )\n",
                    AlgoConversionUtil.assetToDecimal(amount, asset.getDecimals()), asset.getAssetUnit(), amount));
        } catch (Exception e) {
            e.printStackTrace();
        }


        AssetTransferTransactionBuilder builder = Transaction.AssetTransferTransactionBuilder();
        builder  = (AssetTransferTransactionBuilder) populateBaseTransactionDetails(builder, sender.getAddress(), txnDetailsParameters);

        builder.assetIndex(asset.getAssetId())
                .assetAmount(amount)
                .assetReceiver(receiver)
                .sender(sender.getAddress());

        if (builder == null) {
            logListener.error("Transaction could not be built");
            return false;
        }

        Transaction txn = builder.build();

        if(txn == null) {
            logListener.error("Transaction could not be built");
            return false;
        }

        return postApplicationTransaction(sender, txn);
    }

    public Asset getAsset(Long assetId) throws Exception {
        if(assetId == null) {
            logListener.error("Asset id cannot be null");
            return null;
        }

        logListener.info("Fetching asset info ...");
        Asset asset = null;
        if(indexerClient == null) {
            Response<Asset> assetResponse = client.GetAssetByID(assetId).execute(getHeaders()._1(), getHeaders()._2());
            if (!assetResponse.isSuccessful()) {
                printErrorMessage("Reading asset info failed", assetResponse);
                return null;
            }

            asset = assetResponse.body();

            logListener.info(JsonUtil.getPrettyJson(asset));
            logListener.info("\n");
        } else {
            logListener.info("Fetching asset detail from Indexer Url ...");
            Response<AssetResponse> response = indexerClient.lookupAssetByID(assetId).execute(getHeaders()._1(), getHeaders()._2());
            if(!response.isSuccessful()) {
                printErrorMessage("Reading asset info failed", response);
                return null;
            }

            AssetResponse assetResponse = response.body();
            asset = assetResponse.asset;

            logListener.info(JsonUtil.getPrettyJson(asset));
            logListener.info("\n");
        }

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


