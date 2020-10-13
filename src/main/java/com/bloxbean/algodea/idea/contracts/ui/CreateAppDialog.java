package com.bloxbean.algodea.idea.contracts.ui;

import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CreateAppDialog extends DialogWrapper {
    private CreateAppEntryForm createAppEntryForm;

    public CreateAppDialog(Project project,
                              AlgoAccount creatorAccount, String approvalProgram, String clearStateProgram,
                              int globalByteslices, int globalInts, int localByteslices, int localInts) {
        super(project);
        createAppEntryForm = new CreateAppEntryForm(project, creatorAccount, approvalProgram,
                clearStateProgram, globalByteslices, globalInts, localByteslices, localInts);
        init();
        setTitle("Create Stateful Smart Contract App");
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        return createAppEntryForm.doValidate();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return createAppEntryForm.getMainPanel();
    }

    public CreateAppEntryForm getCreateForm() {
        return createAppEntryForm;
    }
}
