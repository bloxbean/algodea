package com.bloxbean.algodea.idea.atomic.ui;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.bloxbean.algodea.idea.transaction.ui.AccountEntryInputForm;
import com.bloxbean.algodea.idea.util.StringUtility;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class SigningAccountInputDialog extends DialogWrapper {
    private AccountEntryInputForm accountEntryInputForm;
    private JPanel mainPanel;

    private LogicSigChooser logicSigChooser;
    private JPanel cardPanel;
    private JComboBox signerTypeCB;
    private JPanel accountChooserPanel;
    private JPanel logicChooserPanel;
    private JLabel signByLabel;

    private String ACCOUNT_TYPE = "Account";
    private String LSIG_TYPE = "Logic Sig file";

    protected SigningAccountInputDialog(@Nullable Project project, Module module) {
        super(project, true);
        init();
        setTitle("Signing Account");
        initializeComponents();
        accountEntryInputForm.initializeData(project);
        logicSigChooser.initialize(project, module);

        attachSigTypeListeners();
    }

    private void initializeComponents() {
        signByLabel.setText(StringUtility.padLeft("Sign using", 20));
        cardPanel.add(accountChooserPanel, ACCOUNT_TYPE);
        cardPanel.add(logicChooserPanel, LSIG_TYPE);

        setOKButtonText("Sign");
    }

    private void attachSigTypeListeners() {
        signerTypeCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                CardLayout cardLayout = (CardLayout)cardPanel.getLayout();
//
                cardLayout.show(cardPanel, (String)e.getItem());
            }
        });
    }

    public boolean isAccountType() {
        if(ACCOUNT_TYPE.equals(signerTypeCB.getSelectedItem())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLsigType() {
        if(LSIG_TYPE.equals(signerTypeCB.getSelectedItem())) {
            return true;
        } else {
            return false;
        }
    }

    public Account getAccount() {
        if(isAccountType()) {
            return accountEntryInputForm.getSignerAccount();
        } else {
            return null;
        }
    }

    public LogicsigSignature getLogicSignature() {
        if(isLsigType()) {
            return logicSigChooser.getLogicsigSignature();
        } else {
            return null;
        }
    }

    public AccountEntryInputForm getAccountEntryInputForm() {
        return accountEntryInputForm;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        accountEntryInputForm = new AccountEntryInputForm(true, false);
        accountEntryInputForm.setSigningAccountLabel("Account");
        accountEntryInputForm.setMnemonic("Mnemonic");
        accountEntryInputForm.setEnableMnemonic(true);
        accountEntryInputForm.setEnableMultiSig(false);

        logicSigChooser = new LogicSigChooser();

       // cardPanel = new JBPanel<>(new CardLayout());

        signerTypeCB = new ComboBox();
        signerTypeCB.addItem(ACCOUNT_TYPE);
        signerTypeCB.addItem(LSIG_TYPE);

    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }
}
