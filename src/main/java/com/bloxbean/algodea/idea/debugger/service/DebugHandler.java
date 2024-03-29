package com.bloxbean.algodea.idea.debugger.service;

import com.algorand.algosdk.transaction.SignedTransaction;
import com.bloxbean.algodea.idea.core.exception.LocalSDKNotConfigured;
import com.bloxbean.algodea.idea.dryrun.util.DryRunJsonUtil;
import com.bloxbean.algodea.idea.stateless.model.LogicSigParams;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.IncorrectOperationException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DebugHandler {
    ObjectMapper mapper;

    public DebugHandler() {
        mapper = new ObjectMapper();
    }

    public void startDebugger(Project project, AlgoConsole console, String sourcePath, SignedTransaction signedTransaction, LogicSigParams logicSigParams) {
        DebugService debugService = getDebugService(project);

        File txnFile = null;
        try {
            txnFile = File.createTempFile("tealsrc", ".json");
            FileUtil.writeToFile(txnFile, DryRunJsonUtil.toJson(signedTransaction));
            debugService.startDebugger(new String[] {sourcePath}, txnFile, null, new DebugListenerImpl(console));
        } catch (Exception e) {
            console.showErrorMessage("Error starting debugger", e);
        } catch (LocalSDKNotConfigured localSDKNotConfigured) {
            console.showErrorMessage("Local SDK is not configured. To use \"tealdbg\", you need to configure compilation target as Local SDK.");
            return;
        }

        if(txnFile != null) {
            try {
                txnFile.deleteOnExit();
            } catch (Exception e) {

            }
        }
    }

    //Used when directly run using dryrun dump file
    public void startDebuggerForDryDumpFile(Project project, File dryRunDumpFile, AlgoConsole console) {
        DebugService debugService = getDebugService(project);

        List<String> sourceFiles = new ArrayList<>();
        try {
            String[] sources = null;
            try {
                console.showInfoMessage("Extracting source from the dump file...");
                sources = DryRunJsonUtil.sources(dryRunDumpFile);
                for(String source: sources) {
                    File sourceFile = File.createTempFile("source", ".teal");
                    FileUtil.writeToFile(sourceFile, source);
                    sourceFiles.add(sourceFile.getAbsolutePath());
                }
            } catch (Exception e) {

            }

            debugService.startDebugger(sourceFiles.toArray(new String[0]), null, dryRunDumpFile, new DebugListenerImpl(console));
        } catch (Exception e) {
            console.showErrorMessage("Error starting debugger", e);
        } catch (LocalSDKNotConfigured localSDKNotConfigured) {
            console.showErrorMessage("Local SDK is not configured. To use \"tealdbg\", you need to configure compilation target as Local SDK.");
            return;
        }

        for(String file: sourceFiles) {
            try {
                new File(file).deleteOnExit();
            } catch (Exception e) {}
        }
    }

    public void startStatefulCallDebugger(Project project, String[] sourceFiles, AlgoConsole console, String dryRunDumpJson) {
        DebugService debugService = getDebugService(project);

        File dryReqDumpFile = null;
        try {
            console.showInfoMessage(dryRunDumpJson);
            dryReqDumpFile = File.createTempFile("statefultx-dump", ".json");
            FileUtil.writeToFile(dryReqDumpFile, dryRunDumpJson);
            console.showInfoMessage(dryReqDumpFile.getAbsolutePath());
            debugService.startDebugger(sourceFiles, null, dryReqDumpFile, new DebugListenerImpl(console));
        } catch (Exception e) {
            console.showErrorMessage("Error starting debugger", e);
        } catch (LocalSDKNotConfigured localSDKNotConfigured) {
            console.showErrorMessage("Local SDK is not configured. To use \"tealdbg\", you need to configure compilation target as Local SDK.");
            return;
        }

        if(dryReqDumpFile != null) {
            try {
                dryReqDumpFile.deleteOnExit();
            } catch (Exception e) {

            }
        }
    }

    private DebugService getDebugService(Project project) {
        if(project == null)
            return null;

        DebugService debugService = null;
        if(project != null) {
            debugService = ServiceManager.getService(project, DebugService.class);
        }
        return debugService;
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
