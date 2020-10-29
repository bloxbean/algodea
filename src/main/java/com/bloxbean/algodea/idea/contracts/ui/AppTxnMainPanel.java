package com.bloxbean.algodea.idea.contracts.ui;

import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AppTxnMainPanel {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private AppTxnBaseParamEntryForm appTxnBaseEntryForm;
    private AppTxnDetailsEntryForm appTxnDetailsEntryForm;
    private TransactionDtlsEntryForm txnDtlEntryForm;

    public AppTxnMainPanel(Project project) {
        initialize(project);
    }

    public AppTxnBaseParamEntryForm getAppTxnBaseEntryForm() {
        return appTxnBaseEntryForm;
    }

    public AppTxnDetailsEntryForm getAppTxnDetailsEntryForm() {
        return appTxnDetailsEntryForm;
    }

    public TransactionDtlsEntryForm getTxnDtlEntryForm() {
        return txnDtlEntryForm;
    }

    protected @Nullable List<ValidationInfo> doValidate() {

        ValidationInfo appTxnValidateInfo = appTxnBaseEntryForm.doValidate();
        ValidationInfo appTxnDetailsValidateInfo = appTxnDetailsEntryForm.doValidate();
        ValidationInfo txnDetailsValidateInfo = txnDtlEntryForm.doValidate();

        List<ValidationInfo> validationInfos = new ArrayList<>();

        if(appTxnValidateInfo != null) {
            validationInfos.add(appTxnValidateInfo);
        }

        if(appTxnDetailsValidateInfo != null) {
            validationInfos.add(appTxnDetailsValidateInfo);
        }

        if(txnDetailsValidateInfo != null) {
            validationInfos.add(txnDetailsValidateInfo);
        }

        return validationInfos;
    }

    public JComponent getMainPanel() {
        return mainPanel;
    }


    private void initialize(Project project) {
        appTxnBaseEntryForm.initializeData(project);
        appTxnDetailsEntryForm.initializeData(project);
        txnDtlEntryForm.initializeData(project);
    }
    private void createUIComponents() {
        // TODO: place custom component creation code here
        appTxnBaseEntryForm = new AppTxnBaseParamEntryForm();
        appTxnDetailsEntryForm = new AppTxnDetailsEntryForm();
        txnDtlEntryForm = new TransactionDtlsEntryForm();
    }
}
