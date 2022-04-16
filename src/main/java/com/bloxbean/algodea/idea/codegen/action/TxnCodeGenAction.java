package com.bloxbean.algodea.idea.codegen.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.transaction.SignedTransaction;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountService;
import com.bloxbean.algodea.idea.atomic.ui.SigningAccountInputDialog;
import com.bloxbean.algodea.idea.codegen.CodeGenLang;
import com.bloxbean.algodea.idea.codegen.model.CodeGenInfo;
import com.bloxbean.algodea.idea.codegen.service.*;
import com.bloxbean.algodea.idea.codegen.service.detector.TypeDetectorFactory;
import com.bloxbean.algodea.idea.codegen.service.exception.CodeGenerationException;
import com.bloxbean.algodea.idea.codegen.service.util.FileContent;
import com.bloxbean.algodea.idea.codegen.service.util.SdkCodeGenExportUtil;
import com.bloxbean.algodea.idea.codegen.service.util.TxnType;
import com.bloxbean.algodea.idea.codegen.ui.CodeGenTxnDetailsDialog;
import com.bloxbean.algodea.idea.common.AlgoIcons;
import com.bloxbean.algodea.idea.common.Tuple;
import com.bloxbean.algodea.idea.configuration.action.ConfigurationAction;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.core.action.BaseTxnAction;
import com.bloxbean.algodea.idea.nodeint.AlgoServerConfigurationHelper;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.bloxbean.algodea.idea.util.JsonUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TxnCodeGenAction extends BaseTxnAction {
    private final static Logger LOG = Logger.getInstance(TxnCodeGenAction.class);
    private final static String EXTENSION = "tx.json";

    public TxnCodeGenAction() {
        super(AlgoIcons.LOGIC_SIG_RUN_ICON);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        if (isAlgoProject(e)) {
            VirtualFile file = e.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);
            if (file.getName().endsWith(EXTENSION)) {
                e.getPresentation().setEnabledAndVisible(true);
            } else {
                e.getPresentation().setEnabledAndVisible(false);
            }
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            return;

        final Module module = LangDataKeys.MODULE.getData(e.getDataContext());

        AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();

        SdkCodeGeneratorFactory sdkCodeGeneratorFactory = project.getService(SdkCodeGeneratorFactory.class);
        if (sdkCodeGeneratorFactory == null) {
            console.showErrorMessage("Unexpected error. SdkCodeGeneratorFactory not found");
            return;
        }

        try {

            PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
            if (psiFile == null) {
                console.showErrorMessage("Unable read txn file");
                return;
            }

            //Build txn object
            VirtualFile txnVirtualFile = psiFile.getVirtualFile();
            if (txnVirtualFile == null || !txnVirtualFile.exists()) {
                console.showErrorMessage("Unable read txn file");
                return;
            }

            final NodeInfo deploymentNodeInfo = AlgoServerConfigurationHelper.getDeploymentNodeInfo(project);
            if (deploymentNodeInfo == null) {
                IdeaUtil.showNotification(project, "Compilation configuration",
                        "Algorand Node configuration is not done for this module. " +
                                "Please select a deployment node.", NotificationType.ERROR, ConfigurationAction.ACTION_ID);
                return;
            }

            File txnFile = VfsUtil.virtualToIoFile(txnVirtualFile);
            SignedTransaction signedTransaction = loadSignedTransaction(txnFile);

            Transaction txn;
            if (signedTransaction == null) {
                txn = loadTransaction(txnFile);
            } else {
                txn = signedTransaction.tx;
            }

            String content = JsonUtil.getPrettyJson(txn);

            CodeGenTxnDetailsDialog dialog = new CodeGenTxnDetailsDialog(project, txn, content);
            boolean ok = dialog.showAndGet();
            CodeGenLang lang = dialog.getSelectedLang();
            SdkCodeGenerator sdkCodeGenerator = sdkCodeGeneratorFactory.getSdkCodeGenerator(lang);
            TxnType txnType = TypeDetectorFactory.INSTANCE.deletectType(signedTransaction, txn);

            if (!ok || (ok && lang == null)) {
                IdeaUtil.showNotification(project, "Code Generation", "Code Generation was cancelled", NotificationType.WARNING, null);
                return;
            }

            if (sdkCodeGenerator == null) {
                console.showErrorMessage("No code generator found for lang : " + lang);
                return;
            }

            Tuple<SignedTransaction, Account> tuple = signTransaction(project, module, txn);
            signedTransaction = tuple._1();
            Account signer = tuple._2();
            if (signedTransaction == null) {
                console.showErrorMessage("Transaction signing failed.");
                return;
            }

            LogListener logListener = new LogListenerAdapter(console);

            Task.Backgroundable task = new Task.Backgroundable(project, getTxnCommand()) {

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    console.showInfoMessage("Generating code ....");
                    console.showInfoMessage("Language: " + lang.toString().toLowerCase());

                    List<FileContent> genereatedContents;
                    try {
                        CodeGenInfo codeGenInfo = new CodeGenInfo();
                        codeGenInfo.setTealFile(txnVirtualFile.getCanonicalPath());
                        genereatedContents = sdkCodeGenerator.generateCode(txn, txnType, signer , deploymentNodeInfo, codeGenInfo, txnVirtualFile.getNameWithoutExtension(), logListener);
                    } catch (Exception exception) {
                        console.showErrorMessage("Error generating code", exception);
                        return;
                    }

                    if (genereatedContents == null || genereatedContents.size() == 0) {
                        console.showInfoMessage("No file generated");
                        return;
                    }

                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            String targetFolder = SdkCodeGenExportUtil.getSdkCodeGenerationFolder(project, module, lang, logListener);
                            try {
                                SdkCodeGenExportUtil.writeGeneratedCodeFile(genereatedContents, targetFolder, logListener);
                            } catch (CodeGenerationException codeGenerationException) {
                                console.showErrorMessage("Error writing generating file to disk", codeGenerationException);
                            }
                        }
                    });
                }
            };

            ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            //deploymentTargetNotConfigured.printStackTrace();
            warnDeploymentTargetNotConfigured(project, getTitle());
        } catch (Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.error(ex);
            }
            console.showErrorMessage(ex.getMessage());
            IdeaUtil.showNotification(project, getTitle(), String.format("Transaction execution failed, reason: %s", ex.getMessage()), NotificationType.ERROR, null);
        }
    }

    private Tuple<SignedTransaction, Account> signTransaction(Project project, Module module, Transaction txn) throws DeploymentTargetNotConfigured {
        Address address = txn.sender;
        AccountService accountService = AccountService.getAccountService();
        AlgoAccount algoAccount = accountService.getAccountByAddress(address.toString());

        SigningAccountInputDialog singingAccDialog = new SigningAccountInputDialog(project, module);
        if (algoAccount != null) {
            singingAccDialog.getAccountEntryInputForm().setSenderAddress(project, algoAccount);
        }

        boolean ok1 = singingAccDialog.showAndGet();

        SignedTransaction signedTransaction = null;
        Account account = null;
        if (ok1) {
            if (singingAccDialog.isAccountType()) {
                account = singingAccDialog.getAccount();
                if (account != null) {
                    try {
                        signedTransaction = account.signTransaction(txn);
                    } catch (Exception exception) {
                        if (LOG.isDebugEnabled())
                            LOG.warn(exception);
                        Messages.showErrorDialog("Signing by account failed. Please select a valid account. \nReason: "
                                + exception.getMessage(), "Trasaction signing");
                    }
                } else {
                    Messages.showErrorDialog("Signing by account failed. Please select a valid account", "Trasaction signing");
                }
            } else {
                LogicsigSignature logicsigSignature = singingAccDialog.getLogicSignature();
                if (logicsigSignature != null) {
                    try {
                        signedTransaction
                                = Account.signLogicsigTransaction(logicsigSignature, txn);
                    } catch (Exception exception) {
                        if (LOG.isDebugEnabled())
                            LOG.warn(exception);
                        Messages.showErrorDialog("Signing by logic sig file failed. Please select a valid logic sig file. \nReason: "
                                + exception.getMessage(), "Trasaction signing");
                    }
                } else {
                    Messages.showErrorDialog("Please select a valid logic sig file", "Trasaction signing");
                }
            }
        }

        return new Tuple<>(signedTransaction, account);
    }

    public String getTitle() {
        return "Generate code (Algorand sdk)";
    }

    public String getTxnCommand() {
        return "Generate code (Algorand sdk)";
    }

    private Transaction loadTransaction(File file) throws IOException {
        if (file == null || !file.exists())
            return null;

        String content = FileUtil.loadFile(file);
        Transaction transaction = Encoder.decodeFromJson(content, Transaction.class);
        return transaction;
    }

    private SignedTransaction loadSignedTransaction(File file) {
        try {
            if (file == null || !file.exists())
                return null;

            String content = FileUtil.loadFile(file);
            SignedTransaction transaction = Encoder.decodeFromJson(content, SignedTransaction.class);
            return transaction;
        } catch (Exception e) {
            return null;
        }
    }
}
