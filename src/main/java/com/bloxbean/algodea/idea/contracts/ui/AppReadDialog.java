package com.bloxbean.algodea.idea.contracts.ui;

import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class AppReadDialog extends DialogWrapper {
    private AppReadMainPanel appReadMainPanel;

    public AppReadDialog(Project project, boolean localState) throws DeploymentTargetNotConfigured {
        super(project,  true);
        appReadMainPanel = new AppReadMainPanel(project);
        init();
        setTitle("Application - Read State");
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        return appReadMainPanel.doValidate();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return appReadMainPanel.getMainPanel();
    }

    public AppTxnBaseParamEntryForm getAppTxnBaseEntryForm() {
        return appReadMainPanel.getAppTxnBaseEntryForm();
    }

    public AppReadMainPanel getAppReadMainPanel() {
        return appReadMainPanel;
    }

}
