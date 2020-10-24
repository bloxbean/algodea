package com.bloxbean.algodea.idea.transaction.ui;

import com.algorand.algosdk.account.Account;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.util.StringUtility;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class AccountEntryInputForm {
    private JPanel mainPanel;
    private JTextField accountTf;
    private JButton accountChooserBtn;
    private JButton multisigAccChooserBtn;
    private JTextField mnemonicTf;
    private JLabel accountLabel;
    private JLabel menmonicLabel;
    private JLabel orLabel;

    private boolean enableMnemonic = true;
    private boolean enableMultiSig = true;

    private boolean isMandatory;

    public AccountEntryInputForm() {

    }

    public AccountEntryInputForm(boolean mandatory, boolean enableMultiSig) {
        this.isMandatory = mandatory;
        this.enableMultiSig = enableMultiSig;
    }

    public void initializeData(Project project) {

        if(!enableMnemonic) {
            menmonicLabel.setVisible(false);
            mnemonicTf.setVisible(false);
            orLabel.setVisible(false);
        }

        if(!enableMultiSig) {
            multisigAccChooserBtn.setEnabled(false);
            multisigAccChooserBtn.setVisible(false);
        }

        attachedListeners(project);
    }

    private void attachedListeners(Project project) {
        accountChooserBtn.addActionListener(e -> {
            AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
            if (algoAccount != null) {
                accountTf.setText(algoAccount.getAddress());
                mnemonicTf.setText(algoAccount.getMnemonic());
            }
        });

        if(enableMultiSig) {
            multisigAccChooserBtn.addActionListener(e -> {
                AlgoMultisigAccount algoMultisigAccount = AccountChooser.getSelectedMultisigAccount(project, true);
                if (algoMultisigAccount != null) {
                    accountTf.setText(algoMultisigAccount.getAddress());
                }
            });
        }

        if(enableMnemonic) {
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
    }

    public @Nullable ValidationInfo doValidate() {

        if(isMandatory) {
            if (StringUtil.isEmpty(accountTf.getText())) {
                accountTf.setToolTipText("Please select a valid account or enter valid mnemonic");
                return new ValidationInfo("Please select a valid account or enter valid mnemonic", accountTf);
            } else {
                accountTf.setToolTipText("");
            }
        }

        return null;
    }

    public void setAccountLabel(String label) {
        accountLabel.setToolTipText(label);
        accountLabel.setText(StringUtility.padLeft(label, 20));
    }

    public void setMenmonicLabel(String label) {
        mnemonicTf.setText(StringUtility.padLeft(label, 20));
    }

    public void setEnableMnemonic(boolean flag) {
        enableMnemonic = flag;
    }

    public void setEnableMultiSig(boolean flag) {
        enableMultiSig = flag;
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

    public void setMnemonic(String mnemonic) {
        if(StringUtil.isEmpty(mnemonic))
            return;

        mnemonicTf.setText(mnemonic);
        try {
            Account account = new Account(mnemonic);
            if(account != null)
                accountTf.setText(account.getAddress().toString());
        } catch (Exception e) {

        }
    }

}
