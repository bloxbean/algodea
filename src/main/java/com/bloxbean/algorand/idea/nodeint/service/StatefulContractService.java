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
package com.bloxbean.algorand.idea.nodeint.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.TEALProgram;
import com.algorand.algosdk.logic.StateSchema;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.*;
import com.bloxbean.algorand.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algorand.idea.nodeint.purestake.CustomAlgodClient;
import com.intellij.openapi.project.Project;

public class StatefulContractService extends AlgoBaseService {
    CustomAlgodClient client;

    public StatefulContractService(Project project, LogListener logListener) throws DeploymentTargetNotConfigured {
        super(project, logListener);
        client = getAlgodClient();
    }

    public Long createApp(String approvalProgram, String clearStateProgram, Account creator,
                          int globalBytes, int globalInts, int localBytes, int localInts) throws Exception {
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

        Long appId = _createApp(creator, new TEALProgram(approvalProgramBytes), new TEALProgram(clearProgramBytes), globalInts, globalBytes, localInts, localBytes);
        return appId;
    }

    private Long _createApp(Account creator, TEALProgram approvalProgramSource,
                          TEALProgram clearProgramSource, int globalInts, int globalBytes, int localInts, int localBytes)
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
        Transaction txn = Transaction.ApplicationCreateTransactionBuilder()
                .sender(sender)
                .suggestedParams(params)
                .approvalProgram(approvalProgramSource)
                .clearStateProgram(clearProgramSource)
                .globalStateSchema(new StateSchema(globalInts, globalBytes))
                .localStateSchema(new StateSchema(localInts, localBytes))
                .build();

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
