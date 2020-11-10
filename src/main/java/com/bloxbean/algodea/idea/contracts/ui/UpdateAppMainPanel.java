package com.bloxbean.algodea.idea.contracts.ui;

import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class UpdateAppMainPanel {
    private JTabbedPane tabbedPane1;
    private JPanel mainPanel;
    private AppTxnBaseParamEntryForm appTxnBaseForm;
    private UpdateAppEntryForm updateAppEntryForm;
    private AppTxnDetailsEntryForm appTxnDetailsEntryForm;
    private TransactionDtlsEntryForm txnDetailsEntryForm;

    public UpdateAppMainPanel(Project project, String contract) {
        initialize(project, contract);
    }

    private void initialize(Project project, String contract) {
        appTxnBaseForm.initializeData(project);
        updateAppEntryForm.initializeData(project, contract );
        appTxnDetailsEntryForm.initializeData(project);
        txnDetailsEntryForm.initializeData(project);
    }

    public AppTxnBaseParamEntryForm getAppTxnBaseEntryForm() {
        return appTxnBaseForm;
    }

    public UpdateAppEntryForm getUpdateAppEntryForm() {
        return updateAppEntryForm;
    }

    public AppTxnDetailsEntryForm getAppTxnDetailsEntryForm() {
        return appTxnDetailsEntryForm;
    }

    public TransactionDtlsEntryForm getTxnDetailsEntryForm() {
        return txnDetailsEntryForm;
    }

    protected @Nullable ValidationInfo doValidate() {
        ValidationInfo appTxnValidateInfo = appTxnBaseForm.doValidate();
        if(appTxnValidateInfo != null) {
            return appTxnValidateInfo;
        }

        ValidationInfo updateAppEntryValidateInfo = updateAppEntryForm.doValidate();
        if(updateAppEntryValidateInfo != null) {
            return updateAppEntryValidateInfo;
        }

        ValidationInfo appTxnDetailsValidateInfo = appTxnDetailsEntryForm.doValidate();
        if(appTxnDetailsValidateInfo != null) {
           return appTxnDetailsValidateInfo;
        }

        ValidationInfo txnDetailsValidateInfo = txnDetailsEntryForm.doValidate();
        if(txnDetailsValidateInfo != null) {
            return txnDetailsValidateInfo;
        }

        return null;
    }

    public JComponent getMainPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        appTxnBaseForm = new AppTxnBaseParamEntryForm();
        updateAppEntryForm = new UpdateAppEntryForm();
        appTxnDetailsEntryForm = new AppTxnDetailsEntryForm();
        txnDetailsEntryForm = new TransactionDtlsEntryForm();
    }
}
