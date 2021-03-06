package com.bloxbean.algodea.idea.contracts.ui;

import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBRadioButton;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AppReadMainPanel {
    private JRadioButton localStateRadioButton;
    private JRadioButton globalStateRadioButton;
    private JRadioButton bothRadioButton;
    private AppTxnBaseParamEntryForm appTxnBaseEntryForm;
    private JPanel mainPanel;
    private ButtonGroup buttonGroup;

    public AppReadMainPanel(Project project) throws DeploymentTargetNotConfigured {
        initialize(project);
    }

    public void initialize(Project project) throws DeploymentTargetNotConfigured {
        appTxnBaseEntryForm.initializeData(project);

        appTxnBaseEntryForm.disbleSignerFields();
        localStateRadioButton.addActionListener(e -> {
            if(localStateRadioButton.isSelected()) {
                appTxnBaseEntryForm.setMandatoryAccountCheck(true);
            }
        });

        globalStateRadioButton.addActionListener( e -> {
            appTxnBaseEntryForm.setMandatoryAccountCheck(false);
        });

        bothRadioButton.addActionListener(e -> {
            appTxnBaseEntryForm.setMandatoryAccountCheck(true);
        });
    }

    protected @Nullable ValidationInfo doValidate() {
        return appTxnBaseEntryForm.doValidate();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        localStateRadioButton = new JBRadioButton();
        globalStateRadioButton = new JBRadioButton();
        bothRadioButton = new JBRadioButton();
        bothRadioButton.setSelected(true);

        buttonGroup = new ButtonGroup();

        buttonGroup.add(localStateRadioButton);
        buttonGroup.add(globalStateRadioButton);
        buttonGroup.add(bothRadioButton);

        appTxnBaseEntryForm = new AppTxnBaseParamEntryForm();
    }

    public boolean isLocalState() {
        return localStateRadioButton.isSelected();
    }

    public boolean isGlobalState() {
        return globalStateRadioButton.isSelected();
    }

    public boolean isBoth() {
        return bothRadioButton.isSelected();
    }

    public JComponent getMainPanel() {
        return mainPanel;
    }

    public AppTxnBaseParamEntryForm getAppTxnBaseEntryForm() {
        return appTxnBaseEntryForm;
    }
}
