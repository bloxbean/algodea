package com.bloxbean.algodea.idea.contracts.ui;

import com.bloxbean.algodea.idea.core.action.ui.TxnDialogWrapper;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class UpdateAppDialog extends TxnDialogWrapper {
    private UpdateAppMainPanel updateAppMainPanel;

    public UpdateAppDialog(Project project, String contract) throws DeploymentTargetNotConfigured {
        super(project,  true);
        updateAppMainPanel = new UpdateAppMainPanel(project, contract);
        init();
        setTitle("UpdateApplication");
    }

    @Override
    protected ValidationInfo doTransactionInputValidation() {
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
