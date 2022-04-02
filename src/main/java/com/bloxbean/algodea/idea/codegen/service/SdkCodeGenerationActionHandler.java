package com.bloxbean.algodea.idea.codegen.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.bloxbean.algodea.idea.codegen.CodeGenLang;
import com.bloxbean.algodea.idea.codegen.service.detector.TypeDetectorFactory;
import com.bloxbean.algodea.idea.codegen.service.exception.CodeGenerationException;
import com.bloxbean.algodea.idea.configuration.action.ConfigurationAction;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.nodeint.AlgoServerConfigurationHelper;
import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

import java.io.IOException;
import java.util.List;

public class SdkCodeGenerationActionHandler {
    private final static Logger LOG = Logger.getInstance(SdkCodeGenerationActionHandler.class);

    private Project project;
    private Module module;

    public SdkCodeGenerationActionHandler(Project project, Module module) {
        this.project = project;
        this.module = module;
    }

    public void handleCodeGeneration(String txnJson, Account signerAccount, LogListener logListener) {
//        AlgoConsole console = AlgoConsole.getConsole(project);
//        console.clearAndshow();

        SdkCodeGeneratorFactory sdkCodeGeneratorFactory = project.getService(SdkCodeGeneratorFactory.class);
        if (sdkCodeGeneratorFactory == null) {
            logListener.error("Unexpected error. SdkCodeGeneratorFactory not found");
            return;
        }

        try {

            final NodeInfo deploymentNodeInfo = AlgoServerConfigurationHelper.getDeploymentNodeInfo(project);
            if (deploymentNodeInfo == null) {
                IdeaUtil.showNotification(project, "Compilation configuration",
                        "Algorand Node configuration is not done for this module. " +
                                "Please select a deployment node.", NotificationType.ERROR, ConfigurationAction.ACTION_ID);
                return;
            }

            SignedTransaction signedTransaction = loadSignedTransaction(txnJson);

            Transaction txn;
            if (signedTransaction == null) {
                txn = loadTransaction(txnJson);
            } else {
                txn = signedTransaction.tx;
            }

//            String content = JsonUtil.getPrettyJson(txn);
//
//            CodeGenTxnDetailsDialog dialog = new CodeGenTxnDetailsDialog(project, txn, content);
//            boolean ok = dialog.showAndGet();
//            CodeGenLang lang = dialog.getSelectedLang();
            CodeGenLang lang = CodeGenLang.JS;
            SdkCodeGenerator sdkCodeGenerator = sdkCodeGeneratorFactory.getSdkCodeGenerator(lang);
            TxnType txnType = TypeDetectorFactory.INSTANCE.deletectType(txn);

//            if (!ok || (ok && lang == null)) {
//                IdeaUtil.showNotification(project, "Code Generation", "Code Generation was cancelled", NotificationType.WARNING, null);
//                return;
//            }

//            if (sdkCodeGenerator == null) {
//                logListener.error("No code generator found for lang : " + lang);
//                return;
//            }


//             Task.Backgroundable task = new Task.Backgroundable(project, "Code generation") {
//
//                @Override
//                public void run(@NotNull ProgressIndicator indicator) {
                    logListener.info("Generating code ....");
                    logListener.info("Language: " + lang.toString().toLowerCase());

                    List<FileContent> genereatedContents;
                    try {
                        genereatedContents = sdkCodeGenerator.generateCode(txn, txnType, signerAccount , deploymentNodeInfo, null, logListener);
                    } catch (Exception exception) {
                        logListener.error("Error generating code", exception);
                        return;
                    }

                    if (genereatedContents == null || genereatedContents.size() == 0) {
                        logListener.info("No file generated");
                        return;
                    }

                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            String targetFolder = SdkCodeGenExportUtil.getSdkCodeGenerationFolder(project, module, lang, logListener);
                            try {
                                SdkCodeGenExportUtil.writeGeneratedCodeFile(genereatedContents, targetFolder, logListener);
                            } catch (CodeGenerationException codeGenerationException) {
                                logListener.error("Error writing generating file to disk", codeGenerationException);
                            }
                        }
                    });
//                }
//            };

//            ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));
//        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
//            //deploymentTargetNotConfigured.printStackTrace();
//            warnDeploymentTargetNotConfigured(project, getTitle());
//        }
            } catch (Exception ex) {
                if (LOG.isDebugEnabled()) {
                    LOG.error(ex);
                }
                logListener.error(ex.getMessage());
                IdeaUtil.showNotification(project, getTitle(), String.format("Code generation failed, reason: %s", ex.getMessage()), NotificationType.ERROR, null);
            }
    }

    private String getTitle() {
        return "Code Generation";
    }

    private Transaction loadTransaction(String txnJson) throws IOException {
        Transaction transaction = Encoder.decodeFromJson(txnJson, Transaction.class);
        return transaction;
    }

    private SignedTransaction loadSignedTransaction(String txnJson) {
        try {
            SignedTransaction transaction = Encoder.decodeFromJson(txnJson, SignedTransaction.class);
            return transaction;
        } catch (Exception e) {
            return null;
        }
    }

//    private void warnDeploymentTargetNotConfigured(Project project, String actionTitle) {
//        IdeaUtil.showNotification(project, actionTitle, "Algorand Node for deployment node is not configured. Click here to configure.",
//                NotificationType.ERROR, ConfigurationAction.ACTION_ID);
//    }
}
