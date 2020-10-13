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
import com.algorand.algosdk.builder.transaction.*;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.*;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.purestake.CustomAlgodClient;
import com.bloxbean.algodea.idea.util.JsonUtil;
import com.intellij.openapi.project.Project;

import java.util.List;

public class StatefulContractService extends AlgoBaseService {
    public StatefulContractService(Project project, LogListener logListener) throws DeploymentTargetNotConfigured {
        super(project, logListener);
    }

    public Long createApp(String approvalProgram, String clearStateProgram, Account creator,
                          int globalBytes, int globalInts, int localBytes, int localInts,
                          TxnDetailsParameters txnDetailsParameters) throws Exception {
        if(creator == null) {
            logListener.error("Creator account cannot be null");
            return null;
        }

        // compile programs
        logListener.info("Compiling Approval Program ...");
        String approvalProgramBytes = compileProgram(client, approvalProgram.getBytes("UTF-8"));
        if(approvalProgramBytes == null) {
            logListener.error("Approval Program compilation failed");
            return null;
        } else {
            logListener.info("Approval Program compiled successfully.");
        }

        logListener.info("Compiling Clear State Program ...");
        String clearProgramBytes = compileProgram(client, clearStateProgram.getBytes("UTF-8"));

        if(clearProgramBytes == null) {
            logListener.error("Clear State Program compilation failed");
            return null;
        } else {
            logListener.info("Clear State Program compiled successfully.");
        }

        Long appId = _createApp(creator, new TEALProgram(approvalProgramBytes), new TEALProgram(clearProgramBytes),
                globalInts, globalBytes, localInts, localBytes, txnDetailsParameters);
        return appId;
    }

    public boolean updateApp(Long appId, Account fromAccount, String approvalProgram, String clearStateProgram, TxnDetailsParameters txnDetailsParameters) throws Exception {
        if(fromAccount == null) {
            logListener.error("From account cannot be null");
            return false;
        }

        // compile programs
        logListener.info("Compiling Approval Program ...");
        String approvalProgramBytes = compileProgram(client, approvalProgram.getBytes("UTF-8"));
        if(approvalProgramBytes == null) {
            logListener.error("Approval Program compilation failed");
            return false;
        } else {
            logListener.info("Approval Program compiled successfully.");
        }

        logListener.info("Compiling Clear State Program ...");
        String clearProgramBytes = compileProgram(client, clearStateProgram.getBytes("UTF-8"));

        if(clearProgramBytes == null) {
            logListener.error("Clear State Program compilation failed");
            return false;
        } else {
            logListener.info("Clear State Program compiled successfully.");
        }

        ApplicationUpdateTransactionBuilder txnBuilder = Transaction.ApplicationUpdateTransactionBuilder();
        txnBuilder.approvalProgram(new TEALProgram(approvalProgramBytes));
        txnBuilder.clearStateProgram(new TEALProgram(clearProgramBytes));

        Transaction txn = populateBaseTransaction( txnBuilder, appId, fromAccount, txnDetailsParameters);
        if(txn == null) {
            logListener.error("Transaction could not be built");
            return false;
        }

        return postApplicationTransaction(fromAccount, txn);
    }

    public boolean optIn(Long appId, Account fromAccount, TxnDetailsParameters txnDetailsParameters)  throws Exception {

        ApplicationOptInTransactionBuilder txnBuilder = Transaction.ApplicationOptInTransactionBuilder();

        Transaction txn = populateBaseTransaction( txnBuilder, appId, fromAccount, txnDetailsParameters);
        if(txn == null) {
            logListener.error("Transaction could not be built");
            return false;
        }

        return postApplicationTransaction(fromAccount, txn);
    }

    public boolean call(Long appId, Account fromAccount, TxnDetailsParameters txnDetailsParameters) throws Exception {
        ApplicationCallTransactionBuilder txnBuilder = Transaction.ApplicationCallTransactionBuilder();

        Transaction txn = populateBaseTransaction( txnBuilder, appId, fromAccount, txnDetailsParameters);
        if(txn == null) {
            logListener.error("Transaction could not be built");
            return false;
        }

        return postApplicationTransaction(fromAccount, txn);
    }

    public boolean closeOut(Long appId, Account fromAccount, TxnDetailsParameters txnDetailsParameters) throws Exception {
        ApplicationCloseTransactionBuilder txnBuilder = Transaction.ApplicationCloseTransactionBuilder();

        Transaction txn = populateBaseTransaction( txnBuilder, appId, fromAccount, txnDetailsParameters);
        if(txn == null) {
            logListener.error("Transaction could not be built");
            return false;
        }

        return postApplicationTransaction(fromAccount, txn);
    }

    public boolean clear(Long appId, Account fromAccount, TxnDetailsParameters txnDetailsParameters) throws Exception {
        ApplicationClearTransactionBuilder txnBuilder = Transaction.ApplicationClearTransactionBuilder();

        Transaction txn = populateBaseTransaction( txnBuilder, appId, fromAccount, txnDetailsParameters);
        if(txn == null) {
            logListener.error("Transaction could not be built");
            return false;
        }

        return postApplicationTransaction(fromAccount, txn);
    }

    public boolean delete(Long appId, Account fromAccount, TxnDetailsParameters txnDetailsParameters) throws Exception {
        ApplicationDeleteTransactionBuilder txnBuilder = Transaction.ApplicationDeleteTransactionBuilder();

        Transaction txn = populateBaseTransaction( txnBuilder, appId, fromAccount, txnDetailsParameters);
        if(txn == null) {
            logListener.error("Transaction could not be built");
            return false;
        }

        return postApplicationTransaction(fromAccount, txn);
    }

    private boolean postApplicationTransaction(Account fromAccount, Transaction txn) throws Exception {
        // sign transaction
        SignedTransaction signedTxn = fromAccount.signTransaction(txn);
        logListener.info("Signed transaction with txid: " + signedTxn.transactionID);

        // send to network
        byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTxn);
        logListener.info("Posting transaction to the network ...");
        Response<PostTransactionsResponse> postTransactionsResponse = client.RawTransaction().rawtxn(encodedTxBytes).execute();
        if(!postTransactionsResponse.isSuccessful()) {
            printErrorMessage("Transaction could not be posted to the network", postTransactionsResponse);
            return false;
        }

        String id = postTransactionsResponse.body().txId;
        logListener.info("Successfully sent tx with ID: " + id);

        // await confirmation
        waitForConfirmation(id);

        // display results
        Response<PendingTransactionResponse> pendingTransactionResponse = client.PendingTransactionInformation(id).execute();
        if(!pendingTransactionResponse.isSuccessful()) {
            printErrorMessage("Unable to get pending transaction info", pendingTransactionResponse);
            return false;
        }

        if(pendingTransactionResponse.body() != null) {
            logListener.info("\nTransaction Info :-");
            logListener.info(JsonUtil.getPrettyJson(pendingTransactionResponse.body().toString()));
        }

        return true;
    }

    public Transaction populateBaseTransaction(ApplicationBaseTransactionBuilder appTransactionBuilder, Long appId, Account fromAccount, TxnDetailsParameters txnDetailsParameters) throws Exception {
        if(fromAccount == null) {
            logListener.error("From Account cannot be null");
            return null;
        }

//        if(appId == null || appId == 0) {
//            logListener.error("Invalid application id");
//            return null;
//        }

        if(appId != null) {
            logListener.info("Application : " + appId);
        }

        logListener.info("From Account : " + fromAccount.getAddress().toString());
        // define sender
        Address sender = fromAccount.getAddress();

        logListener.info("Getting node suggested transaction parameters ...");
        // get node suggested parameters
        Response<TransactionParametersResponse> transactionParametersResponse = client.TransactionParams().execute();
        if(!transactionParametersResponse.isSuccessful()) {
            printErrorMessage("Unable to get Transaction Params from the node", transactionParametersResponse);
            return null;
        }

        TransactionParametersResponse params = transactionParametersResponse.body();
        logListener.info("Got node suggested transaction parameters.");

        logListener.info("Signing transaction ...");
        // create unsigned transaction
        appTransactionBuilder
                .sender(sender)
                .suggestedParams(params);

        if(appId != null && appId != 0) {
            appTransactionBuilder.applicationId(appId);
        }

        List<byte[]> appArgs = txnDetailsParameters.getAppArgs();
        byte[] note = txnDetailsParameters.getNote();
        byte[] lease = txnDetailsParameters.getLease();
        List<Address> accounts = txnDetailsParameters.getAccounts();
        List<Long> foreignApps = txnDetailsParameters.getForeignApps();
        List<Long> foreignAssets = txnDetailsParameters.getForeignAssets();

        if(appArgs != null && appArgs.size() > 0) {
            appTransactionBuilder.args(appArgs);
        }

        if(note != null) {
            appTransactionBuilder.note(note);
        }

        if(lease != null) {
            appTransactionBuilder.lease(lease);
        }

        if(accounts != null && accounts.size() > 0)
            appTransactionBuilder.accounts(accounts);

        if(foreignApps != null && foreignApps.size() > 0)
            appTransactionBuilder.foreignApps(foreignApps);

        if(foreignAssets != null && foreignAssets.size() > 0)
            appTransactionBuilder.foreignAssets(foreignAssets);

        return appTransactionBuilder.build();
    }

    private Long _createApp(Account creator, TEALProgram approvalProgramSource,
                          TEALProgram clearProgramSource, int globalInts, int globalBytes, int localInts, int localBytes, TxnDetailsParameters txnDetailsParameters)
            throws Exception {
        // define sender as creator
        Address sender = creator.getAddress();

        logListener.info("Getting node suggested transaction parameters ...");
        // get node suggested parameters
        Response<TransactionParametersResponse> transactionParametersResponse = client.TransactionParams().execute();
        if(!transactionParametersResponse.isSuccessful()) {
            printErrorMessage("Unable to get Transaction Params from the node", transactionParametersResponse);
            return null;
        }

        TransactionParametersResponse params = transactionParametersResponse.body();
        logListener.info("Got node suggested transaction parameters.");

        logListener.info("Signing transaction ...");
        // create unsigned transaction
//        ApplicationCreateTransactionBuilderTransaction.ApplicationCreateTransactionBuilder();
        ApplicationCreateTransactionBuilder transactionBuilder = Transaction.ApplicationCreateTransactionBuilder()
                .sender(sender)
                .suggestedParams(params)
                .approvalProgram(approvalProgramSource)
                .clearStateProgram(clearProgramSource)
                .globalStateSchema(new StateSchema(globalInts, globalBytes))
                .localStateSchema(new StateSchema(localInts, localBytes));
                //.build();

        Transaction txn = populateBaseTransaction(transactionBuilder, null, creator, txnDetailsParameters);

        // sign transaction
        SignedTransaction signedTxn = creator.signTransaction(txn);
        logListener.info("Signed transaction with txid: " + signedTxn.transactionID);

        // send to network
        byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTxn);
        logListener.info("Posting transaction to the network ...");
        Response<PostTransactionsResponse> postTransactionsResponse = client.RawTransaction().rawtxn(encodedTxBytes).execute();
        if(!postTransactionsResponse.isSuccessful()) {
            printErrorMessage("Transaction could not be posted to the network", postTransactionsResponse);
            return null;
        }

        String id = postTransactionsResponse.body().txId;
        logListener.info("Successfully sent tx with ID: " + id);

        // await confirmation
        waitForConfirmation(id);

        // display results
        Response<PendingTransactionResponse> pendingTransactionResponse = client.PendingTransactionInformation(id).execute();
        if(!pendingTransactionResponse.isSuccessful()) {
            printErrorMessage("Unable to get pending transaction info", pendingTransactionResponse);
            return null;
        }

        if(pendingTransactionResponse.body() != null) {
            logListener.info("\nTransaction Info :-");
            logListener.info(JsonUtil.getPrettyJson(pendingTransactionResponse.body().toString()));
        }

        PendingTransactionResponse pTrx = pendingTransactionResponse.body();
        Long appId = pTrx.applicationIndex;
        logListener.info("Created new app-id: " + appId);

        return appId;
    }

    public String compileProgram(CustomAlgodClient client, byte[] programSource) {
        Response<CompileResponse> compileResponse = null;
        try {
            compileResponse = client.TealCompile().source(programSource).execute();
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
            return compileResponse.body().result;
        }
    }

    public void waitForConfirmation(String txID) throws Exception {
//        if (client == null)
//            this.client = connectToNetwork();
        Response<NodeStatusResponse> response = client.GetStatus().execute();
        if(!response.isSuccessful()) {
            printErrorMessage("Failed to get transaction status for txId :" + txID, response);
        }

        Long lastRound = response.body().lastRound;
        while (true) {
            try {
                // Check the pending transactions
                Response<PendingTransactionResponse> pendingInfo = client.PendingTransactionInformation(txID).execute();
                if (pendingInfo.body().confirmedRound != null && pendingInfo.body().confirmedRound > 0) {
                    // Got the completed Transaction
                    logListener.info(
                            "Transaction " + txID + " confirmed in round " + pendingInfo.body().confirmedRound);
                    break;
                }
                lastRound++;
                client.WaitForBlock(lastRound).execute();
            } catch (Exception e) {
                throw (e);
            }
        }
    }
}
