package com.bloxbean.algorand.idea.configuration.action;

import com.bloxbean.algorand.idea.configuration.model.NodeInfo;
import com.bloxbean.algorand.idea.configuration.service.ConfiguraionHelperService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class DeleteAlgoNodeAction extends AnAction {
    private NodeInfo node;

    public DeleteAlgoNodeAction(NodeInfo node) {
        super("Delete", "Delete this Algorand Node", AllIcons.General.Remove);
        this.node = node;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(project == null) return;

        int result = Messages.showYesNoDialog("Do you really want to delete this Algorand Node configuration ?", "Algorand Node Configuration", AllIcons.General.QuestionDialog);

        if(result == Messages.NO)
            return;

        if(node != null)
            ConfiguraionHelperService.deleteAlgoNodeConfiguration(node);
    }
}
