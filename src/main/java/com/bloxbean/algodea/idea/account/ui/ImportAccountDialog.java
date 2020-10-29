package com.bloxbean.algodea.idea.account.ui;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ImportAccountDialog extends DialogWrapper {
    private JPanel mainPanel;
    private JTextField mnemonicTf;
    private JTextField accountTf;
    private JCheckBox readOnlyAccount;
    private JTextField accountNameTf;

    protected ImportAccountDialog() {
        super(false);
        init();
        setTitle("Import External Account");
        attachListener();

        accountTf.setEditable(false);
    }

    private void attachListener() {

        readOnlyAccount.addActionListener(e -> {
            if(readOnlyAccount.isSelected()) {
                mnemonicTf.setText("");
                mnemonicTf.setEditable(false);
                accountTf.setEditable(true);
            } else {
                accountTf.setEditable(false);
                mnemonicTf.setEditable(true);
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

    @Override
    protected @Nullable ValidationInfo doValidate() {
        if(StringUtil.isEmpty(accountNameTf.getText())) {
            return new ValidationInfo("Account name cannot be empty", accountNameTf);
        }

        if (readOnlyAccount.isSelected()) {
            if (StringUtil.isEmpty(accountTf.getText()))
                return new ValidationInfo("Enter a valid address", accountTf);

            try {
                Address  address = new Address(accountTf.getText());
            } catch (Exception e) {
                return new ValidationInfo("Invalid address", accountTf);
            }

            return null;
        } else {
            if (StringUtil.isEmpty(accountTf.getText()))
                return new ValidationInfo("Enter a valid mnemonic phrase", accountTf);

            if (StringUtil.isEmpty(mnemonicTf.getText()))
                return new ValidationInfo("Enter a valid mnemonic phrase", mnemonicTf);
        }

        return null;
    }

    private Account deriveAccountFromMnemonic() {
        String mnemonic = mnemonicTf.getText().trim();
        try {
            Account account = new Account(mnemonic);
            return account;
        } catch (Exception e) {
            return null;
        }
    }

    public AlgoAccount getAccount() {
        if(readOnlyAccount.isSelected()) {
            AlgoAccount algoAccount = new AlgoAccount(StringUtil.trim(accountTf.getText()));
            algoAccount.setName(accountNameTf.getText());
            return algoAccount;
        } else {
            Account account = deriveAccountFromMnemonic();
            if(account == null)
                return null;

            AlgoAccount algoAccount = new AlgoAccount(account.getAddress().toString(), account.toMnemonic());
            algoAccount.setName(accountNameTf.getText());
            return algoAccount;
        }
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }
}
