package com.bloxbean.algorand.idea.configuration.action;

import com.bloxbean.algorand.idea.configuration.model.AlgoLocalSDK;
import com.bloxbean.algorand.idea.configuration.model.NodeInfo;
import com.bloxbean.algorand.idea.configuration.service.ConfiguraionHelperService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class DeleteAlgoSDKAction extends AnAction {
    private AlgoLocalSDK sdk;

    public DeleteAlgoSDKAction(AlgoLocalSDK sdk) {
        super("Delete", "Delete this Algorand SDK", AllIcons.General.Remove);
        this.sdk = sdk;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(project == null) return;

        int result = Messages.showYesNoDialog("Do you really want to delete this Algorand SDK configuration ?", "Algorand Local SDK Configuration", AllIcons.General.QuestionDialog);

        if(result == Messages.NO)
            return;

        if(sdk != null)
            ConfiguraionHelperService.deleteAlgoLocalSDKConfiguration(sdk);
    }
}
