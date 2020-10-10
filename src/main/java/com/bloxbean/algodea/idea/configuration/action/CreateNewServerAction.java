package com.bloxbean.algodea.idea.configuration.action;

import com.bloxbean.algodea.idea.configuration.service.ConfiguraionHelperService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class CreateNewServerAction extends AnAction {
    public final static String ACTION_ID = CreateNewServerAction.class.getName();

    public CreateNewServerAction() {
        super("Add Algorand Node", "Add a New Algorand Node", AllIcons.General.Add);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        ConfiguraionHelperService.createOrUpdateNewNodeConfiguration(project, null);
    }

}
