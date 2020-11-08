package com.bloxbean.algodea.idea.nodeint.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.*;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.Asset;
import com.algorand.algosdk.v2.client.model.AssetResponse;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.AccountAsset;
import com.bloxbean.algodea.idea.nodeint.model.AssetTxnParameters;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.util.NetworkHelper;
import com.bloxbean.algodea.idea.util.AlgoConversionUtil;
import com.bloxbean.algodea.idea.util.JsonUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public class AssetTransactionService extends AlgoBaseService {

    public AssetTransactionService(Project project, LogListener logListener) throws DeploymentTargetNotConfigured {
        super(project, logListener);
    }

    public Result<Long> createAsset(Account sender, AssetTxnParameters assetTxnParameters, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        AssetCreateTransactionBuilder builder = Transaction.AssetCreateTransactionBuilder();

        populateAssetCreateTransaction(builder, assetTxnParameters);
        builder = (AssetCreateTransactionBuilder) populateBaseTransactionDetails(builder, sender.getAddress(), txnDetailsParameters);

        if (builder == null) {
            logListener.error("Transaction could not be built");
            return null;
        }

        Transaction txn = builder.build();

        SignedTransaction signTxn = signTransaction((transaction -> {
            return sender.signTransaction(transaction);
        }), txn);

        if (requestMode == null || requestMode.equals(RequestMode.TRANSACTION)) {
            PendingTransactionResponse transactionResponse = postTransaction(signTxn);

            if (transactionResponse != null) {
                if (NetworkHelper.getInstance().getExplorerBaseUrl(getNetworkGenesisHash()) != null) {
                    logListener.info("Check asset details here : "
                            + NetworkHelper.getInstance().getAssetUrl(getNetworkGenesisHash(), String.valueOf(transactionResponse.assetIndex)));
                }
            }

            if (transactionResponse == null) return Result.error();
            else
                return Result.success(JsonUtil.getPrettyJson(transactionResponse)).withValue(transactionResponse.assetIndex);
        } else if (requestMode.equals(RequestMode.EXPORT_SIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(signTxn));
        } else if(requestMode.equals(RequestMode.EXPORT_UNSIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(txn));
        } else {
            return Result.error("Invalid request mode : " + requestMode);
        }
    }

    public Result modifyAsset(Account sender, AssetTxnParameters finalAssetTxnPrameters, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
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
            return Result.error();
        }

        Transaction txn = builder.build();

        SignedTransaction signTxn = signTransaction((transaction -> {
            return sender.signTransaction(transaction);
        }), txn);

        return processAssetTransaction(finalAssetTxnPrameters, requestMode, txn, signTxn);
    }

    public Result optInAsset(Account sender, AssetTxnParameters finalAssetTxnPrameters, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        if(sender == null) {
            logListener.error("Sender cannot be null");
            return Result.error();
        }

        AssetAcceptTransactionBuilder builder = Transaction.AssetAcceptTransactionBuilder();

        builder = (AssetAcceptTransactionBuilder) populateBaseTransactionDetails(builder, sender.getAddress(), txnDetailsParameters);

        builder.assetIndex(finalAssetTxnPrameters.assetId)
                .acceptingAccount(sender.getAddress());

        if (builder == null) {
            logListener.error("Transaction could not be built");
            return Result.error();
        }

        Transaction txn = builder.build();

        SignedTransaction signTxn = signTransaction((transaction -> {
            return sender.signTransaction(transaction);
        }), txn);

        return processAssetTransaction(finalAssetTxnPrameters, requestMode, txn, signTxn);
    }

    public Result freezeAsset(Account sender, AssetTxnParameters finalAssetTxnPrameters, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        if(sender == null) {
            logListener.error("Sender cannot be null");
            return Result.error();
        }

        AssetFreezeTransactionBuilder builder = Transaction.AssetFreezeTransactionBuilder();

        builder = (AssetFreezeTransactionBuilder) populateBaseTransactionDetails(builder, sender.getAddress(), txnDetailsParameters);

        builder.assetIndex(finalAssetTxnPrameters.assetId)
                .freezeTarget(finalAssetTxnPrameters.freezeTarget)
                .freezeState(true);

        if (builder == null) {
            logListener.error("Transaction could not be built");
            return Result.error();
        }

        Transaction txn = builder.build();

        SignedTransaction signTxn = signTransaction((transaction -> {
            return sender.signTransaction(transaction);
        }), txn);

        return processAssetTransaction(finalAssetTxnPrameters, requestMode, txn, signTxn);
    }

    public Result unfreezeAsset(Account sender, AssetTxnParameters finalAssetTxnPrameters, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        if(sender == null) {
            logListener.error("Sender cannot be null");
            return Result.error();
        }

        AssetFreezeTransactionBuilder builder = Transaction.AssetFreezeTransactionBuilder();

        builder = (AssetFreezeTransactionBuilder) populateBaseTransactionDetails(builder, sender.getAddress(), txnDetailsParameters);

        builder.assetIndex(finalAssetTxnPrameters.assetId)
                .freezeTarget(finalAssetTxnPrameters.freezeTarget)
                .freezeState(false);

        if (builder == null) {
            logListener.error("Transaction could not be built");
            return Result.error();
        }

        Transaction txn = builder.build();

        SignedTransaction signTxn = signTransaction((transaction -> {
            return sender.signTransaction(transaction);
        }), txn);

        return processAssetTransaction(finalAssetTxnPrameters, requestMode, txn, signTxn);
    }

    public Result revokeAsset(Account sender, AssetTxnParameters finalAssetTxnPrameters, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        if(sender == null) {
            logListener.error("Sender cannot be null");
            return Result.error();
        }

        AssetClawbackTransactionBuilder builder = Transaction.AssetClawbackTransactionBuilder();

        builder = (AssetClawbackTransactionBuilder) populateBaseTransactionDetails(builder, sender.getAddress(), txnDetailsParameters);

        builder.assetIndex(finalAssetTxnPrameters.assetId)
                .assetClawbackFrom(finalAssetTxnPrameters.revokeAddress)
                .assetReceiver(finalAssetTxnPrameters.receiverAddress)
                .assetAmount(finalAssetTxnPrameters.assetAmount);

        if (builder == null) {
            logListener.error("Transaction could not be built");
            return Result.error();
        }

        Transaction txn = builder.build();

        SignedTransaction signTxn = signTransaction((transaction -> {
            return sender.signTransaction(transaction);
        }), txn);

        return processAssetTransaction(finalAssetTxnPrameters, requestMode, txn, signTxn);
    }

    public Result destroyAsset(Account sender, AssetTxnParameters finalAssetTxnPrameters, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        if(sender == null) {
            logListener.error("Sender cannot be null");
            return Result.error();
        }

        AssetDestroyTransactionBuilder builder = Transaction.AssetDestroyTransactionBuilder();

        builder = (AssetDestroyTransactionBuilder) populateBaseTransactionDetails(builder, sender.getAddress(), txnDetailsParameters);

        builder.assetIndex(finalAssetTxnPrameters.assetId);

        if (builder == null) {
            logListener.error("Transaction could not be built");
            return Result.error();
        }

        Transaction txn = builder.build();

        SignedTransaction signTxn = signTransaction((transaction -> {
            return sender.signTransaction(transaction);
        }), txn);

        return processAssetTransaction(finalAssetTxnPrameters, requestMode, txn, signTxn);
    }

    public Result assetTransfer(Account sender, String receiver, AccountAsset asset, BigInteger amount,
                                TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        if(sender == null) {
            logListener.error("Sender cannot be null");
            return Result.error();
        }

        if(StringUtil.isEmpty(receiver)) {
            logListener.error("Receiver cannot be null");
            return Result.error();
        }

        try {
            logListener.info("From Address     : " + sender.getAddress().toString());
            logListener.info("Receiver Address : " + receiver);
            logListener.info(String.format("Amount           : %s %s ( %d )\n",
                    AlgoConversionUtil.assetToDecimal(amount, asset.getDecimals()), asset.getAssetUnit(), amount));
        } catch (Exception e) {

        }

        AssetTransferTransactionBuilder builder = Transaction.AssetTransferTransactionBuilder();
        builder  = (AssetTransferTransactionBuilder) populateBaseTransactionDetails(builder, sender.getAddress(), txnDetailsParameters);

        builder.assetIndex(asset.getAssetId())
                .assetAmount(amount)
                .assetReceiver(receiver)
                .sender(sender.getAddress());

        if (builder == null) {
            logListener.error("Transaction could not be built");
            return Result.error();
        }

        Transaction txn = builder.build();

        if(txn == null) {
            logListener.error("Transaction could not be built");
            return Result.error();
        }

        SignedTransaction signedTransaction = signTransaction(sender, txn);

        if (requestMode == null || requestMode.equals(RequestMode.TRANSACTION)) {
            return postApplicationTransaction(sender, signedTransaction);
        } else if (requestMode.equals(RequestMode.EXPORT_SIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(signedTransaction));
        } else if(requestMode.equals(RequestMode.EXPORT_UNSIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(txn));
        } else {
            return Result.error("Invalid request mode : " + requestMode);
        }
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

    @NotNull
    private Result processAssetTransaction(AssetTxnParameters finalAssetTxnPrameters, RequestMode requestMode, Transaction txn, SignedTransaction signTxn) throws Exception {
        if (requestMode == null || requestMode.equals(RequestMode.TRANSACTION)) {
            PendingTransactionResponse transactionResponse = postTransaction(signTxn);

            if(transactionResponse != null) {
                if(NetworkHelper.getInstance().getExplorerBaseUrl(getNetworkGenesisHash()) != null) {
                    logListener.info("Check asset details here : "
                            + NetworkHelper.getInstance().getAssetUrl(getNetworkGenesisHash(), String.valueOf(finalAssetTxnPrameters.assetId)));
                }
            }

            if(transactionResponse == null) return Result.error();
            else
                return Result.success(transactionResponse.toString());
        } else if (requestMode.equals(RequestMode.EXPORT_SIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(signTxn));
        } else if(requestMode.equals(RequestMode.EXPORT_UNSIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(txn));
        } else {
            return Result.error("Invalid request mode : " + requestMode);
        }
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


