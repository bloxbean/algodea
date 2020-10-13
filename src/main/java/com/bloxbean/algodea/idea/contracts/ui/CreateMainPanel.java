package com.bloxbean.algodea.idea.contracts.ui;

import com.bloxbean.algodea.idea.account.model.AlgoAccount;
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
    private TxnDetailsEntryForm txnDetailsEntryForm;

    public CreateMainPanel(Project project, AlgoAccount creatorAccount, String approvalProgram, String clearStateProgram,
                           int globalByteslices, int globalInts, int localByteslices, int localInts) {
        initialize(project, creatorAccount, approvalProgram,
                clearStateProgram, globalByteslices, globalInts, localByteslices, localInts);
    }

    private void initialize(Project project, AlgoAccount creatorAccount, String approvalProgram, String clearStateProgram,
                            int globalByteslices, int globalInts, int localByteslices, int localInts) {
        createAppEntryForm.initializeData(project, creatorAccount, approvalProgram,
                clearStateProgram, globalByteslices, globalInts, localByteslices, localInts);
        txnDetailsEntryForm.initializeData(project);
    }

    public CreateAppEntryForm getCreateAppEntryForm() {
        return createAppEntryForm;
    }

    public TxnDetailsEntryForm getTxnDetailsEntryForm() {
        return txnDetailsEntryForm;
    }

    protected @Nullable List<ValidationInfo> doValidate() {

        ValidationInfo updateAppEntryValidateInfo = createAppEntryForm.doValidate();
        ValidationInfo txnDetailsValidateInfo = txnDetailsEntryForm.doValidate();

        List<ValidationInfo> validationInfos = new ArrayList<>();
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
        createAppEntryForm = new CreateAppEntryForm();
        txnDetailsEntryForm = new TxnDetailsEntryForm();
    }
}
