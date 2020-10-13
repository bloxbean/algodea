package com.bloxbean.algodea.idea.contracts.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AppTxnParamEntryDialog extends DialogWrapper {
    AppTxnParamEntryForm appTxnParamEntryForm;

    public AppTxnParamEntryDialog(Project project, String title) {
        super(project, false);
        appTxnParamEntryForm = new AppTxnParamEntryForm(project);
        init();
        setTitle(title);
    }

    public AppTxnParamEntryForm getAppTxnParamEntryForm() {
        return appTxnParamEntryForm;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return appTxnParamEntryForm.getMainPanel();
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        return appTxnParamEntryForm.doValidate();
    }
}
