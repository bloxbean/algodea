package com.bloxbean.algodea.idea.transaction.ui;

import com.bloxbean.algodea.idea.core.action.ui.TxnDialogWrapper;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TransferDialog extends TxnDialogWrapper {
    private JPanel mainPanel;
    private TransferTxnParamEntryForm transferTxnForm;
    private TransactionDtlsEntryForm transactionDtlsEntryForm;
    private JTabbedPane tabbedPane1;

    public TransferDialog(@Nullable Project project) throws DeploymentTargetNotConfigured {
        super(project, true);
        transferTxnForm.initializeData(project);
        transactionDtlsEntryForm.initializeData(project);
        init();
        setTitle("Transfer");
    }

    public TransferTxnParamEntryForm getTransferTxnEntryForm() {
        return transferTxnForm;
    }

    public TransactionDtlsEntryForm getTransactionDtlsEntryForm() {
        return transactionDtlsEntryForm;
    }

    public boolean isAlgoTransfer() {
        return transferTxnForm.isAlgoTransfer();
    }

    @Override
    protected ValidationInfo doTransactionInputValidation() {
        ValidationInfo validatedInfo = transferTxnForm.doValidate();
        if( validatedInfo == null)
            return transactionDtlsEntryForm.doValidate();
        else
            return validatedInfo ;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        transferTxnForm = new TransferTxnParamEntryForm();
        transactionDtlsEntryForm = new TransactionDtlsEntryForm();
    }
}
