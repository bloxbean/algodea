package com.bloxbean.algorand.idea.serverint.action;

import com.bloxbean.algorand.idea.serverint.service.ConfiguraionHelperService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class CreateNewServerAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        ConfiguraionHelperService.createNewNodeConfiguration(project);
    }
}
