package com.bloxbean.algodea.idea.contracts.ui;

import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class CreateAppDialog extends DialogWrapper {
    private CreateMainPanel createMainPanel;

    public CreateAppDialog(Project project,
                              AlgoAccount creatorAccount, String approvalProgram, String clearStateProgram,
                              int globalByteslices, int globalInts, int localByteslices, int localInts) {
        super(project);
        createMainPanel = new CreateMainPanel(project, creatorAccount, approvalProgram,
                clearStateProgram, globalByteslices, globalInts, localByteslices, localInts);
        init();
        setTitle("Create Stateful Smart Contract App");
    }

    @Override
    protected @NotNull List<ValidationInfo> doValidateAll() {
        return createMainPanel.doValidate();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return createMainPanel.getMainPanel();
    }

    public CreateAppEntryForm getCreateForm() {
        return createMainPanel.getCreateAppEntryForm();
    }

    public TxnDetailsEntryForm getTxnDetailsEntryForm() {
        return createMainPanel.getTxnDetailsEntryForm();
    }
}
