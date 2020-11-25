package com.bloxbean.algodea.idea.dryrun.action;

import com.algorand.algosdk.v2.client.model.Account;
import com.bloxbean.algodea.idea.core.action.AlgoBaseAction;
import com.bloxbean.algodea.idea.core.action.util.ExporterUtil;
import com.bloxbean.algodea.idea.dryrun.ui.AccountStateExportDialog;
import com.bloxbean.algodea.idea.nodeint.exception.ApiCallException;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.service.AlgoAccountService;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.util.JsonUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AccountStateExportAction extends AlgoBaseAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            return;

        final Module module = LangDataKeys.MODULE.getData(e.getDataContext());

        AccountStateExportDialog dialog = new AccountStateExportDialog(project);
        boolean ok = dialog.showAndGet();

        if(!ok)
            return;

        List<String> accounts = dialog.getAccounts();

        AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();
        try {
            LogListenerAdapter logListener = new LogListenerAdapter(console);
            AlgoAccountService accountService = new AlgoAccountService(project, logListener);

            Task.Backgroundable task = new Task.Backgroundable(project, "Exporting Account details") {

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    try {
                        console.showInfoMessage("Start dumping account details  ...");
                        List<Account> accountList = accountService.getAccounts(accounts);
                        String result = JsonUtil.getPrettyJson(accountList);
                        console.showInfoMessage(result);
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ExporterUtil.exportDryRunAccounts(module, result, "accounts", logListener);
                                } catch (Exception exception) {
                                    console.showErrorMessage("Account(s) export failed");
                                    console.showErrorMessage(exception.getMessage());
                                }
                            }
                        });
                    } catch (ApiCallException apiCallException) {
                        console.showErrorMessage("Account(s) export failed");
                        console.showErrorMessage(apiCallException.getMessage());
                    } catch (Exception exception) {
                        console.showErrorMessage("Account(s) export failed");
                        console.showErrorMessage(exception.getMessage());
                    }
                }
            };
            ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            warnDeploymentTargetNotConfigured(project, "Account dump");
        }
    }
}
