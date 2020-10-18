package com.bloxbean.algodea.idea.stateless.ui;

import com.algorand.algosdk.account.Account;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class LogicSigSigningAccountForm {
    private JTextField accountTf;
    private JButton accountChooserBtn;
    private JPanel mainPanel;
    private JTextField mnemonicTf;

    protected LogicSigSigningAccountForm() {

    }

    public void initialize(Project project) {
        accountChooserBtn.addActionListener(e -> {
            AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
            if(algoAccount != null) {
                accountTf.setText(algoAccount.getAddress());
                mnemonicTf.setText(algoAccount.getMnemonic());
            }
        });

        mnemonicTf.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                String mnemonic = mnemonicTf.getText();
                try {
                    Account account = new Account(mnemonic);
                    accountTf.setText(account.getAddress().toString());
                } catch (Exception ex) {
                    accountTf.setText("");
                }
            }
        });

    }

    public @Nullable ValidationInfo doValidate() {
        if(StringUtil.isEmpty(accountTf.getText())) {
            accountTf.setToolTipText("Please select a valid account or enter valid mnemonic");
            return new ValidationInfo("Please select a valid account or enter valid mnemonic", accountTf);
        } else {
            accountTf.setToolTipText("");
        }

        return null;
    }

    public Account getAccount() {
        String mnemonic = mnemonicTf.getText().trim();
        try {
            Account account = new Account(mnemonic);
            return account;
        } catch (Exception e) {
            return null;
        }
    }

    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }
}
