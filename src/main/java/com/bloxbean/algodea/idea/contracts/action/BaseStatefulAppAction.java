package com.bloxbean.algodea.idea.contracts.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algodea.idea.contracts.ui.AppTxnBaseParamEntryForm;
import com.bloxbean.algodea.idea.contracts.ui.AppTxnParamEntryDialog;
import com.bloxbean.algodea.idea.contracts.ui.TxnDetailsEntryForm;
import com.bloxbean.algodea.idea.core.action.AlgoBaseAction;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.service.StatefulContractService;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
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

import java.util.List;

public abstract class  BaseStatefulAppAction extends AlgoBaseAction {
    private final static Logger LOG = Logger.getInstance(BaseStatefulAppAction.class);

    public abstract String getInputDialogTitle();

    public abstract String getApplicationTxnDescription();

    public abstract String getApplicationTxnCommand();

    public String getTitle() {
        return "Application - " + getApplicationTxnCommand();
    }

    public abstract boolean invokeTransaction(StatefulContractService statefulContractService, Long appId, Account fromAccount,
                                              TxnDetailsParameters txnDetailsParameters) throws Exception;

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
            IdeaUtil.showNotification(project, getTitle(), getApplicationTxnCommand() + " call was cancelled", NotificationType.INFORMATION, null);
            return;
        }

        if(LOG.isDebugEnabled()) {
            LOG.debug("*****  App Id: " + dialog.getAppTxnBaseEntryForm().getAppId());
            LOG.debug("****** From Account: " + dialog.getAppTxnBaseEntryForm().getFromAccount());
            LOG.debug("******* Args : " + dialog.getTxnDetailsEntryForm().getArgs());
            LOG.debug("******** Note : " + dialog.getTxnDetailsEntryForm().getNote());
        }

        AlgoProjectState projectState = AlgoProjectState.getInstance(project);
        if(projectState == null) {
            LOG.error("Project state is null");
            IdeaUtil.showNotificationWithAction(project, getTitle(),
                    "Project data could not be found. Something is wrong", NotificationType.ERROR, null);
            return;
        }

        try {
            StatefulContractService sfService
                    = new StatefulContractService(project, new LogListenerAdapter(console));

            AppTxnBaseParamEntryForm appBaseEntryForm = dialog.getAppTxnBaseEntryForm();
            TxnDetailsEntryForm txnDetailsEntryForm = dialog.getTxnDetailsEntryForm();

            Long appId = appBaseEntryForm.getAppId();
            Account fromAccount = appBaseEntryForm.getFromAccount();
            List<byte[]> appArgs = txnDetailsEntryForm.getArgsAsBytes();

            byte[] note = txnDetailsEntryForm.getNoteBytes();
            byte[] lease = txnDetailsEntryForm.getLeaseBytes();
            List<Address> accounts = txnDetailsEntryForm.getAccounts();
            List<Long> foreignApps = txnDetailsEntryForm.getForeignApps();
            List<Long> foreignAssets = txnDetailsEntryForm.getForeignAssets();

            TxnDetailsParameters txnDetailsParameters = new TxnDetailsParameters();
            txnDetailsParameters.setAppArgs(appArgs);
            txnDetailsParameters.setNote(note);
            txnDetailsParameters.setLease(lease);
            txnDetailsParameters.setAccounts(accounts);
            txnDetailsParameters.setForeignApps(foreignApps);
            txnDetailsParameters.setForeignAssets(foreignAssets);

            Task.Backgroundable task = new Task.Backgroundable(project, getApplicationTxnDescription()) {

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    console.showInfoMessage(String.format("Starting %s transaction ...", getApplicationTxnCommand()));
                    try {
                        boolean status = invokeTransaction(sfService, appId, fromAccount, txnDetailsParameters);

                        String fromAccountAddress = fromAccount != null ? fromAccount.getAddress().toString(): "";

                        if(status) {
                            console.showInfoMessage(String.format("%s was successful with app id : %s, from account: %s",  getApplicationTxnCommand(), appId, fromAccountAddress));
                            IdeaUtil.showNotification(project, getTitle(), String.format("%s was successful", getApplicationTxnCommand()), NotificationType.INFORMATION, null);
                        } else {
                            console.showErrorMessage(String.format("%s failed", getApplicationTxnCommand()));
                            IdeaUtil.showNotification(project, getTitle(), String.format("%s failed", getApplicationTxnCommand()), NotificationType.ERROR, null);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        console.showErrorMessage(String.format("%s failed", getApplicationTxnCommand()));
                        IdeaUtil.showNotification(project, getTitle(), String.format("%s failed", getApplicationTxnCommand()), NotificationType.ERROR, null);
                    }
                }
            };

            ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            deploymentTargetNotConfigured.printStackTrace();
            warnDeploymentTargetNotConfigured(project, getTitle());
        } catch (Exception ex) {
            LOG.error(ex);
            console.showErrorMessage(ex.getMessage());
            IdeaUtil.showNotification(project, getTitle(), String.format("%s failed, reason: %s", getApplicationTxnCommand(), ex.getMessage()), NotificationType.ERROR, null);
        }
    }
}
