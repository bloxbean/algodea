package com.bloxbean.algodea.idea.transaction.ui;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
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
    private JTextField signerAccountTf;
    private JButton signerAccountChooserBtn;
    private JButton multisigSignerAccChooserBtn;
    private JTextField mnemonicTf;
    private JLabel accountLabel;
    private JLabel menmonicLabel;
    private JLabel orLabel;
    private JTextField senderAddressTf;
    private JButton senderAddressChooser;
    private JButton senderAddressMultiSigChooser;
    private JLabel senderAddressLabel;

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
            multisigSignerAccChooserBtn.setEnabled(false);
            multisigSignerAccChooserBtn.setVisible(false);
        }

        attachedListeners(project);
    }

    private void attachedListeners(Project project) {
        signerAccountChooserBtn.addActionListener(e -> {
            AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
            if (algoAccount != null) {
                signerAccountTf.setText(algoAccount.getAddress());
                mnemonicTf.setText(algoAccount.getMnemonic());
                senderAddressTf.setText(algoAccount.getAddress());
            }
        });

        if(enableMultiSig) {
            multisigSignerAccChooserBtn.addActionListener(e -> {
                AlgoMultisigAccount algoMultisigAccount = AccountChooser.getSelectedMultisigAccount(project, true);
                if (algoMultisigAccount != null) {
                    signerAccountTf.setText(algoMultisigAccount.getAddress());
                    senderAddressTf.setText(algoMultisigAccount.getAddress());
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
                        signerAccountTf.setText(account.getAddress().toString());
                        senderAddressTf.setText(account.getAddress().toString());
                    } catch (Exception ex) {
                        signerAccountTf.setText("");
                        senderAddressTf.setText("");
                    }
                }
            });
        }

        senderAddressChooser.addActionListener(e -> {
            AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
            if (algoAccount != null) {
                senderAddressTf.setText(algoAccount.getAddress());
            }
        });

        senderAddressMultiSigChooser.addActionListener(e -> {
            AlgoMultisigAccount algoMultisigAccount = AccountChooser.getSelectedMultisigAccount(project, true);
            if (algoMultisigAccount != null) {
                senderAddressTf.setText(algoMultisigAccount.getAddress());
            }
        });
    }

    public @Nullable ValidationInfo doValidate() {

        if(isMandatory) {
            if (StringUtil.isEmpty(signerAccountTf.getText())) {
                signerAccountTf.setToolTipText("Please select a valid account or enter valid mnemonic");
                return new ValidationInfo("Please select a valid account or enter valid mnemonic", signerAccountTf);
            } else {
                signerAccountTf.setToolTipText("");
            }

            if (StringUtil.isEmpty(senderAddressTf.getText())) {
                senderAddressTf.setToolTipText("Please select a valid sender address");
                return new ValidationInfo("Please select a valid sender address", senderAddressTf);
            } else {
                senderAddressTf.setToolTipText("");
            }
        }

        return null;
    }

    public void setSigningAccountLabel(String label) {
        accountLabel.setToolTipText(label);
        accountLabel.setText(StringUtility.padLeft(label, 20));
    }

    public void setMenmonicLabel(String label) {
        menmonicLabel.setText(StringUtility.padLeft(label, 20));
    }

    public void setSenderAddressLabel(String label) {
        senderAddressLabel.setText(StringUtility.padLeft(label, 20));
    }

    public void setEnableMnemonic(boolean flag) {
        enableMnemonic = flag;
    }

    public void setEnableMultiSig(boolean flag) {
        enableMultiSig = flag;
    }

    public void disableSenderAddressFields() {
        senderAddressTf.setEnabled(false);
        senderAddressChooser.setEnabled(false);
        senderAddressMultiSigChooser.setEnabled(false);
    }

    public Account getSignerAccount() {
        String mnemonic = mnemonicTf.getText().trim();
        try {
            Account account = new Account(mnemonic);
            return account;
        } catch (Exception e) {
            return null;
        }
    }

    public Address getSenderAddress() {
        String address = senderAddressTf.getText().trim();
        try {
            return new Address(address);
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
            if(account != null) {
                signerAccountTf.setText(account.getAddress().toString());
                senderAddressTf.setText(account.getAddress().toString());
            }
        } catch (Exception e) {

        }
    }

}
