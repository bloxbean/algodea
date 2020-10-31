package com.bloxbean.algodea.idea.contracts.action;

import com.algorand.algosdk.account.Account;
import com.bloxbean.algodea.idea.contracts.ui.AppReadDialog;
import com.bloxbean.algodea.idea.contracts.ui.AppReadMainPanel;
import com.bloxbean.algodea.idea.contracts.ui.AppTxnBaseParamEntryForm;
import com.bloxbean.algodea.idea.core.action.AlgoBaseAction;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.service.StatefulContractService;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ReadStatefulAppAction extends AlgoBaseAction {

    private static final Logger LOG = Logger.getInstance(ReadStatefulAppAction.class);

    public ReadStatefulAppAction() {
        super(AllIcons.Actions.Show);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(project == null)
            return;

        AppReadDialog dialog = new AppReadDialog(project, true);
        boolean ok = dialog.showAndGet();

        if(!ok) {
            return;
        }

        AppReadMainPanel mainPanel = dialog.getAppReadMainPanel();
        AppTxnBaseParamEntryForm entryForm = dialog.getAppTxnBaseEntryForm();
        Long appId = entryForm.getAppId();
        Account fromAccount = entryForm.getFromAccount();

        boolean localState = mainPanel.isLocalState();
        boolean globalState = mainPanel.isGlobalState();
        boolean bothState = mainPanel.isBoth();

        AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();

        try {
            final StatefulContractService sfService = new StatefulContractService(project, new LogListenerAdapter(console));

            Task.Backgroundable task = new Task.Backgroundable(project, "Application - Read State") {

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    try {
                        console.showInfoMessage("App Id      : " + appId);
                        if(fromAccount != null) {
                            console.showInfoMessage("Account     : " + fromAccount.getAddress().toString());
                        }
                        console.showInfoMessage("\n");

                        if(localState || bothState) {
                            sfService.readLocalState(fromAccount, appId);
                        }
                        if(globalState || bothState) {
                            sfService.readGlobalState(appId);
                        }

                    } catch (Exception exception) {
                        console.showErrorMessage("ReadState failed");
                        IdeaUtil.showNotification(project, "Application - ReadState", "ReadState failed", NotificationType.ERROR, null);
                    }
                }
            };

            ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            if(LOG.isDebugEnabled()) {
                LOG.warn(deploymentTargetNotConfigured);
            }
            IdeaUtil.showNotification(project, "Application - ReadState", "ReadState failed", NotificationType.ERROR, null);
        } catch (Exception ex) {
            if(LOG.isDebugEnabled()) {
                LOG.error(ex);
            }
            IdeaUtil.showNotification(project, "Application - ReadState", "ReadState failed", NotificationType.ERROR, null);
        }

    }
}
