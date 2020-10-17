package com.bloxbean.algodea.idea.transaction.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TransferDialog extends DialogWrapper {
    private JPanel mainPanel;
    private TransferTxnParamEntryForm transferTxnForm;

    public TransferDialog(@Nullable Project project) {
        super(project, true);
        transferTxnForm.initializeData(project);
        init();
        setTitle("Transfer Algo");
    }

    public TransferTxnParamEntryForm getTransferTxnEntryForm() {
        return transferTxnForm;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        return transferTxnForm.doValidate();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        transferTxnForm = new TransferTxnParamEntryForm();
    }
}
