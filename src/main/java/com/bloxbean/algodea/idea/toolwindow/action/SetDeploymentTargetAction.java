package com.bloxbean.algodea.idea.toolwindow.action;

import com.bloxbean.algodea.idea.configuration.service.ConfiguraionHelperService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class SetDeploymentTargetAction extends AnAction {
    private String deploymentSererId;

    public SetDeploymentTargetAction(String deploymentSererId) {
        super("Use for Deployment", "Use this for Deployment", AllIcons.Actions.Execute);
        this.deploymentSererId = deploymentSererId;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(project == null) return;

        ConfiguraionHelperService.setDeploymenetServerId(project, deploymentSererId);
    }
}
