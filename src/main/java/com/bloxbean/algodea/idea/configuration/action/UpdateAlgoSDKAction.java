package com.bloxbean.algodea.idea.configuration.action;

import com.bloxbean.algodea.idea.configuration.model.AlgoLocalSDK;
import com.bloxbean.algodea.idea.configuration.service.ConfiguraionHelperService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class UpdateAlgoSDKAction extends AnAction {
    private AlgoLocalSDK sdk;

    public UpdateAlgoSDKAction(AlgoLocalSDK sdk) {
        super("Edit", "Edit this Algorand SDK", AllIcons.General.Settings);
        this.sdk = sdk;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        if(sdk != null)
            ConfiguraionHelperService.createOrUpdateLocalSDKConfiguration(project, sdk);
    }
}
