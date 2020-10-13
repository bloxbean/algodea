package com.bloxbean.algodea.idea.contracts.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AppTxnMainPanel {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private AppTxnBaseParamEntryForm appTxnBaseEntryForm;
    private TxnDetailsEntryForm txnDetailsEntryForm;

    public AppTxnMainPanel(Project project) {
        initialize(project);
    }

    public AppTxnBaseParamEntryForm getAppTxnBaseEntryForm() {
        return appTxnBaseEntryForm;
    }

    public TxnDetailsEntryForm getTxnDetailsEntryForm() {
        return txnDetailsEntryForm;
    }

    protected @Nullable List<ValidationInfo> doValidate() {

        ValidationInfo appTxnValidateInfo = appTxnBaseEntryForm.doValidate();
        ValidationInfo txnDetailsValidateInfo = txnDetailsEntryForm.doValidate();

        List<ValidationInfo> validationInfos = new ArrayList<>();

        if(appTxnValidateInfo != null) {
            validationInfos.add(appTxnValidateInfo);
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
        txnDetailsEntryForm.initializeData(project);
    }
    private void createUIComponents() {
        // TODO: place custom component creation code here
        appTxnBaseEntryForm = new AppTxnBaseParamEntryForm();
        txnDetailsEntryForm = new TxnDetailsEntryForm();
    }
}
