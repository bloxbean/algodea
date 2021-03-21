package com.bloxbean.algodea.idea.nodeint.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.PaymentTransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.model.DryrunResponse;
import com.algorand.algosdk.v2.client.model.DryrunTxnResult;
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.AccountAsset;
import com.bloxbean.algodea.idea.nodeint.model.AssetTxnParameters;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.util.JsonUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class LogicSigTransactionService extends TransactionService {

    private AssetTransactionService assetTransactionService;

    public LogicSigTransactionService(Project project, LogListener logListener) throws DeploymentTargetNotConfigured {
        super(project, logListener);
        this.assetTransactionService = new AssetTransactionService(project, logListener);
    }

    public Result logicSigTransaction(String lsigPath, Address sender, Address receiver, AccountAsset asset, BigInteger amount, Address closeReminderTo, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode)
            throws Exception {

        byte[] logicSigBytes = FileUtil.loadFileBytes(new File(lsigPath));

        return logicSigTransaction(logicSigBytes, sender, receiver, asset, amount, closeReminderTo, txnDetailsParameters, requestMode);
    }

    public Result logicSigTransaction(byte[] logicSigBytes, Address sender, Address receiver, AccountAsset asset, BigInteger amount, Address closeReminderTo, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        byte[] sourceBytes = null;

//        if(RequestMode.DRY_RUN.equals(requestMode)) {
//            sourceBytes = LogicSigUtil.readSourceBytesIfAvailable(lsigPath);
//        }

        if(sender == null) {
            if(asset == null) { //Algo
                return contractAccountTransaction(logicSigBytes, receiver, amount, closeReminderTo, txnDetailsParameters, sourceBytes, requestMode);
            } else { //Asset transfer
                LogicsigSignature logicsigSignature = Encoder.decodeFromMsgPack(logicSigBytes, LogicsigSignature.class);
                Address fromAddress = logicsigSignature.toAddress();

                return assetTransactionService.logicSigAssetTransfer(fromAddress, receiver.toString(), asset, amount, closeReminderTo, txnDetailsParameters, sourceBytes, requestMode, new TransactionSigner() {
                    @Override
                    public SignedTransaction signTransaction(Transaction transaction) throws Exception {
                        return Account.signLogicsigTransaction(logicsigSignature, transaction);
                    }
                });
            }
        } else {
            if(asset == null) { //Algo
                return accountDelegationTransaction(logicSigBytes, sender, receiver, amount, closeReminderTo, txnDetailsParameters, sourceBytes, requestMode);
            } else { // Asset transfer
                LogicsigSignature logicsigSignature = Encoder.decodeFromMsgPack(logicSigBytes, LogicsigSignature.class);

                return assetTransactionService.logicSigAssetTransfer(sender, receiver.toString(), asset, amount, closeReminderTo, txnDetailsParameters, sourceBytes, requestMode, new TransactionSigner() {
                    @Override
                    public SignedTransaction signTransaction(Transaction transaction) throws Exception {
                        return Account.signLogicsigTransaction(logicsigSignature, transaction);
                    }
                });
            }
        }
    }

    public Result logicSigOptInAssetTransaction(String lsigPath, Address sender, AssetTxnParameters assetTxnPrameters,
                                                TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {

        byte[] logicSigBytes = FileUtil.loadFileBytes(new File(lsigPath));

        return logicSigOptInAssetTransaction(logicSigBytes, sender, assetTxnPrameters, txnDetailsParameters, requestMode);

    }

    public Result logicSigOptInAssetTransaction(byte[] logicSigBytes, Address sender, AssetTxnParameters assetTxnPrameters, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        byte[] sourceBytes = null;

        if(sender == null) {
                LogicsigSignature logicsigSignature = Encoder.decodeFromMsgPack(logicSigBytes, LogicsigSignature.class);
                Address fromAddress = logicsigSignature.toAddress();

                return assetTransactionService.logicSigOptInAsset(fromAddress, assetTxnPrameters, txnDetailsParameters, sourceBytes, requestMode, new TransactionSigner() {
                    @Override
                    public SignedTransaction signTransaction(Transaction transaction) throws Exception {
                        return Account.signLogicsigTransaction(logicsigSignature, transaction);
                    }
                });
        } else {
                LogicsigSignature logicsigSignature = Encoder.decodeFromMsgPack(logicSigBytes, LogicsigSignature.class);

                return assetTransactionService.logicSigOptInAsset(sender, assetTxnPrameters, txnDetailsParameters, sourceBytes, requestMode, new TransactionSigner() {
                    @Override
                    public SignedTransaction signTransaction(Transaction transaction) throws Exception {
                        return Account.signLogicsigTransaction(logicsigSignature, transaction);
                    }
                });
        }
    }

    public Result contractAccountTransaction(byte[] logicSigBytes, Address receiver, BigInteger amount, Address closeReminderTo,
                                             TxnDetailsParameters txnDetailsParameters, byte[] sourceBytes, RequestMode requestMode) throws Exception {

        if (receiver == null) {
            logListener.error("Receiver account cannot be null");
            return Result.error();
        }

        if (amount == null) {
            logListener.error("Amount cannot be null");
            return Result.error();
        }

        LogicsigSignature logicsigSignature = Encoder.decodeFromMsgPack(logicSigBytes, LogicsigSignature.class);
        Address fromAddress = logicsigSignature.toAddress();

        logListener.info("Starting Contract Account transaction ...\n");

        logListener.info("From Contract Address     : " + fromAddress.toString());

        PaymentTransactionBuilder paymentTransactionBuilder = Transaction.PaymentTransactionBuilder();
        Transaction txn = populatePaymentTransaction(paymentTransactionBuilder, fromAddress, receiver.toString(), amount.longValue(), closeReminderTo, txnDetailsParameters);

        if (txn == null) {
            logListener.error("Transaction could not be built");
            return Result.error();
        }

        TransactionSigner txnSigner = new TransactionSigner() {
            @Override
            public SignedTransaction signTransaction(Transaction transaction) throws Exception {
                return Account.signLogicsigTransaction(logicsigSignature, transaction);
            }
        };

        SignedTransaction signTxn = signTransaction(txnSigner, txn);

        if (requestMode == null || requestMode.equals(RequestMode.TRANSACTION)) {
            PendingTransactionResponse txnResponse = postTransaction(signTxn);
            return txnResponse != null ? Result.success(txnResponse.toString()) : Result.error();
        } else if (requestMode.equals(RequestMode.EXPORT_SIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(signTxn));
        } else if (requestMode.equals(RequestMode.EXPORT_UNSIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(txn));
        } else if (requestMode.equals(RequestMode.DRY_RUN)) {
            return processDryRun(signTxn, sourceBytes);
        } else {
            return Result.error("Invalid request mode : " + requestMode);
        }

    }

    public Result accountDelegationTransaction(byte[] logicSigBytes, Address sender, Address receiver, BigInteger amount, Address closeReminderTo,
                                                TxnDetailsParameters txnDetailsParameters, byte[] sourceBytes, RequestMode requestMode) throws Exception {
        if(sender == null) {
            logListener.error("Sender address cannot be empty");
            return Result.error();
        }

        if(receiver == null) {
            logListener.error("Receiver address cannot be empty");
            return Result.error();
        }

        if(amount == null) {
            logListener.error("Amount cannot be null");
            return Result.error();
        }

        LogicsigSignature logicsigSignature = Encoder.decodeFromMsgPack(logicSigBytes, LogicsigSignature.class);

        logListener.info("Starting Account Delegation transaction ...\n");

        PaymentTransactionBuilder paymentTransactionBuilder = Transaction.PaymentTransactionBuilder();
        Transaction txn = populatePaymentTransaction(paymentTransactionBuilder, sender, receiver.toString(), amount.longValue(), closeReminderTo, txnDetailsParameters);

        if(txn == null) {
            logListener.error("Transaction could not be built");
            return Result.error();
        }

        TransactionSigner txnSigner = new TransactionSigner() {
            @Override
            public SignedTransaction signTransaction(Transaction transaction) throws Exception {
                return Account.signLogicsigTransaction(logicsigSignature, txn);
            }
        };

        SignedTransaction signTxn = signTransaction(txnSigner, txn);

        if (requestMode == null || requestMode.equals(RequestMode.TRANSACTION)) {
            PendingTransactionResponse txnResponse = postTransaction(signTxn);
            return txnResponse != null? Result.success(txnResponse.toString()): Result.error();
        } else if (requestMode.equals(RequestMode.EXPORT_SIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(signTxn));
        } else if(requestMode.equals(RequestMode.EXPORT_UNSIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(txn));
        } else if (requestMode.equals(RequestMode.DRY_RUN)) {
            return processDryRun(signTxn, sourceBytes);

        } else {
            return Result.error("Invalid request mode : " + requestMode);
        }
    }

    @NotNull
    private Result processDryRun(SignedTransaction signTxn, byte[] sourceBytes) throws Exception {
        List<SignedTransaction> stxns = new ArrayList<>();
        List<byte[]> sources = null;
        stxns.add(signTxn);

        if(sourceBytes != null) {
            sources = new ArrayList<>();
            sources.add(sourceBytes);
        }

        DryrunResponse dryrunResponse = postDryRunTransaction(stxns, sources);
        if(dryrunResponse == null) {
            return Result.error("Dry run failed");
        } else {
            List<DryrunTxnResult> dryrunTxnResults = dryrunResponse.txns;
            if(dryrunTxnResults == null || dryrunTxnResults.size() == 0)
                return Result.error("Dry run failed");

            return Result.success(JsonUtil.getPrettyJson(dryrunTxnResults.get(0)));
        }
    }

}
