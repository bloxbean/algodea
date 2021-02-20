package com.bloxbean.algodea.idea.stateless.action;

import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.common.AlgoIcons;
import com.bloxbean.algodea.idea.core.action.BaseTxnAction;
import com.bloxbean.algodea.idea.module.filetypes.LSigFileType;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.AssetTxnParameters;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.service.LogicSigTransactionService;
import com.bloxbean.algodea.idea.stateless.ui.LogicSigOptInAssetDialog;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class LogicSigOptInAssetTransactionAction extends BaseTxnAction {
    private final static Logger LOG = Logger.getInstance(LogicSigOptInAssetTransactionAction.class);

    public LogicSigOptInAssetTransactionAction() {
        super(AlgoIcons.LOGIC_SIG_RUN_ICON);
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        if(isAlgoProject(e)) {
            e.getPresentation().setEnabledAndVisible(true);
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (LOG.isDebugEnabled())
            LOG.debug("Logic sig transaction");

        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        if (project == null)
            return;

        final Module module = LangDataKeys.MODULE.getData(e.getDataContext());

        final AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();

        VirtualFile lsigVfs = e.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);

        LogicSigOptInAssetDialog dialog = null;
        if(lsigVfs != null
                && LSigFileType.EXTENSION.equals(lsigVfs.getExtension())
                && lsigVfs.exists()) {
            byte[] logicSig = null;
            try {
                logicSig = VfsUtil.loadBytes(lsigVfs);
            } catch (IOException ioException) {
                console.showErrorMessage("Error loading Logic sig file : " + lsigVfs.getCanonicalPath());
                IdeaUtil.showNotification(project, "Logic Sig transaction",
                        "Logic Sig file is not readable : " + ioException.getMessage(), NotificationType.ERROR, null);
                return;
            }

            //Open dialog and get transaction inputs
            try {
                dialog = new LogicSigOptInAssetDialog(project, module, lsigVfs.getCanonicalPath());
            } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
                warnDeploymentTargetNotConfigured(project, getTitle());
                return;
            }
        } else { //Opened from editor context menu
            try {
                dialog = new LogicSigOptInAssetDialog(project, module);
            }catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
                warnDeploymentTargetNotConfigured(project, getTitle());
                return;
            }
        }
        //Enable dry run button
        dialog.enableDryRun();

        TransactionDtlsEntryForm transactionDtlsEntryForm = dialog.getTransactionDtlsEntryForm();

        boolean ok = dialog.showAndGet();
        if(!ok) {
            IdeaUtil.showNotification(project, "Logic Sig OptIn Asset",
                    "Stateless contract OptIn Asset transaction was cancelled", NotificationType.WARNING, null);
            return;
        }

        String lsigPath = dialog.getLsigPath();
        if(StringUtil.isEmpty(lsigPath)) {
            IdeaUtil.showNotification(project, "Logic Sig OptIn Asset",
                    "Invalid path to Logic Sig file", NotificationType.WARNING, null);
            return;
        }

        //Get input values
        boolean isContractAccountTxnType = dialog.isContractAccountType();
        boolean isAccountDelegationTxnType = dialog.isAccountDelegationType();

        Address senderAddress = null;
        if(isAccountDelegationTxnType) {
            senderAddress = dialog.getSenderAddress();
        }

        //txn detals params
        TxnDetailsParameters txnDetailsParameters = null;
        try {
            txnDetailsParameters = transactionDtlsEntryForm.getTxnDetailsParameters();
        } catch (Exception exception) {
            console.showErrorMessage("Invalid transaction details input parameters : " +  exception.getMessage());
            IdeaUtil.showNotification(project, "Logic Sig transaction",
                    "Invalid transaction details input parameters : " +  exception.getMessage(), NotificationType.WARNING, null);
            return;
        }

        final Long assetId = dialog.getAssetId();

        AssetTxnParameters assetTxnParameters = new AssetTxnParameters();
        assetTxnParameters.assetId = assetId;

        Address finalSenderAccount = senderAddress;
        TxnDetailsParameters finalTxnDetailsParams = txnDetailsParameters;

        RequestMode requestMode = dialog.getRequestMode();
        LogListener logListener = new LogListenerAdapter(console);
        try {
            LogicSigTransactionService transactionService = new LogicSigTransactionService(project, logListener);

            Task.Backgroundable task = new Task.Backgroundable(project, getTxnCommand()) {

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    console.showInfoMessage(String.format("Starting %s ...\n", getTxnCommand()));
                    try {

                        Result result = transactionService.logicSigOptInAssetTransaction(lsigPath, finalSenderAccount, assetTxnParameters, finalTxnDetailsParams, requestMode);
                        processResult(project, module, result, requestMode, logListener);
                    } catch (Exception exception) {
                        console.showErrorMessage(String.format("%s failed", getTxnCommand()), exception);
                        IdeaUtil.showNotification(project, getTitle(), String.format("%s failed, Reason: %s", getTxnCommand(), exception.getMessage()), NotificationType.ERROR, null);
                    }
                }
            };

            ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            warnDeploymentTargetNotConfigured(project, getTitle());
        } catch (Exception ex) {
            if(LOG.isDebugEnabled()) {
                LOG.error(ex);
            }
            console.showErrorMessage(ex.getMessage(), ex);
            IdeaUtil.showNotification(project, getTitle(), String.format("Logic sig OptIn Asset transaction failed, reason: %s", ex.getMessage()), NotificationType.ERROR, null);
        }

    }

    public String getTitle() {
        return "Opt-In Asset using Logic Sig";
    }

    public String getTxnCommand() {
        return "Opt-In Asset using Logic Sig";
    }
}
