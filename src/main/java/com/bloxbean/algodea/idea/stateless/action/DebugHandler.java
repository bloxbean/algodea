package com.bloxbean.algodea.idea.stateless.action;

import com.algorand.algosdk.transaction.SignedTransaction;
import com.bloxbean.algodea.idea.core.exception.LocalSDKNotConfigured;
import com.bloxbean.algodea.idea.debugger.service.DebugResultListener;
import com.bloxbean.algodea.idea.debugger.service.DebugService;
import com.bloxbean.algodea.idea.stateless.model.LogicSigParams;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.IncorrectOperationException;

import java.io.File;

public class DebugHandler {

    public void startDebugger(Project project, AlgoConsole console, String sourcePath, String txnJson, LogicSigParams logicSigParams) {
        DebugService debugService = null;
        try {
            debugService = new DebugService(project);
        } catch (LocalSDKNotConfigured localSDKNotConfigured) {
            console.showErrorMessage("Local SDK is not configured.");
            return;
        }

        File txnFile = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            SignedTransaction signedTransaction = mapper.readValue(txnJson, SignedTransaction.class);

            JsonNode jsonNode = mapper.readTree(txnJson);
            ObjectNode txnObj = (ObjectNode) jsonNode.get("txn");
            txnObj.put("snd", signedTransaction.tx.sender.encodeAsString());
            txnObj.put("rcv", signedTransaction.tx.receiver.encodeAsString());

            txnFile = File.createTempFile("tealsrc", ".json");
            FileUtil.writeToFile(txnFile, jsonNode.toPrettyString());
            debugService.startDebugger(sourcePath, txnFile, new DebugListenerImpl(console));
        } catch (Exception e) {
            console.showErrorMessage("Error starting debugger", e);
        }

        //TODO delete file
        if(txnFile != null) {
            try {
                txnFile.delete();
            } catch (Exception e) {

            }
        }
    }

    class DebugListenerImpl implements DebugResultListener {
        private AlgoConsole console;

        public DebugListenerImpl(AlgoConsole console) {
            this.console = console;
        }

        @Override
        public void attachProcess(OSProcessHandler processHandler) {
            ApplicationManager.getApplication().invokeLater(() -> {
                try {
                    console.getView().attachToProcess(processHandler);
                } catch (IncorrectOperationException ex) {
                    //This should not happen
                    console.showInfoMessage(ex.getMessage());
                    console.dispose();
                    console.getView().attachToProcess(processHandler);
                }
                processHandler.startNotify();
            });
        }

        @Override
        public void error(String message) {
            console.showErrorMessage(message);
        }

        @Override
        public void info(String message) {
            console.showInfoMessage(message);
        }

        @Override
        public void warn(String msg) {
            console.showWarningMessage(msg);
        }
    }
}
