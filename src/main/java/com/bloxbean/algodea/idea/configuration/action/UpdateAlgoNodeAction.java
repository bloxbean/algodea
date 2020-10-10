package com.bloxbean.algodea.idea.configuration.action;

import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.configuration.service.ConfiguraionHelperService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class UpdateAlgoNodeAction extends AnAction {
    private NodeInfo node;
    public UpdateAlgoNodeAction(NodeInfo nodeInfo) {
        super("Edit", "Edit this Algorand Node", AllIcons.General.Settings);
        this.node = nodeInfo;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        if(node != null)
            ConfiguraionHelperService.createOrUpdateNewNodeConfiguration(project, node);
    }
}
