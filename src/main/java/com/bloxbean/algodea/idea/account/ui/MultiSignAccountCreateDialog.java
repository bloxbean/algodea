package com.bloxbean.algodea.idea.account.ui;

import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountService;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

public class MultiSignAccountCreateDialog extends DialogWrapper {
    private JPanel contentPane;
    private JTextField thresholdTf;
    private JComboBox accountsCB;
    private JButton addOtherAccBtn;
    private JTextField otherAccountTf;
    private JButton removeAccBtn;
    private JScrollPane jscrollpane;
    private JLabel messageLabel;
    private JComboBox thresholdCB;
    private JList selectAccsList;
    private AccountService accountService;

    private DefaultListModel<AlgoAccount> selectedAccountListModel;
    private DefaultComboBoxModel<Integer> thresholdCBModel;

    public MultiSignAccountCreateDialog(@Nullable Project project) {
        super(project, true);
        accountService = AccountService.getAccountService();
        init();
        attachActionHandlers();
        populateData();
    }

    private void attachActionHandlers() {
        removeAccBtn.addActionListener( e -> {
            int selectedIndex = selectAccsList.getSelectedIndex();
            if(selectedIndex != -1) {
                selectedAccountListModel.remove(selectedIndex);
            }
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
    }

    private void setMessageLabel(String message) {
        messageLabel.setText(message);
    }

    private void clearMessage() {
        messageLabel.setText("");
    }

    private void populateData() {

        for(int i=2; i <= 50; i++)
            thresholdCBModel.addElement(i);

        messageLabel.setText("Loading available accounts ...");
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                List<AlgoAccount> accounts = accountService.getAccounts();

                accountsCB.addItem(new AlgoAccount()); //Empty account

                accounts.forEach(acc -> accountsCB.addItem(acc));
                messageLabel.setText("");
            } catch(Exception e) {
                messageLabel.setText("Account loading failed !!!");
            }

            accountsCB.addActionListener(evt -> {
                addSelectedAccountToList();
                accountsCB.setSelectedIndex(0);
            });
        }, ModalityState.stateForComponent(accountsCB));
    }

    private void addSelectedAccountToList() {
        AlgoAccount selectedAlgoAcc = (AlgoAccount)accountsCB.getSelectedItem();
        if(selectedAlgoAcc == null || StringUtil.isEmpty(selectedAlgoAcc.getAddress())) return;

        if(!selectedAccountListModel.contains(selectedAlgoAcc))
            selectedAccountListModel.addElement(selectedAlgoAcc);
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        if(selectedAccountListModel.getSize() < getThreshold()) {
            ValidationInfo validationInfo =
                    new ValidationInfo("No of accounts in a multisig account cannot be less than threshold.", selectAccsList);
            return validationInfo;
        }
        return null;
    }

    @Override
    protected void doOKAction() {
        clearMessage();
        startTrackingValidation();
        super.doOKAction();
    }

    public List<AlgoAccount> getAccounts() {
        List<AlgoAccount> accounts = new ArrayList<>();
        Enumeration<AlgoAccount> enumeration = selectedAccountListModel.elements();
        while(enumeration.hasMoreElements()) {
            AlgoAccount account = enumeration.nextElement();
            if(account != null)
                accounts.add(account);
        }

        return accounts;
    }

    public int getThreshold() {
        Integer threshold = (Integer)thresholdCBModel.getSelectedItem();
        if(threshold == null)
            return 0;
        else
            return threshold.intValue();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        setTitle("Multisig Account");
        selectedAccountListModel = new DefaultListModel();
        selectAccsList = new JBList(selectedAccountListModel);
        jscrollpane = new JBScrollPane(selectAccsList);

        thresholdCBModel = new DefaultComboBoxModel();
        thresholdCB = new ComboBox(thresholdCBModel);

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.setGroupingUsed(false);
        thresholdTf = new JFormattedTextField(decimalFormat);
        thresholdTf.setColumns(2);
    }
}
