package com.bloxbean.algorand.idea.configuration.action;

import com.bloxbean.algorand.idea.configuration.service.ConfiguraionHelperService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class CreateNewLocalSDKAction extends AnAction {
    public final static String ACTION_ID = CreateNewLocalSDKAction.class.getName();

    public CreateNewLocalSDKAction() {
        super("Add Algorand Local SDK", "Add a new Algorand Local SDK", AllIcons.General.AddJdk);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        ConfiguraionHelperService.createOrUpdateLocalSDKConfiguration(project, null);
    }

}
