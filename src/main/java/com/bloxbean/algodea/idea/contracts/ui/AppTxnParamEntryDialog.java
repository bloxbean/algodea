package com.bloxbean.algodea.idea.contracts.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class AppTxnParamEntryDialog extends DialogWrapper {
    AppTxnMainPanel appTxnMainPanel;

    public AppTxnParamEntryDialog(Project project, String title) {
        super(project, false);
        appTxnMainPanel = new AppTxnMainPanel(project);
        init();
        setTitle(title);
    }

    public AppTxnBaseParamEntryForm getAppTxnBaseEntryForm() {
        return appTxnMainPanel.getAppTxnBaseEntryForm();
    }

    public TxnDetailsEntryForm getTxnDetailsEntryForm() {
        return appTxnMainPanel.getTxnDetailsEntryForm();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return appTxnMainPanel.getMainPanel();
    }

    @Override
    protected @NotNull List<ValidationInfo> doValidateAll() {
        return appTxnMainPanel.doValidate();
    }
}
