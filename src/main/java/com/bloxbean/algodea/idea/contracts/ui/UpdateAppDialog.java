package com.bloxbean.algodea.idea.contracts.ui;

import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class UpdateAppDialog extends DialogWrapper {
    private UpdateAppMainPanel updateAppMainPanel;

    public UpdateAppDialog(Project project, String contract) {
        super(project,  true);
        updateAppMainPanel = new UpdateAppMainPanel(project, contract);
        init();
        setTitle("UpdateApplication");
    }


    @Override
    protected @Nullable List<ValidationInfo> doValidateAll() {
        return updateAppMainPanel.doValidate();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return updateAppMainPanel.getMainPanel();
    }

    public UpdateAppMainPanel getUpdateAppMainPanel() {
        return updateAppMainPanel;
    }

    public UpdateAppEntryForm getUpdateAppEntryForm() {
        return updateAppMainPanel.getUpdateAppEntryForm();
    }

    public AppTxnBaseParamEntryForm getAppTxnBaseEntryForm() {
        return updateAppMainPanel.getAppTxnBaseEntryForm();
    }

    public AppTxnDetailsEntryForm getAppTxnDetailsEntryForm() {
        return updateAppMainPanel.getAppTxnDetailsEntryForm();
    }

    public TransactionDtlsEntryForm getTxnDetailsEntryForm() {
        return updateAppMainPanel.getTxnDetailsEntryForm();
    }
}
