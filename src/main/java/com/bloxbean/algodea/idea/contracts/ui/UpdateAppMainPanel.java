package com.bloxbean.algodea.idea.contracts.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateAppMainPanel {
    private JTabbedPane tabbedPane1;
    private JPanel mainPanel;
    private AppTxnBaseParamEntryForm appTxnBaseForm;
    private UpdateAppEntryForm updateAppEntryForm;
    private TxnDetailsEntryForm txnDetailsEntryForm;

    public UpdateAppMainPanel(Project project, String contract) {
        initialize(project, contract);
    }

    private void initialize(Project project, String contract) {
        appTxnBaseForm.initializeData(project);
        updateAppEntryForm.initializeData(project, contract );
        txnDetailsEntryForm.initializeData(project);
    }

    public AppTxnBaseParamEntryForm getAppTxnBaseEntryForm() {
        return appTxnBaseForm;
    }

    public UpdateAppEntryForm getUpdateAppEntryForm() {
        return updateAppEntryForm;
    }

    public TxnDetailsEntryForm getTxnDetailsEntryForm() {
        return txnDetailsEntryForm;
    }

    protected @Nullable List<ValidationInfo> doValidate() {

        ValidationInfo appTxnValidateInfo = appTxnBaseForm.doValidate();
        ValidationInfo updateAppEntryValidateInfo = updateAppEntryForm.doValidate();
        ValidationInfo txnDetailsValidateInfo = txnDetailsEntryForm.doValidate();

        List<ValidationInfo> validationInfos = new ArrayList<>();

        if(appTxnValidateInfo != null) {
            validationInfos.add(appTxnValidateInfo);
        }
        if(updateAppEntryValidateInfo != null) {
            validationInfos.add(updateAppEntryValidateInfo);
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
        appTxnBaseForm = new AppTxnBaseParamEntryForm();
        updateAppEntryForm = new UpdateAppEntryForm();
        txnDetailsEntryForm = new TxnDetailsEntryForm();
    }
}
