package com.bloxbean.algorand.idea.configuration.action;

import com.bloxbean.algorand.idea.common.Tuple;
import com.bloxbean.algorand.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algorand.idea.configuration.ui.AlgoProjectConfigurationDialog;
import com.bloxbean.algorand.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ConfigurationAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(project == null)
            return;

        AlgoProjectState algoProjectState = AlgoProjectState.getInstance(project);
        if(algoProjectState == null) {
            IdeaUtil.showNotification(project, "Algorand project configuration",
                    "Unable to configure Algorand project", NotificationType.ERROR, null);
            return;
        }

        AlgoProjectConfigurationDialog dialog = new AlgoProjectConfigurationDialog(project);

        boolean ok = dialog.showAndGet();
        if(ok) {
            if(algoProjectState == null) {
                IdeaUtil.showNotification(project, "Algorand node configuration",
                        "Unable to save Algorand configuration for the project", NotificationType.ERROR, null);
                return;
            } else {
                AlgoProjectState.State state = algoProjectState.getState();
                Tuple<AlgoProjectState.ConfigType, String> compilerSetting = dialog.getCompilerSdkId();

                if(compilerSetting != null) {
                    state.setCompilerType(compilerSetting._1());
                    state.setCompilerId(compilerSetting._2());
                }
                state.setDeploymentServerId(dialog.getDeployementNodeId());

                algoProjectState.setState(state);
            }
        } else {

        }

    }
}
