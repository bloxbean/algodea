package com.bloxbean.algodea.idea.core.action.util;

import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

import static com.bloxbean.algodea.idea.common.AlgoConstants.ALGO_DRY_RUN_FILE_EXT;
import static com.bloxbean.algodea.idea.common.AlgoConstants.ALGO_TXN_FILE_EXT;

public class ExporterUtil {
    public static boolean exportTransaction(Module module, String txnJson, String outputFileName, LogListener logListener) throws Exception {
        VirtualFile txnOutputFolder = AlgoContractModuleHelper.getTxnOutputFolder(module);

        String txnOutFileName = getOutputFileName(txnOutputFolder, outputFileName, ALGO_TXN_FILE_EXT, "Export Transaction", logListener);
        if(StringUtil.isEmpty(txnOutFileName))
            return false;

        ApplicationManager.getApplication().runWriteAction(()  -> {
            VirtualFile outputFile = null;
            try {
                outputFile = txnOutputFolder.findOrCreateChildData(module, txnOutFileName);
                outputFile.setBinaryContent(txnJson.getBytes("UTF-8"));
                logListener.info("Transaction data has been written to " + outputFile.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
                logListener.error("Export transaction failed", e);
            }
        });

        return true;
    }

    public static boolean exportDryRunResponse(Module module, String dryRunResult, String outputFileName, LogListener logListener) throws Exception {
        VirtualFile dryRunOutputFolder = AlgoContractModuleHelper.getDryRunOutputFolder(module);

        String txnOutFileName = getOutputFileName(dryRunOutputFolder, outputFileName, ALGO_DRY_RUN_FILE_EXT, "Dry run", logListener);
        if(StringUtil.isEmpty(txnOutFileName))
            return false;

        ApplicationManager.getApplication().runWriteAction(()  -> {
            VirtualFile outputFile = null;
            try {
                outputFile = dryRunOutputFolder.findOrCreateChildData(module, txnOutFileName);
                outputFile.setBinaryContent(dryRunResult.getBytes("UTF-8"));
                logListener.info("Dry run result has been written to " + outputFile.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
                logListener.error("Dry run export failed", e);
            }
        });

        return true;
    }

    public static boolean exportDryRunAccounts(Module module, String accounts, String outputFileName, LogListener logListener) throws Exception {
        VirtualFile dryRunOutputFolder = AlgoContractModuleHelper.getDryRunOutputFolder(module);

        String accountsOutputFie = getOutputFileName(dryRunOutputFolder, outputFileName, ".json", "Export accounts", logListener);
        if(StringUtil.isEmpty(accountsOutputFie))
            return false;

        ApplicationManager.getApplication().runWriteAction(()  -> {
            VirtualFile outputFile = null;
            try {
                outputFile = dryRunOutputFolder.findOrCreateChildData(module, accountsOutputFie);
                outputFile.setBinaryContent(accounts.getBytes("UTF-8"));
                logListener.info("Exported accounts to " + outputFile.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
                logListener.error("Export accounts failed", e);
            }
        });

        return true;
    }

    public static boolean exportDryRunApplications(Module module, String accounts, String outputFileName, LogListener logListener) throws Exception {
        VirtualFile dryRunOutputFolder = AlgoContractModuleHelper.getDryRunOutputFolder(module);

        String applicationsOutputFile = getOutputFileName(dryRunOutputFolder, outputFileName, ".json", "Export applications", logListener);
        if(StringUtil.isEmpty(applicationsOutputFile))
            return false;

        ApplicationManager.getApplication().runWriteAction(()  -> {
            VirtualFile outputFile = null;
            try {
                outputFile = dryRunOutputFolder.findOrCreateChildData(module, applicationsOutputFile);
                outputFile.setBinaryContent(accounts.getBytes("UTF-8"));
                logListener.info("Exported applications to " + outputFile.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
                logListener.error("Export applications failed", e);
            }
        });

        return true;
    }

    private static String getOutputFileName(VirtualFile txnOutputFolder, String outputFileName, String extension, String command, LogListener logListener) {
        if(outputFileName != null && outputFileName.endsWith(extension))
            outputFileName = outputFileName.substring(0 - extension.length());

        final String finalOutputFileName = outputFileName;
        String txnOutFileName = finalOutputFileName;

        if(txnOutputFolder.findChild(txnOutFileName + extension) != null) {
            int ret = Messages.showYesNoCancelDialog(String.format("Already a file exists with file name %s. Do you want to overwrite?",
                    txnOutFileName + extension), command, "Overwrite", "Create New", "Cancel", AllIcons.General.QuestionDialog);

            if(ret == Messages.NO) {
                int i = 0;
                while(txnOutputFolder.findChild(txnOutFileName + extension) != null)
                    txnOutFileName = finalOutputFileName + "-" + ++i;
            } else if(ret == Messages.CANCEL) {
                logListener.warn(command + " was cancelled");
                return null;
            }
        }

        return txnOutFileName + extension;
    }
}
