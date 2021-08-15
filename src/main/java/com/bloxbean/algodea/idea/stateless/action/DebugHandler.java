package com.bloxbean.algodea.idea.stateless.action;

import com.algorand.algosdk.transaction.SignedTransaction;
import com.bloxbean.algodea.idea.core.exception.LocalSDKNotConfigured;
import com.bloxbean.algodea.idea.debugger.service.DebugResultListener;
import com.bloxbean.algodea.idea.debugger.service.DebugService;
import com.bloxbean.algodea.idea.dryrun.util.DryRunJsonUtil;
import com.bloxbean.algodea.idea.stateless.model.LogicSigParams;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.IncorrectOperationException;

import java.io.File;

public class DebugHandler {
    ObjectMapper mapper;

    public DebugHandler() {
        mapper = new ObjectMapper();
    }

    public void startDebugger(Project project, AlgoConsole console, String sourcePath, SignedTransaction signedTransaction, LogicSigParams logicSigParams) {
        DebugService debugService = null;
        try {
            debugService = new DebugService(project);
        } catch (LocalSDKNotConfigured localSDKNotConfigured) {
            console.showErrorMessage("Local SDK is not configured. To use \"tealdbg\", you need to configure compilation target as Local SDK.");
            return;
        }

        File txnFile = null;
        try {
            txnFile = File.createTempFile("tealsrc", ".json");
            FileUtil.writeToFile(txnFile, DryRunJsonUtil.toJson(signedTransaction));
            debugService.startDebugger(sourcePath, txnFile, null, new DebugListenerImpl(console));
        } catch (Exception e) {
            console.showErrorMessage("Error starting debugger", e);
        }

        if(txnFile != null) {
            try {
                txnFile.delete();
            } catch (Exception e) {

            }
        }
    }

    public void startStatefulCallDebugger(Project project, String sourceFile, AlgoConsole console, String dryRunDumpJson) {
        DebugService debugService = null;
        try {
            debugService = new DebugService(project);
        } catch (LocalSDKNotConfigured localSDKNotConfigured) {
            console.showErrorMessage("Local SDK is not configured.");
            return;
        }

        File dryReqDumpFile = null;
        try {
            console.showInfoMessage(dryRunDumpJson);
            dryReqDumpFile = File.createTempFile("statefultx-dump", ".json");
            FileUtil.writeToFile(dryReqDumpFile, dryRunDumpJson);
            console.showInfoMessage(dryReqDumpFile.getAbsolutePath());
            debugService.startDebugger(sourceFile, null, dryReqDumpFile, new DebugListenerImpl(console));
        } catch (Exception e) {
            console.showErrorMessage("Error starting debugger", e);
        }

        if(dryReqDumpFile != null) {
            try {
                dryReqDumpFile.delete();
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
