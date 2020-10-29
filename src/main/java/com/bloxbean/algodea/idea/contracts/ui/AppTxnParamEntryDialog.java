package com.bloxbean.algodea.idea.contracts.ui;

import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
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

    public AppTxnDetailsEntryForm getAppTxnDetailsEntryForm() {
        return appTxnMainPanel.getAppTxnDetailsEntryForm();
    }

    public TransactionDtlsEntryForm getTxnDetailsEntryForm() {
        return appTxnMainPanel.getTxnDtlEntryForm();
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
