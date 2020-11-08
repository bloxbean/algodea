package com.bloxbean.algodea.idea.core.action.util;

import com.bloxbean.algodea.idea.common.AlgoConstants;
import com.bloxbean.algodea.idea.core.action.util.AlgoContractModuleHelper;
import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

import static com.bloxbean.algodea.idea.common.AlgoConstants.ALGO_TXN_FILE_EXT;

public class TransactionExporterUtil {
    public static boolean exportTransaction(Module module, String txnJson, String outputFileName, LogListener logListener) throws Exception {
        VirtualFile txnOutputFolder = AlgoContractModuleHelper.getTxnOutputFolder(module);

        String txnOutFileName = getOutputFileName(txnOutputFolder, outputFileName, ALGO_TXN_FILE_EXT, logListener);
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

    private static String getOutputFileName(VirtualFile txnOutputFolder, String outputFileName, String extension, LogListener logListener) {
        if(outputFileName != null && outputFileName.endsWith(extension))
            outputFileName = outputFileName.substring(0 - extension.length());

        final String finalOutputFileName = outputFileName;
        String txnOutFileName = finalOutputFileName;

        if(txnOutputFolder.findChild(txnOutFileName + extension) != null) {
            int ret = Messages.showYesNoCancelDialog(String.format("Already a file exists with file name %s. Do you want to overwrite?",
                    txnOutFileName + extension), "Export Transaction", "Overwrite", "Create New", "Cancel", AllIcons.General.QuestionDialog);

            if(ret == Messages.NO) {
                int i = 0;
                while(txnOutputFolder.findChild(txnOutFileName + extension) != null)
                    txnOutFileName = finalOutputFileName + "-" + ++i;
            } else if(ret == Messages.CANCEL) {
                logListener.warn("Export transaction was cancelled");
                return null;
            }
        }

        return txnOutFileName + extension;
    }
}
