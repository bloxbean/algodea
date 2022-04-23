package com.bloxbean.algodea.idea.dryrun.action;

import com.algorand.algosdk.v2.client.model.Application;
import com.bloxbean.algodea.idea.core.action.AlgoBaseAction;
import com.bloxbean.algodea.idea.core.action.util.ExporterUtil;
import com.bloxbean.algodea.idea.dryrun.ui.ApplicationStateExportDialog;
import com.bloxbean.algodea.idea.nodeint.exception.ApiCallException;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.service.StatefulContractService;
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

public class ApplicationStateExportAction extends AlgoBaseAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            return;

        final Module module = LangDataKeys.MODULE.getData(e.getDataContext());

        ApplicationStateExportDialog dialog = new ApplicationStateExportDialog();
        boolean ok = dialog.showAndGet();

        if(!ok)
            return;

        List<Long> accounts = dialog.getApplications();


        AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();
        try {
            LogListenerAdapter logListener = new LogListenerAdapter(console);
            StatefulContractService service = new StatefulContractService(project, logListener);

            Task.Backgroundable task = new Task.Backgroundable(project, "Exporting Application details") {

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    try {
                        console.showInfoMessage("Start dumping Application details  ...");
                        List<Application> applications = service.getApplication(accounts);
                        String result = JsonUtil.getPrettyJson(applications);
                        console.showInfoMessage(result);
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ExporterUtil.exportDryRunApplications(project, module, result, "applications", logListener);
                                } catch (Exception exception) {
                                    console.showErrorMessage("Application(s) export failed");
                                    console.showErrorMessage(exception.getMessage());
                                }
                            }
                        });
                    } catch (ApiCallException apiCallException) {
                        console.showErrorMessage("Application(s) export failed");
                        console.showErrorMessage(apiCallException.getMessage());
                    } catch (Exception exception) {
                        console.showErrorMessage("Application(s) export failed");
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
