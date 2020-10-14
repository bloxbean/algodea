package com.bloxbean.algodea.idea.contracts.ui;

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

    public AppReadMainPanel(Project project) {
        initialize(project);
    }

    public void initialize(Project project) {
        appTxnBaseEntryForm.initializeData(project);
    }

    protected @Nullable ValidationInfo doValidate() {
        if(isLocalState() || isBoth()) {
            appTxnBaseEntryForm.setMandatoryAccountCheck(true);
        } else if(isGlobalState()) {
            appTxnBaseEntryForm.setMandatoryAccountCheck(false);
        }

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
