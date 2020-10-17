package com.bloxbean.algodea.idea.account.action;

import com.bloxbean.algodea.idea.core.action.AlgoBaseAction;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.account.ui.AccountEntryDialog;
import com.bloxbean.algodea.idea.nodeint.exception.ApiCallException;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.service.AlgoAccountService;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class AccountDumpAction extends AlgoBaseAction {
    public final static String ACTION_ID = AccountDumpAction.class.getName();

    public AccountDumpAction() {
        super(AllIcons.Actions.Download);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            return;

        AccountEntryDialog dialog = new AccountEntryDialog(project, "Enter an Account");
        boolean ok = dialog.showAndGet();
        if (!ok)
            return;

        AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();
        try {
            AlgoAccountService accountService = new AlgoAccountService(project, new LogListenerAdapter(console));

            Task.Backgroundable task = new Task.Backgroundable(project, "Dumping Account details") {

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    try {
                        console.showInfoMessage("Start dumping account details  ...");
                        String accountDetails = accountService.getAccountDump(dialog.getAccount());
                        console.showInfoMessage(accountDetails);
                    } catch (ApiCallException apiCallException) {
                        console.showErrorMessage("Account dump failed");
                        console.showErrorMessage(apiCallException.getMessage());
                    }
                }
            };
            ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            warnDeploymentTargetNotConfigured(project, "Account dump");
        }

    }
}
