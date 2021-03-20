package com.bloxbean.algodea.idea.stateless.ui;

import com.algorand.algosdk.account.Account;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBRadioButton;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class LogicSigSigningAccountForm {
    private JTextField accountTf;
    private JButton accountChooserBtn;
    private JPanel mainPanel;
    private JTextField mnemonicTf;
    private JRadioButton contractAccountRadioButton;
    private JRadioButton accountDelegationRadioButton;

    private ButtonGroup statelessContractTypeBtnGrp;
    private ChangeListener changeListener;

    protected LogicSigSigningAccountForm() {
        changeListener = new ChangeListener(){};
    }

    protected LogicSigSigningAccountForm(ChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public void initialize(Project project) {
        contractAccountRadioButton.addActionListener(e -> {
            if(contractAccountRadioButton.isSelected()) {
                enableDisableSignerAccountFields(false);
                changeListener.contractTypeSelected();
            }
        });

        accountDelegationRadioButton.addActionListener(e -> {
            if(accountDelegationRadioButton.isSelected()) {
                enableDisableSignerAccountFields(true);
                changeListener.delegationTypeSelect();
            }
        });

        contractAccountRadioButton.setSelected(true);
        enableDisableSignerAccountFields(false);

        accountChooserBtn.addActionListener(e -> {
            AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
            if(algoAccount != null) {
                accountTf.setText(algoAccount.getAddress());
                mnemonicTf.setText(algoAccount.getMnemonic());
                changeListener.signerAddressChanged(algoAccount.getAddress());
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

                    changeListener.signerAddressChanged(account.getAddress().toString());
                } catch (Exception ex) {
                    accountTf.setText("");
                }
            }
        });

    }

    private void enableDisableSignerAccountFields(boolean flag) {
        accountTf.setText("");
        accountTf.setEnabled(flag);
        accountChooserBtn.setEnabled(flag);
        mnemonicTf.setEnabled(flag);
    }

    public @Nullable ValidationInfo doValidate() {
        if(isAccountDelegationType()) {
            if (StringUtil.isEmpty(accountTf.getText())) {
                accountTf.setToolTipText("Please select a valid account or enter valid mnemonic");
                return new ValidationInfo("Please select a valid account or enter valid mnemonic", accountTf);
            } else {
                accountTf.setToolTipText("");
            }
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

    public boolean isContractAccountType() {
        return contractAccountRadioButton.isSelected();
    }

    public boolean isAccountDelegationType() {
        return accountDelegationRadioButton.isSelected();
    }

    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        statelessContractTypeBtnGrp = new ButtonGroup();
        contractAccountRadioButton = new JBRadioButton();
        accountDelegationRadioButton = new JBRadioButton();

        statelessContractTypeBtnGrp.add(contractAccountRadioButton);
        statelessContractTypeBtnGrp.add(accountDelegationRadioButton);
    }

    public interface ChangeListener {
        default public void signerAddressChanged(String address){};
        default public void contractTypeSelected(){};
        default public void delegationTypeSelect(){};
    }
}
