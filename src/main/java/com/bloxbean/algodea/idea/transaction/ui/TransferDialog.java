package com.bloxbean.algodea.idea.transaction.ui;

import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TransferDialog extends DialogWrapper {
    private JPanel mainPanel;
    private TransferTxnParamEntryForm transferTxnForm;
    private TransactionDtlsEntryForm transactionDtlsEntryForm;
    private JTabbedPane tabbedPane1;

    public TransferDialog(@Nullable Project project) throws DeploymentTargetNotConfigured {
        super(project, true);
        transferTxnForm.initializeData(project);
        transactionDtlsEntryForm.initializeData(project);
        init();
        setTitle("Transfer Algo");
    }

    public TransferTxnParamEntryForm getTransferTxnEntryForm() {
        return transferTxnForm;
    }

    public TransactionDtlsEntryForm getTransactionDtlsEntryForm() {
        return transactionDtlsEntryForm;
    }

//    @Override
//    protected @NotNull List<ValidationInfo> doValidateAll() {
//        ValidationInfo transferValidateInfo = transferTxnForm.doValidate();
//        ValidationInfo txnDtlValidateInfo = transactionDtlsEntryForm.doValidate();
//
//        List<ValidationInfo> validationInfos = null;
//
//        if(transferValidateInfo != null) {
//            validationInfos = new ArrayList<>();
//            validationInfos.add(transferValidateInfo);
//        }
//
//        if(txnDtlValidateInfo != null) {
//
//        }
//    }

    public boolean isAlgoTransfer() {
        return transferTxnForm.isAlgoTransfer();
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
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
