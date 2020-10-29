package com.bloxbean.algodea.idea.contracts.ui;

import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class CreateMainPanel {
    private JTabbedPane tabbedPane1;
    private JPanel mainPanel;
    private CreateAppEntryForm createAppEntryForm;
    private AppTxnDetailsEntryForm appTxnDetailsEntryForm;
    private TransactionDtlsEntryForm txnDtlEntryForm;

    public CreateMainPanel(Project project, AlgoAccount creatorAccount, String contractName) {
        initialize(project, creatorAccount, contractName);
    }

    private void initialize(Project project, AlgoAccount creatorAccount, String contractName) {
        createAppEntryForm.initializeData(project, creatorAccount, contractName);
        appTxnDetailsEntryForm.initializeData(project);
        txnDtlEntryForm.initializeData(project);
    }

    public CreateAppEntryForm getCreateAppEntryForm() {
        return createAppEntryForm;
    }

    public AppTxnDetailsEntryForm getAppTxnDetailsEntryForm() {
        return appTxnDetailsEntryForm;
    }

    public TransactionDtlsEntryForm getTxnDetailsEntryForm() {
        return txnDtlEntryForm;
    }

    protected @Nullable List<ValidationInfo> doValidate() {

        ValidationInfo updateAppEntryValidateInfo = createAppEntryForm.doValidate();
        ValidationInfo appTxnDetailsValidateInfo = appTxnDetailsEntryForm.doValidate();
        ValidationInfo txnDetailsValidateInfo = txnDtlEntryForm.doValidate();

        List<ValidationInfo> validationInfos = new ArrayList<>();
        if(updateAppEntryValidateInfo != null) {
            validationInfos.add(updateAppEntryValidateInfo);
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
    private void createUIComponents() {
        // TODO: place custom component creation code here
        createAppEntryForm = new CreateAppEntryForm();
        appTxnDetailsEntryForm = new AppTxnDetailsEntryForm();
        txnDtlEntryForm = new TransactionDtlsEntryForm();
    }
}
