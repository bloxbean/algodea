/*
 * Copyright (c) 2020 BloxBean Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bloxbean.algodea.idea.nodeint.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.builder.transaction.ApplicationBaseTransactionBuilder;
import com.algorand.algosdk.builder.transaction.TransactionBuilder;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.AlgodClient;
import com.algorand.algosdk.v2.client.common.IndexerClient;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.*;
import com.bloxbean.algodea.idea.common.Tuple;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.nodeint.AlgoConnectionFactory;
import com.bloxbean.algodea.idea.nodeint.AlgoServerConfigurationHelper;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.util.NetworkHelper;
import com.bloxbean.algodea.idea.util.JsonUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class AlgoBaseService {
    private final static Logger LOG = Logger.getInstance(AlgoBaseService.class);

    protected Project project;
    protected AlgoConnectionFactory algoConnectionFactory;
    protected LogListener logListener;
    protected AlgodClient client;
    protected IndexerClient indexerClient;
    protected String networkGenesisHash;

    public AlgoBaseService(Project project) throws DeploymentTargetNotConfigured {
        this(project, new LogListener() {
            @Override
            public void info(String msg) {
                LOG.info(msg);
            }

            @Override
            public void error(String msg) {
                LOG.warn(msg);
            }

            @Override
            public void warn(String msg) {
                LOG.warn(msg);
            }
        });
    }

    public AlgoBaseService(Project project, LogListener logListener) throws DeploymentTargetNotConfigured {
        NodeInfo nodeInfo = AlgoServerConfigurationHelper.getDeploymentNodeInfo(project);
        if(nodeInfo == null)
            throw new DeploymentTargetNotConfigured("No deployment node found");
        algoConnectionFactory
                = new AlgoConnectionFactory(nodeInfo.getNodeAPIUrl(), nodeInfo.getIndexerAPIUrl(), nodeInfo.getApiKey());
        this.logListener = logListener;

        this.client = algoConnectionFactory.connect();
        this.indexerClient = algoConnectionFactory.connectToIndexerApi();
        this.networkGenesisHash = nodeInfo.getGenesisHash();
    }

    //Pass your custom nodeInfo object. Required for compiler service
    public AlgoBaseService(@NotNull NodeInfo nodeInfo, LogListener logListener) {
        if(nodeInfo == null)
            throw new IllegalArgumentException("NodeInfo cannot be null");
        algoConnectionFactory
                = new AlgoConnectionFactory(nodeInfo.getNodeAPIUrl(), nodeInfo.getIndexerAPIUrl(), nodeInfo.getApiKey());
        this.logListener = logListener;
        this.client = algoConnectionFactory.connect();
        this.indexerClient = algoConnectionFactory.connectToIndexerApi();
        this.networkGenesisHash = nodeInfo.getGenesisHash();
    }

    public AlgodClient getAlgodClient() {
        return algoConnectionFactory.connect();
    }

    public void printErrorMessage(String message, Response response) {
        if(response == null) {
            logListener.error(message);
            logListener.error("Response : " + null);
        } else if(!response.isSuccessful()) {
            logListener.error(message);
            logListener.error("Failure code    : " + response.code());

            String failureMsg = JsonUtil.getPrettyJson(response.message());
            if(failureMsg == null)
                failureMsg = response.message();

            logListener.error("Failure message : " + failureMsg);
        }
    }

    public String compileProgram(byte[] programSource) {
        Response<CompileResponse> compileResponse = null;
        try {
            compileResponse = client.TealCompile().source(programSource).execute(getHeaders()._1(), getHeaders()._2());
        } catch (Exception e) {
            printErrorMessage("Compilation failed", compileResponse);
            logListener.error("Compilation error", e);
            return null;
        }

        if(!compileResponse.isSuccessful()) {
            printErrorMessage("Compilation failed", compileResponse);
            return null;
        } else {
            logListener.info("Compiled Data : " + compileResponse.body().result);
            logListener.info("Hash          : " + compileResponse.body().hash);
            return compileResponse.body().result;
        }
    }

    protected SignedTransaction signTransaction(Account fromAccount, Transaction txn) throws Exception {
        // sign transaction
        SignedTransaction signedTxn = fromAccount.signTransaction(txn);
        logListener.info("Signed transaction with txid: " + signedTxn.transactionID);
        return signedTxn;
    }

    protected Result postApplicationTransaction(Account fromAccount, SignedTransaction signedTxn) throws Exception {
        // send to network
        byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTxn);
        return postRawTransaction(encodedTxBytes);

    }

    @NotNull
    protected Result postRawTransaction(byte[] encodedTxBytes) throws Exception {
        logListener.info(String.format("Posting transaction to the network (%s) ...", client.getHost()));

        Tuple<String[], String[]> headers = algoConnectionFactory.getHeadersForBinaryContent();
        Response<PostTransactionsResponse> postTransactionsResponse = client.RawTransaction().rawtxn(encodedTxBytes).execute(headers._1(), headers._2());
        if(!postTransactionsResponse.isSuccessful()) {
            printErrorMessage("Transaction could not be posted to the network", postTransactionsResponse);
            return Result.error(postTransactionsResponse.message());
        }

        String id = postTransactionsResponse.body().txId;
        logListener.info("Successfully sent tx with ID: " + id);

        // await confirmation
        waitForConfirmation(id);

        // display results
        Response<PendingTransactionResponse> pendingTransactionResponse
                = client.PendingTransactionInformation(id).execute(getHeaders()._1(), getHeaders()._2());
        if(!pendingTransactionResponse.isSuccessful()) {
            printErrorMessage("Unable to get pending transaction info", pendingTransactionResponse);
            return Result.error();
        }

        if(pendingTransactionResponse.body() != null) {
            logListener.info("\nTransaction Info :-");
            logListener.info(JsonUtil.getPrettyJson(pendingTransactionResponse.body().toString()));

            if(NetworkHelper.getInstance().getExplorerBaseUrl(getNetworkGenesisHash()) != null)
                logListener.info("Check transaction details here : " + NetworkHelper.getInstance().getTxnHashUrl(getNetworkGenesisHash(), id));

            return Result.success(pendingTransactionResponse.body().toString());
        } else {
            return Result.success(null);
        }
    }

    protected  SignedTransaction signTransaction(TransactionSigner signer, Transaction txn) throws Exception {
        // sign transaction
        SignedTransaction signedTxn = signer.signTransaction(txn);
        logListener.info("Signed transaction with txid: " + signedTxn.transactionID);

        return signedTxn;
    }

    protected PendingTransactionResponse postTransaction(SignedTransaction signedTxn) throws Exception {
        // send to network
        byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTxn);
        logListener.info(String.format("Posting transaction to the network (%s) ...", client.getHost()));

        Tuple<String[], String[]> headers = algoConnectionFactory.getHeadersForBinaryContent();

        Response<PostTransactionsResponse> postTransactionsResponse = client.RawTransaction().rawtxn(encodedTxBytes).execute(headers._1(), headers._2());
        if(!postTransactionsResponse.isSuccessful()) {
            printErrorMessage("Transaction could not be posted to the network", postTransactionsResponse);
            return null  ;
        }

        String id = postTransactionsResponse.body().txId;
        logListener.info("Successfully sent tx with ID: " + id);

        // await confirmation
        waitForConfirmation(id);

        // display results
        Response<PendingTransactionResponse> pendingTransactionResponse
                = client.PendingTransactionInformation(id).execute(getHeaders()._1(), getHeaders()._2());
        if(!pendingTransactionResponse.isSuccessful()) {
            printErrorMessage("Unable to get pending transaction info", pendingTransactionResponse);
            return null;
        }

        if(pendingTransactionResponse.body() != null) {
            logListener.info("\nTransaction Info :-");
            logListener.info(JsonUtil.getPrettyJson(pendingTransactionResponse.body().toString()));

            if(NetworkHelper.getInstance().getExplorerBaseUrl(getNetworkGenesisHash()) != null)
                logListener.info("Check transaction details here : " + NetworkHelper.getInstance().getTxnHashUrl(getNetworkGenesisHash(), id));
        }

        return pendingTransactionResponse.body();
    }

    protected Transaction populateBaseAppTransaction(ApplicationBaseTransactionBuilder appTransactionBuilder, Long appId, Account fromAccount, TxnDetailsParameters txnDetailsParameters) throws Exception {
        if(fromAccount == null) {
            logListener.error("From Account cannot be null");
            return null;
        }

        if(appId != null) {
            logListener.info("Application : " + appId);
        }

        logListener.info("From Account : " + fromAccount.getAddress().toString());
        // define sender
        Address sender = fromAccount.getAddress();

        if(appId != null && appId != 0) {
            appTransactionBuilder.applicationId(appId);
        }

        List<byte[]> appArgs = txnDetailsParameters.getAppArgs();
        List<Address> accounts = txnDetailsParameters.getAccounts();
        List<Long> foreignApps = txnDetailsParameters.getForeignApps();
        List<Long> foreignAssets = txnDetailsParameters.getForeignAssets();

        if(appArgs != null && appArgs.size() > 0) {
            appTransactionBuilder.args(appArgs);
        }

        if(accounts != null && accounts.size() > 0)
            appTransactionBuilder.accounts(accounts);

        if(foreignApps != null && foreignApps.size() > 0)
            appTransactionBuilder.foreignApps(foreignApps);

        if(foreignAssets != null && foreignAssets.size() > 0)
            appTransactionBuilder.foreignAssets(foreignAssets);

        populateBaseTransactionDetails(appTransactionBuilder, sender, txnDetailsParameters);

        return appTransactionBuilder.build();
    }

    protected TransactionBuilder populateBaseTransactionDetails(TransactionBuilder transactionBuilder, Address sender, TxnDetailsParameters txnDetailsParameters) throws Exception {
        if(sender == null) {
            logListener.error("From Account cannot be null");
            return null;
        }

        logListener.info("Getting node suggested transaction parameters ...");
        // get node suggested parameters
        Response<TransactionParametersResponse> transactionParametersResponse
                = client.TransactionParams().execute(getHeaders()._1(), getHeaders()._2());
        if(!transactionParametersResponse.isSuccessful()) {
            printErrorMessage("Unable to get Transaction Params from the node", transactionParametersResponse);
            return null;
        }

        TransactionParametersResponse params = transactionParametersResponse.body();
        logListener.info("Got node suggested transaction parameters.");

        // create unsigned transaction
        transactionBuilder
                .sender(sender)
                .suggestedParams(params);

        byte[] note = txnDetailsParameters.getNote();
        byte[] lease = txnDetailsParameters.getLease();

        if(note != null) {
            transactionBuilder.note(note);
        }

        if(lease != null) {
            transactionBuilder.lease(lease);
        }

        if(txnDetailsParameters.getFee() != null) {
            transactionBuilder.fee(txnDetailsParameters.getFee());
        }

        if(txnDetailsParameters.getFlatFee() != null) {
            transactionBuilder.fee((BigInteger)null);
            transactionBuilder.flatFee(txnDetailsParameters.getFlatFee());
        }

        if(txnDetailsParameters.getFirstValid() != null) {
            transactionBuilder.firstValid(txnDetailsParameters.getFirstValid());
        }

        if(txnDetailsParameters.getLastValid() != null) {
            transactionBuilder.lastValid(txnDetailsParameters.getLastValid());
        }

        return transactionBuilder;
    }

    protected DryrunResponse postDryRunTransaction(List<SignedTransaction> stxns, List<byte[]> sourceBytes) throws Exception {

        List<DryrunSource> sources = new ArrayList<DryrunSource>();
//        List<SignedTransaction> stxns = new ArrayList<SignedTransaction>();
        // compiled
//        if (source == null) {
//            stxns.add(stxn);
//        }
        // source
        if (sourceBytes != null && sourceBytes.size() > 0) {
            long i = 0;
            for(byte[] sourceByte: sourceBytes) {
                DryrunSource drs = new DryrunSource();
                drs.fieldName = "lsig";
                drs.source = new String(sourceByte);
                drs.txnIndex = i;
                sources.add(drs);
                i++;
            }
        }
        Response<DryrunResponse> dryrunResponse;
        DryrunRequest dr = new DryrunRequest();
        dr.txns = stxns;
        dr.sources = sources;
//        dr.sources = sources;

        dryrunResponse = client.TealDryrun().request(dr).execute();

        if(!dryrunResponse.isSuccessful()) {
            printErrorMessage("Dry run failed", dryrunResponse);
            if(dryrunResponse.body() != null) {
                logListener.error("Error: " + dryrunResponse.body().error);
            }
            return dryrunResponse.body();
        }

        if(dryrunResponse.isSuccessful()) {
            logListener.info("Dry Run Result :");
            if(dryrunResponse.body() != null && dryrunResponse.body().txns != null) {
                for(DryrunTxnResult dryrunTxnResult:dryrunResponse.body().txns)
                    logListener.info(JsonUtil.getPrettyJson(dryrunTxnResult));
            }
        } else {
            logListener.info("Dry Run error :");
            logListener.error(dryrunResponse.body().error);
        }

        return dryrunResponse.body();
    }

    protected void waitForConfirmation(String txID) throws Exception {
        Response<NodeStatusResponse> response = client.GetStatus().execute(getHeaders()._1(), getHeaders()._2());
        if(!response.isSuccessful()) {
            printErrorMessage("Failed to get transaction status for txId :" + txID, response);
        }

        Long lastRound = response.body().lastRound;
        while (true) {
            try {
                // Check the pending transactions
                Response<PendingTransactionResponse> pendingInfo
                        = client.PendingTransactionInformation(txID).execute(getHeaders()._1(), getHeaders()._2());
                if (pendingInfo.body().confirmedRound != null && pendingInfo.body().confirmedRound > 0) {
                    // Got the completed Transaction
                    logListener.info(
                            "Transaction " + txID + " confirmed in round " + pendingInfo.body().confirmedRound);
                    break;
                }
                lastRound++;
                client.WaitForBlock(lastRound).execute(getHeaders()._1(), getHeaders()._2());
            } catch (Exception e) {
                throw (e);
            }
        }
    }

    public TransactionParametersResponse getSuggestedTxnParams() throws Exception {
        logListener.info("Getting node suggested transaction parameters ...");
        // get node suggested parameters
        Response<TransactionParametersResponse> transactionParametersResponse
                = client.TransactionParams().execute(getHeaders()._1(), getHeaders()._2());
        if(!transactionParametersResponse.isSuccessful()) {
            printErrorMessage("Unable to get Transaction Params from the node", transactionParametersResponse);
            return null;
        }

        TransactionParametersResponse params = transactionParametersResponse.body();
        logListener.info("Got node suggested transaction parameters.");
        return params;
    }

    public String getNetworkGenesisHash() {
        return networkGenesisHash;
    }

    protected Tuple<String[], String[]> getHeaders() {
        return algoConnectionFactory.getHeaders();
    }

    protected Tuple<String[], String[]> getHeadersForBinaryContent() {
        return algoConnectionFactory.getHeadersForBinaryContent();
    }
}
