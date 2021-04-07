package com.bloxbean.algodea.idea.contracts.ui;

import com.bloxbean.algodea.idea.core.action.ui.TxnDialogWrapper;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AppTxnParamEntryDialog extends TxnDialogWrapper {
    AppTxnMainPanel appTxnMainPanel;

    public AppTxnParamEntryDialog(Project project, String title) throws DeploymentTargetNotConfigured {
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
    protected ValidationInfo doTransactionInputValidation() {
        return appTxnMainPanel.doValidate();
    }
}
