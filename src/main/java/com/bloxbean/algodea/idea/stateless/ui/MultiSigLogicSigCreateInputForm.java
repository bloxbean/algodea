package com.bloxbean.algodea.idea.stateless.ui;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.MultisigAddress;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.account.service.AccountService;
import com.bloxbean.algodea.idea.core.util.AlgoAccountUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class MultiSigLogicSigCreateInputForm  {
    private JPanel contentPane;
    private JTextField thresholdTf;
    private JComboBox accountsCB;
    private JButton addOtherAccBtn;
    private JButton addAccountBtn;
    private JTextField otherAccountTf;
    private JButton removeAccBtn;
    private JScrollPane jscrollpane;
    private JLabel messageLabel;
    private JList selectAccsList;
    private JTextField multiSigTf;
    private JButton multisigAccountChooserBtn;
    private AccountService accountService;

    private DefaultComboBoxModel<AlgoAccount> accountComboBoxModel;
    private DefaultListModel<AlgoAccount> selectedAccountListModel;

    public MultiSigLogicSigCreateInputForm() {
    }

    public void initializeData(Project project) {
        accountService = AccountService.getAccountService(project);

        accountComboBoxModel = new DefaultComboBoxModel();
        accountsCB.setModel(accountComboBoxModel);

        multisigAccountChooserBtn.addActionListener(e -> {
            AlgoMultisigAccount algoMultisigAccount = AccountChooser.getSelectedMultisigAccount(project, true);
            if(algoMultisigAccount != null) {
                multiSigTf.setText(algoMultisigAccount.getAddress());
                thresholdTf.setText(algoMultisigAccount.getThreshold() + "");
                ApplicationManager.getApplication().invokeLater(() -> {
                    try {
                        List<String> accounts = algoMultisigAccount.getAccounts();
                        accountComboBoxModel.removeAllElements();

                        accountComboBoxModel.addElement(new AlgoAccount());
                        accounts.forEach(acc -> {
                            AlgoAccount algoAccount = accountService.getAccountByAddress(acc);
                            if (algoAccount != null) {
                                accountComboBoxModel.addElement(algoAccount);
                            }
                        });
                        messageLabel.setText("");
                    } catch (Exception ex) {
                        messageLabel.setText("Account loading failed !!!");
                    }
                }, ModalityState.stateForComponent(accountsCB));
            }
        });

        removeAccBtn.addActionListener( e -> {
            int selectedIndex = selectAccsList.getSelectedIndex();
            if(selectedIndex != -1) {
                selectedAccountListModel.remove(selectedIndex);
            }
        });

        addAccountBtn.addActionListener(e -> {
            clearMessage();

            addSelectedAccountToList();
        });

        addOtherAccBtn.addActionListener(e -> {
            clearMessage();

            String otherAccountMnemonic = otherAccountTf.getText();
            AlgoAccount algoAccount = accountService.getAccountFromMnemonic(otherAccountMnemonic);
            if(algoAccount == null) {
                setMessageLabel("Invalid mnemonic");
                return;
            }

            if(!selectedAccountListModel.contains(algoAccount)) {
                selectedAccountListModel.addElement(algoAccount);
                otherAccountTf.setText("");
            }
        });

        accountsCB.addActionListener(evt -> {
            addSelectedAccountToList();
            accountsCB.setSelectedIndex(0);
        });
    }

    private void setMessageLabel(String message) {
        messageLabel.setText(message);
    }

    private void clearMessage() {
        messageLabel.setText("");
    }


    private void addSelectedAccountToList() {
        AlgoAccount selectedAlgoAcc = (AlgoAccount)accountsCB.getSelectedItem();
        if(selectedAlgoAcc == null || StringUtil.isEmpty(selectedAlgoAcc.getAddress())) return;

        if(!selectedAccountListModel.contains(selectedAlgoAcc))
            selectedAccountListModel.addElement(selectedAlgoAcc);
    }

    protected @Nullable ValidationInfo doValidate() {
        return null;
    }

    public List<Account> getAccounts() throws GeneralSecurityException {
        List<Account> accounts = new ArrayList<>();
        Enumeration<AlgoAccount> enumeration = selectedAccountListModel.elements();
        while(enumeration.hasMoreElements()) {
            AlgoAccount account = enumeration.nextElement();
            if(account != null) {
                accounts.add(new Account(account.getMnemonic()));
            }
        }

        return accounts;
    }

    public MultisigAddress getMultisigAddress() throws NoSuchAlgorithmException {
        AlgoMultisigAccount algoMultisigAccount = accountService.getMultisigAccountByAddress(StringUtil.trim(multiSigTf.getText()));

        if(algoMultisigAccount == null)
            return null;

        return AlgoAccountUtil.getMultisigAddress(algoMultisigAccount);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        selectedAccountListModel = new DefaultListModel();
        selectAccsList = new JBList(selectedAccountListModel);
        jscrollpane = new JBScrollPane(selectAccsList);

        accountsCB = new ComboBox();
    }
}
