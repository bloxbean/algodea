package com.bloxbean.algodea.idea.contracts.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algodea.idea.contracts.ui.AppTxnBaseParamEntryForm;
import com.bloxbean.algodea.idea.contracts.ui.AppTxnDetailsEntryForm;
import com.bloxbean.algodea.idea.contracts.ui.AppTxnParamEntryDialog;
import com.bloxbean.algodea.idea.core.action.BaseTxnAction;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.service.StatefulContractService;
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
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public abstract class  BaseStatefulAppAction extends BaseTxnAction {
    private final static Logger LOG = Logger.getInstance(BaseStatefulAppAction.class);

    public BaseStatefulAppAction() {
        super();
    }

    public BaseStatefulAppAction(Icon icon) {
        super(icon);
    }

    public abstract String getInputDialogTitle();

    public abstract String getApplicationTxnDescription();

    public String getTitle() {
        return "Application - " + getTxnCommand();
    }

    public abstract Result invokeTransaction(StatefulContractService statefulContractService, Long appId, Account fromAccount,
                                             TxnDetailsParameters txnDetailsParameters, RequestMode requestMode) throws Exception;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(project == null)
            return;

        final Module module = LangDataKeys.MODULE.getData(e.getDataContext());
        if(module == null)
            return;

        AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();

        AppTxnParamEntryDialog dialog = new AppTxnParamEntryDialog(project, getApplicationTxnDescription());
        boolean ok = dialog.showAndGet();

        if(!ok) {
            IdeaUtil.showNotification(project, getTitle(), getTxnCommand() + " call was cancelled", NotificationType.WARNING, null);
            return;
        }

        if(LOG.isDebugEnabled()) {
            LOG.debug("*****  App Id: " + dialog.getAppTxnBaseEntryForm().getAppId());
            LOG.debug("****** From Account: " + dialog.getAppTxnBaseEntryForm().getFromAccount());
            LOG.debug("******* Args : " + dialog.getAppTxnDetailsEntryForm().getArgs());
        }

        AlgoProjectState projectState = AlgoProjectState.getInstance(project);
        if(projectState == null) {
            LOG.warn("Project state is null");
            IdeaUtil.showNotificationWithAction(project, getTitle(),
                    "Project data could not be found. Something is wrong", NotificationType.ERROR, null);
            return;
        }

        try {
            LogListenerAdapter logListener = new LogListenerAdapter(console);
            StatefulContractService sfService
                    = new StatefulContractService(project, logListener);

            AppTxnBaseParamEntryForm appBaseEntryForm = dialog.getAppTxnBaseEntryForm();
            AppTxnDetailsEntryForm appTxnDetailsEntryForm = dialog.getAppTxnDetailsEntryForm();

            Long appId = appBaseEntryForm.getAppId();
            Account fromAccount = appBaseEntryForm.getFromAccount();
            List<byte[]> appArgs = appTxnDetailsEntryForm.getArgsAsBytes();

            List<Address> accounts = appTxnDetailsEntryForm.getAccounts();
            List<Long> foreignApps = appTxnDetailsEntryForm.getForeignApps();
            List<Long> foreignAssets = appTxnDetailsEntryForm.getForeignAssets();

            TransactionDtlsEntryForm txnDetailsEntryForm = dialog.getTxnDetailsEntryForm();
            TxnDetailsParameters generalTxnDetailsParam = txnDetailsEntryForm.getTxnDetailsParameters();

            TxnDetailsParameters txnDetailsParameters = new TxnDetailsParameters();
            txnDetailsParameters.setAppArgs(appArgs);
            txnDetailsParameters.setAccounts(accounts);
            txnDetailsParameters.setForeignApps(foreignApps);
            txnDetailsParameters.setForeignAssets(foreignAssets);

            //general txn parameters
            txnDetailsParameters.setNote(generalTxnDetailsParam.getNote());
            txnDetailsParameters.setLease(generalTxnDetailsParam.getLease());
            txnDetailsParameters.setFee(generalTxnDetailsParam.getFee());
            txnDetailsParameters.setFlatFee(generalTxnDetailsParam.getFlatFee());

            RequestMode requestMode = dialog.getRequestMode();
            Task.Backgroundable task = new Task.Backgroundable(project, getApplicationTxnDescription()) {

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    console.showInfoMessage(String.format("Starting %s transaction ...", getTxnCommand()));
                    try {
                        Result result = invokeTransaction(sfService, appId, fromAccount, txnDetailsParameters, requestMode);

                        String fromAccountAddress = fromAccount != null ? fromAccount.getAddress().toString(): "";

                        if(requestMode == null || requestMode.equals(RequestMode.TRANSACTION)) {
                            if (result.isSuccessful()) {
                                console.showInfoMessage(String.format("%s was successful with app id : %s, from account: %s", getTxnCommand(), appId, fromAccountAddress));
                                IdeaUtil.showNotification(project, getTitle(), String.format("%s was successful", getTxnCommand()), NotificationType.INFORMATION, null);
                            } else {
                                console.showErrorMessage(String.format("%s failed", getTxnCommand()));
                                IdeaUtil.showNotification(project, getTitle(), String.format("%s failed", getTxnCommand()), NotificationType.ERROR, null);
                            }
                        } else {
                            processResult(project, module, result, requestMode, logListener);
                        }
                    } catch (Exception exception) {
                        if(LOG.isDebugEnabled()) {
                            LOG.warn(exception);
                        }
                        console.showErrorMessage(String.format("%s failed", getTxnCommand()));
                        IdeaUtil.showNotification(project, getTitle(), String.format("%s failed", getTxnCommand()), NotificationType.ERROR, null);
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
            console.showErrorMessage(ex.getMessage());
            IdeaUtil.showNotification(project, getTitle(), String.format("%s failed, reason: %s", getTxnCommand(), ex.getMessage()), NotificationType.ERROR, null);
        }
    }
}
