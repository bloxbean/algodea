package com.bloxbean.algodea.idea.dryrun.ui;

import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccountStateExportDialog extends DialogWrapper {
    private JPanel mainPanel;
    private JList selectAccountList;
    private JButton addButton;
    private JButton removeButton;
    private JScrollPane scrollPane;
    private DefaultListModel<String> listModel;

    public AccountStateExportDialog(@Nullable Project project) {
        super(project, true);
        init();
        setTitle("Export Accounts");
        attachListeners(project);
        setOKButtonText("Export");
    }

    private void attachListeners(Project project) {
        addButton.addActionListener(e -> {
            AlgoAccount account = AccountChooser.getSelectedAccount(project, true);
            if(account == null)
                return;

            listModel.addElement(account.getAddress());
        });

        removeButton.addActionListener(e -> {
            int index = selectAccountList.getSelectedIndex();
            if(index == -1) {
                Messages.showWarningDialog("Please select an account first to remove from the list.", "Remove");
                return;
            }

            listModel.remove(index);
        });
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        if(listModel.getSize() == 0) {
            return new ValidationInfo("Please add account(s) to export.", selectAccountList);
        }
        return null;
    }

    public List<String> getAccounts() {
        int size = listModel.getSize();
        if(size == 0)
            return Collections.EMPTY_LIST;

        List<String> accounts = new ArrayList<>();
        for(int i=0; i<size; i++) {
            accounts.add(listModel.get(i));
        }

        return accounts;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        listModel = new DefaultListModel<>();
        selectAccountList = new JBList(listModel);
        scrollPane = new JBScrollPane(selectAccountList);
    }
}
