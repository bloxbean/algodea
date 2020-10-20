package com.bloxbean.algodea.idea.transaction.ui;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.account.service.AccountService;
import com.bloxbean.algodea.idea.util.StringUtility;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ManagedAccountEntryInputForm {
    private JPanel mainPanel;
    private JTextField accountTf;
    private JButton accountChooserBtn;
    private JButton multisigAccChooserBtn;
    private JLabel accountLabel;

    private boolean enableMultiSig = true;

    private boolean isMandatory;

    public ManagedAccountEntryInputForm() {

    }

    public ManagedAccountEntryInputForm(boolean mandatory, boolean enableMultiSig) {
        this.isMandatory = mandatory;
        this.enableMultiSig = enableMultiSig;
    }

    public void initializeData(Project project) {

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
        label = StringUtility.padLeft(label, 20);
        accountLabel.setText(label);
    }

    public void setEnableMultiSig(boolean flag) {
        enableMultiSig = flag;
    }

    public Account getAccount() {
        try {
            AlgoAccount algoAccount
                    = AccountService.getAccountService().getAccountByAddress(StringUtil.trim(accountTf.getText()));
            Account account = new Account(algoAccount.getMnemonic());
            return account;
        } catch (Exception e) {
            return null;
        }
    }

    public Address getAddress() {
        if(StringUtil.isEmpty(accountTf.getText()))
            return null;

        try {
            Address address = new Address(StringUtil.trim(accountTf.getText()));
            return address;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        accountLabel = new JLabel(StringUtility.padLeft("Account", 25));
    }
}
