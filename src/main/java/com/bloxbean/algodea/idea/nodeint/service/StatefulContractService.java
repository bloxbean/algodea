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
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.*;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.exception.ApiCallException;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.util.JsonUtil;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatefulContractService extends AlgoBaseService {
    public StatefulContractService(Project project, LogListener logListener) throws DeploymentTargetNotConfigured {
        super(project, logListener);
    }

    public Result<Long> createApp(String approvalProgram, String clearStateProgram, Account signer, Address sender,
                          int globalBytes, int globalInts, int localBytes, int localInts, long extraPages,
                          TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        if(signer == null && sender == null) {
            logListener.error("Creator account cannot be null");
            return null;
        }

        if(signer == null && !requestMode.equals(RequestMode.EXPORT_UNSIGNED)) {
            logListener.error("Creator account cannot be null. Please make sure the mnemonic is valid.");
            return Result.error();
        }

        // compile programs
        logListener.info("Compiling Approval Program ...");
        String approvalProgramBytes = compileProgram(approvalProgram.getBytes("UTF-8"))._1();
        if(approvalProgramBytes == null) {
            logListener.error("Approval Program compilation failed");
            return null;
        } else {
            logListener.info("Approval Program compiled successfully.");
        }

        logListener.info("Compiling Clear State Program ...");
        String clearProgramBytes = compileProgram(clearStateProgram.getBytes("UTF-8"))._1();

        if(clearProgramBytes == null) {
            logListener.error("Clear State Program compilation failed");
            return null;
        } else {
            logListener.info("Clear State Program compiled successfully.");
        }

        return _createApp(signer, sender, new TEALProgram(approvalProgramBytes), new TEALProgram(clearProgramBytes),
                globalInts, globalBytes, localInts, localBytes, extraPages, txnDetailsParameters, requestMode);
    }

    public Result updateApp(Long appId, Account signer, Address sender, String approvalProgram, String clearStateProgram
            , TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {

        if(signer == null && sender == null) {
            logListener.error("From account cannot be null");
            return null;
        }

        if(signer == null && !requestMode.equals(RequestMode.EXPORT_UNSIGNED)) {
            logListener.error("From account cannot be null. Please make sure the mnemonic is valid.");
            return Result.error();
        }

        // compile programs
        logListener.info("Compiling Approval Program ...");
        String approvalProgramBytes = compileProgram(approvalProgram.getBytes("UTF-8"))._1();
        if(approvalProgramBytes == null) {
            logListener.error("Approval Program compilation failed");
            return Result.error();
        } else {
            logListener.info("Approval Program compiled successfully.");
        }

        logListener.info("Compiling Clear State Program ...");
        String clearProgramBytes = compileProgram(clearStateProgram.getBytes("UTF-8"))._1();

        if(clearProgramBytes == null) {
            logListener.error("Clear State Program compilation failed");
            return Result.error();
        } else {
            logListener.info("Clear State Program compiled successfully.");
        }

        ApplicationUpdateTransactionBuilder txnBuilder = Transaction.ApplicationUpdateTransactionBuilder();
        txnBuilder.approvalProgram(new TEALProgram(approvalProgramBytes));
        txnBuilder.clearStateProgram(new TEALProgram(clearProgramBytes));

        Transaction txn = populateBaseAppTransaction( txnBuilder, appId, sender, txnDetailsParameters);
        if(txn == null) {
            logListener.error("Transaction could not be built");
            return Result.error();
        }

        SignedTransaction stxn = null;

        if(signer != null) { //For all modes except EXPORT_TXN
            stxn = signTransaction(signer, txn);
        }

        return processContractTransaction(signer, txn, stxn, requestMode);
    }

    public Result optIn(Long appId, Account signer, Address sender, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode)  throws Exception {

        ApplicationOptInTransactionBuilder txnBuilder = Transaction.ApplicationOptInTransactionBuilder();

        Transaction txn = populateBaseAppTransaction( txnBuilder, appId, sender, txnDetailsParameters);
        if(txn == null) {
            logListener.error("Transaction could not be built");
            return Result.error();
        }

        SignedTransaction stxn = null;
        if(signer != null) {
            stxn = signTransaction(signer, txn);
        }

        return processContractTransaction(signer, txn, stxn, requestMode);
    }

    public Result call(Long appId, Account signer, Address sender, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        ApplicationCallTransactionBuilder txnBuilder = Transaction.ApplicationCallTransactionBuilder();

        Transaction txn = populateBaseAppTransaction( txnBuilder, appId, sender, txnDetailsParameters);
        if(txn == null) {
            logListener.error("Transaction could not be built");
            return Result.error();
        }

        SignedTransaction stxn = null;
        if(signer != null) {
            stxn = signTransaction(signer, txn);
        }

        return processContractTransaction(signer, txn, stxn, requestMode);
    }

    public Result closeOut(Long appId, Account signer, Address sender, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        ApplicationCloseTransactionBuilder txnBuilder = Transaction.ApplicationCloseTransactionBuilder();

        Transaction txn = populateBaseAppTransaction( txnBuilder, appId, sender, txnDetailsParameters);
        if(txn == null) {
            logListener.error("Transaction could not be built");
            return Result.error();
        }

        SignedTransaction stxn = null;
        if(signer != null) {
            stxn = signTransaction(signer, txn);
        }

        return processContractTransaction(signer, txn, stxn, requestMode);
    }

    public Result clear(Long appId, Account signer, Address sender, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        ApplicationClearTransactionBuilder txnBuilder = Transaction.ApplicationClearTransactionBuilder();

        Transaction txn = populateBaseAppTransaction( txnBuilder, appId, sender, txnDetailsParameters);
        if(txn == null) {
            logListener.error("Transaction could not be built");
            return Result.error();
        }

        SignedTransaction stxn = null;
        if(signer != null) {
            stxn = signTransaction(signer, txn);
        }

        return processContractTransaction(signer, txn, stxn, requestMode);
    }

    public Result delete(Long appId, Account signer, Address sender, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception {
        ApplicationDeleteTransactionBuilder txnBuilder = Transaction.ApplicationDeleteTransactionBuilder();

        Transaction txn = populateBaseAppTransaction( txnBuilder, appId, sender, txnDetailsParameters);
        if(txn == null) {
            logListener.error("Transaction could not be built");
            return Result.error();
        }

        SignedTransaction stxn = null;
        if(signer != null) {
            stxn = signTransaction(signer, txn);
        }

        return processContractTransaction(signer, txn, stxn, requestMode);
    }

    public void readLocalState(Address account, Long appId) throws Exception {
        logListener.info("Fetching local state ...");
        Response<com.algorand.algosdk.v2.client.model.Account> acctResponse
                = client.AccountInformation(account).execute(getHeaders()._1(), getHeaders()._2());
        if(!acctResponse.isSuccessful()) {
            printErrorMessage("Reading local state failed", acctResponse);
            return;
        }
        List<ApplicationLocalState> applicationLocalState = acctResponse.body().appsLocalState;

        for (int i = 0; i < applicationLocalState.size(); i++) {
            if (applicationLocalState.get(i).id.equals(appId)) {
                logListener.info("User's application local state: " + JsonUtil.getPrettyJson(applicationLocalState.get(i).keyValue.toString()));
            }
        }
        logListener.info("--\n");
    }

    public void readGlobalState(Long appId) throws Exception {
        logListener.info("Fetching global state ...");
        Response<Application> acctResponse = client.GetApplicationByID(appId).execute(getHeaders()._1(), getHeaders()._2());
        if(!acctResponse.isSuccessful()) {
            printErrorMessage("Reading global state failed", acctResponse);
            return;
        }

        Application application = acctResponse.body();

        logListener.info("Global State: \n");
        logListener.info(JsonUtil.getPrettyJson(application.params.globalState.toString()));
        logListener.info("---\n");
    }

    public void getApplication(Long appId) throws Exception {
        logListener.info("Fetching application info ...");
        Response<Application> acctResponse = client.GetApplicationByID(appId).execute(getHeaders()._1(), getHeaders()._2());
        if(!acctResponse.isSuccessful()) {
            printErrorMessage("Reading application info failed", acctResponse);
            return;
        }

        Application application = acctResponse.body();

        logListener.info(JsonUtil.getPrettyJson(application));
        logListener.info("\n");
    }


    private Result<Long> _createApp(Account creator, Address sender, TEALProgram approvalProgramSource,
                          TEALProgram clearProgramSource, int globalInts, int globalBytes, int localInts, int localBytes,
                                    long extraPages, TxnDetailsParameters txnDetailsParameters, RequestMode requestMode)
            throws Exception {

        logListener.info("Getting node suggested transaction parameters ...");
        // get node suggested parameters
        Response<TransactionParametersResponse> transactionParametersResponse
                = client.TransactionParams().execute(getHeaders()._1(), getHeaders()._2());
        if (!transactionParametersResponse.isSuccessful()) {
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
                .localStateSchema(new StateSchema(localInts, localBytes))
                .extraPages(extraPages);
        //.build();

        Transaction txn = populateBaseAppTransaction(transactionBuilder, null, sender, txnDetailsParameters);

        //If export unsigned, return txn
        if (requestMode.equals(RequestMode.EXPORT_UNSIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(txn));
        }

        // sign transaction
        SignedTransaction signedTxn = creator.signTransaction(txn);
        logListener.info("Signed transaction with txid: " + signedTxn.transactionID);

        if (requestMode == null || requestMode.equals(RequestMode.TRANSACTION)) {
            PendingTransactionResponse transactionResponse = postTransaction(signedTxn);

            if (transactionResponse == null) return Result.error();
            else {
                logListener.info("Created new app-id: " + transactionResponse.applicationIndex);
                return Result.success(JsonUtil.getPrettyJson(transactionResponse)).withValue(transactionResponse.applicationIndex);
            }
        } else if (requestMode.equals(RequestMode.EXPORT_SIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(signedTxn));
        } else if (requestMode.equals(RequestMode.DRY_RUN)) {
            return processDryRun(signedTxn);
        } else {
            return Result.error("Invalid request mode : " + requestMode);
        }
        /**
        // send to network
        byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTxn);
        logListener.info(String.format("Posting transaction to the network (%s) ...", client.getHost()));

        Tuple<String[], String[]> headers = algoConnectionFactory.getHeadersForBinaryContent();

        Response<PostTransactionsResponse> postTransactionsResponse = client.RawTransaction().rawtxn(encodedTxBytes).execute(headers._1(), headers._2());
        if(!postTransactionsResponse.isSuccessful()) {
            printErrorMessage("Transaction could not be posted to the network", postTransactionsResponse);
            return null;
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
                logListener.info("Check transaction details here : "
                        + NetworkHelper.getInstance().getTxnHashUrl(getNetworkGenesisHash(), id));
        }

        PendingTransactionResponse pTrx = pendingTransactionResponse.body();
        Long appId = pTrx.applicationIndex;
        logListener.info("Created new app-id: " + appId);

        return appId;**/
    }

    public List<Application> getApplication(List<Long> appIds) throws Exception {
        if(appIds == null || appIds.size() == 0)
            return Collections.EMPTY_LIST;

        List<Application> applications = new ArrayList<>();
        for(Long appId: appIds) {
            Response<Application> acctResponse = client.GetApplicationByID(appId).execute(getHeaders()._1(), getHeaders()._2());
            if (!acctResponse.isSuccessful()) {
                printErrorMessage("Reading application info failed", acctResponse);
                throw new ApiCallException("Application state not found for app Id: " + appId);
            }

            Application application = acctResponse.body();
            applications.add(application);
        }

        return applications;
    }

    private Result processContractTransaction(Account fromAccount, Transaction txn, SignedTransaction stxn, RequestMode requestMode) throws Exception {
        if (requestMode == null || requestMode.equals(RequestMode.TRANSACTION)) {
            return postApplicationTransaction(fromAccount, stxn);
        } else if (requestMode.equals(RequestMode.EXPORT_SIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(stxn));
        } else if(requestMode.equals(RequestMode.EXPORT_UNSIGNED)) {
            return Result.success(JsonUtil.getPrettyJson(txn));
        } else if (requestMode.equals(RequestMode.DRY_RUN)) {
            return processDryRun(stxn);
        }  else {
            return Result.error("Invalid request mode : " + requestMode);
        }
    }

    @NotNull
    private Result processDryRun(SignedTransaction signTxn) throws Exception {
        List<SignedTransaction> stxns = new ArrayList<>();
        stxns.add(signTxn);

        DryrunResponse dryrunResponse = postStatefulDryRunTransaction(stxns);
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
