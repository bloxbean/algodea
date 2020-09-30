/*
 * Copyright (c) 2020 BloxBean Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.bloxbean.algorand.idea.account.ui;

import com.bloxbean.algorand.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algorand.idea.account.service.AccountService;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ListMultisigAccountDialog extends DialogWrapper {
    private JPanel mainPanel;
    private JTable accListTable;
    private JButton fetchBalanceButton;
    private JList accountList;
    private JTextField multisigAccTf;
    private JTextField thresholdTf;
    private JLabel messageLabel;
    private Project project;
    private List<AlgoMultisigAccount> accounts;
    private MultisigAccountListTableModel tableModel;
    private boolean isRemote;
    private boolean showBalance;
    private AccountService accountService;
    private DefaultListModel defaultAccountsListModel;

    public ListMultisigAccountDialog(Project project) {
        this(project, true);
    }

    public ListMultisigAccountDialog(Project project, boolean showBalance) {
        super(project, true);
        init();
        this.accountService = AccountService.getAccountService(project);
        this.project = project;
        this.showBalance = showBalance;
        setTitle("Multi-Signature Accounts");

        initialize();

        if(showBalance) {
////            Right align balance column
//            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
//            rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
//            accListTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        } else {
            fetchBalanceButton.setVisible(false);
        }

        fetchBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchBalance(isRemote);
            }
        });
    }

    public void fetchBalance(boolean isRemote) {
//        if (accounts == null) return;
//
//        if(isRemote) {
//            AccountService accountListFetcher = new AccountService();
//
//            try {
//                ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
//                    @Override
//                    public void run() {
//                        ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
//                        float counter = 0;
//                        for (AlgoAccount account : accounts) {
//                            //TODO fetch balance
////                           //TO BigInteger balance = accountListFetcher.getBalance(account, isRemote);
////                            progressIndicator.setFraction(counter++ / accounts.size());
////                            if (balance != null) {
////                                account.setBalance(balance);
////                            }
//                        }
//                        progressIndicator.setFraction(1.0);
//                        tableModel.fireTableDataChanged();
//                    }
//                }, "Fetching balance from remote kernel ...", true, project);
//
//            } finally {
//
//            }
//        } else {
//
//        }
    }

    public AlgoMultisigAccount getSelectAccount() {
        if (accounts == null)
            return null;

        int selectedRow = accListTable.getSelectedRow();
        if (selectedRow == -1)
            return null;
        else if (selectedRow <= accounts.size() - 1) {
            return accounts.get(selectedRow);
        } else {
            return null;
        }
    }

    private void initialize() {
        tableModel = new MultisigAccountListTableModel(showBalance);
        accListTable.setModel(tableModel);
        accListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        multisigAccTf.setEditable(false);
        thresholdTf.setEditable(false);

        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                accounts = accountService.getMultisigAccounts();
                tableModel.setElements(accounts);
            } catch(Exception e) {
                messageLabel.setText("Account loading failed !!!");
            }

            messageLabel.setText("");
        }, ModalityState.stateForComponent(accListTable));

        ListSelectionModel listSelectionModel = accListTable.getSelectionModel();
        listSelectionModel.addListSelectionListener(e -> {
            int index = accListTable.getSelectedRow();
            if(index != -1 && index <= accounts.size() - 1) {
                AlgoMultisigAccount multisigAccount = accounts.get(index);
                multisigAccTf.setText(multisigAccount.getAddress());
                thresholdTf.setText(multisigAccount.getThreshold() + "");
                defaultAccountsListModel.clear();
                multisigAccount.getAccounts().stream()
                        .forEach(acc -> defaultAccountsListModel.addElement(acc));
            } else {
                defaultAccountsListModel.clear();
            }
        });
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        defaultAccountsListModel = new DefaultListModel();
        accountList = new JBList(defaultAccountsListModel);
    }
}
