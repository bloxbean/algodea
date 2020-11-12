package com.bloxbean.algodea.idea.atomic.ui;

import com.algorand.algosdk.account.Account;
import com.bloxbean.algodea.idea.transaction.ui.AccountEntryInputForm;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SigningAccountInputDialog extends DialogWrapper {
    private AccountEntryInputForm accountEntryInputForm;
    private JPanel mainPanel;

    protected SigningAccountInputDialog(@Nullable Project project) {
        super(project, true);
        init();
        setTitle("Signing Account");
    }

    public Account getAccount() {
        return accountEntryInputForm.getAccount();
    }

    public AccountEntryInputForm getAccountEntryInputForm() {
        return accountEntryInputForm;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        accountEntryInputForm = new AccountEntryInputForm(true, false);
        accountEntryInputForm.setAccountLabel("Signing Account");
        accountEntryInputForm.setEnableMnemonic(true);
        accountEntryInputForm.setEnableMultiSig(false);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }
}
