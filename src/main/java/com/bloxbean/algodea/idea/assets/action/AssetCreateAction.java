package com.bloxbean.algodea.idea.assets.action;

import com.algorand.algosdk.account.Account;
import com.bloxbean.algodea.idea.assets.service.AssetCacheService;
import com.bloxbean.algodea.idea.assets.ui.AssetConfigurationDialog;
import com.bloxbean.algodea.idea.core.action.BaseTxnAction;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.AssetTxnParameters;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.AssetTransactionService;
import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.twelvemonkeys.lang.StringUtil;
import org.jetbrains.annotations.NotNull;

public class AssetCreateAction extends BaseTxnAction {
    private final static Logger LOG = Logger.getInstance(AssetCreateAction.class);

    public AssetCreateAction() {
        super();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(project == null)
            return;

        final Module module = LangDataKeys.MODULE.getData(e.getDataContext());

        AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();

        AssetConfigurationDialog dialog = new AssetConfigurationDialog(project);
        boolean ok = dialog.showAndGet();

        if(!ok) {
            IdeaUtil.showNotification(project, getTitle(), String.format("%s was cancelled", getTxnCommand()), NotificationType.WARNING, null);
            return;
        }

        AssetTxnParameters assetTxnParameters = null;
        try {
            assetTxnParameters = dialog.getAssetTxnParameters();
        } catch (Exception exception) {
            console.showErrorMessage("Asset creation failed. Reason: ", exception);
            IdeaUtil.showNotification(project, getTitle(), "error getting asset creation parameters", NotificationType.ERROR, null);
        }

        Account creatorAccount = dialog.getCreatorAddress();

        if(creatorAccount == null) {
            console.showErrorMessage("Creator account cannot be empty");
            IdeaUtil.showNotification(project, getTitle(), "Creator account cannot be empty", NotificationType.WARNING, null);
            return;
        }

        RequestMode requestMode = dialog.getRequestMode();
        LogListener logListener = new LogListenerAdapter(console);
        AssetCacheService assetCacheService = AssetCacheService.getInstance();
        try {
            TransactionDtlsEntryForm transactionDtlsEntryForm = dialog.getTransactionDtlsEntryForm();
            TxnDetailsParameters txnDetailsParameters = transactionDtlsEntryForm.getTxnDetailsParameters();

            AssetTransactionService assetTransactionService = new AssetTransactionService(project, logListener);

            final AssetTxnParameters finalAssetTxnPrameters = assetTxnParameters;
            Task.Backgroundable task = new Task.Backgroundable(project, getTxnCommand()) {

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    console.showInfoMessage(String.format("Starting %s ...\n", getTxnCommand()));
                    try {
                        Result<Long> result = assetTransactionService.createAsset(creatorAccount, finalAssetTxnPrameters, txnDetailsParameters, requestMode);

                        if(requestMode.equals(RequestMode.TRANSACTION)) {
                            if (result != null && result.getValue() != null) {

                                if (!StringUtil.isEmpty(assetTransactionService.getNetworkGenesisHash())) {
                                    assetCacheService.addAssetId(assetTransactionService.getNetworkGenesisHash(),
                                            finalAssetTxnPrameters.assetName, String.valueOf(result.getValue()));
                                }

                                console.showInfoMessage(String.format("Asset created successfully with asset id %s", result.getValue()));
                                IdeaUtil.showNotification(project, getTitle(), String.format("%s was successful", getTxnCommand()), NotificationType.INFORMATION, null);
                            } else {
                                console.showErrorMessage(String.format("%s failed", getTxnCommand()));
                                IdeaUtil.showNotification(project, getTitle(), String.format("%s failed", getTxnCommand()), NotificationType.ERROR, null);
                            }
                        } else {
                            processResult(project, module, result, requestMode, logListener);
                        }
                    } catch (Exception exception) {
                        //exception.printStackTrace();
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
                LOG.warn(ex);
            }
            console.showErrorMessage(ex.getMessage(), ex);
            IdeaUtil.showNotification(project, getTitle(), String.format("AssetModify transaction failed, reason: %s", ex.getMessage()), NotificationType.ERROR, null);
        }
    }

    protected String getTxnCommand() {
        return "AssetCreation";
    }

    protected String getTitle() {
        return "Asset Create";
    }
}
