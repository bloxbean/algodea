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

package com.bloxbean.algodea.idea.account.ui;

import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountService;
import com.bloxbean.algodea.idea.configuration.action.ConfigurationAction;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.service.AlgoAccountService;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ListAccountDialog extends DialogWrapper {
    private JPanel mainPanel;
    private JTable accListTable;
    private JButton fetchBalanceButton;
    private JLabel messageLabel;
    private Project project;
    private AccountListTableModel tableModel;
    private boolean isRemote;
    private boolean showBalance;
    private AccountService accountService;
    private AlgoConsole console;

    public ListAccountDialog(Project project, boolean isRemote) {
        this(project, isRemote, true);
        console = AlgoConsole.getConsole(project);
    }

    public ListAccountDialog(Project project, boolean isRemote, boolean showBalance) {
        super(project, true);
        init();
        setTitle("Accounts (" + (isRemote ? "Remote Mode": "Embedded Mode") + ")");

        this.accountService = AccountService.getAccountService(project);
        this.project = project;
        this.isRemote = isRemote;
        this.showBalance = showBalance;

        initialize();

        if(showBalance) {
            try {
                //Right align balance column
                DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
                rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
                accListTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
            } catch (Exception e) {

            }
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
        if (tableModel.getAccounts() == null) return;

        if(isRemote) {
            AccountService accountListFetcher = new AccountService();
            AlgoConsole console = AlgoConsole.getConsole(project);
            console.clearAndshow();

            try {
                ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
                    @Override
                    public void run() {
                        ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
                        float counter = 0;
                        AlgoAccountService accountService = null;
                        try {
                             accountService = new AlgoAccountService(project, new LogListenerAdapter(console));
                        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
                            deploymentTargetNotConfigured.printStackTrace();
                            IdeaUtil.showNotification(project, "Algorand Configuration",
                                    "Algorand deployment node is not configured.", NotificationType.ERROR, ConfigurationAction.ACTION_ID);
                        }
                        if(accountService == null)
                            return;

                        for (AlgoAccount account : tableModel.getAccounts()) {
                            //TODO fetch balance
                            try {
                                Long balance = accountService.getBalance(account.getAddress());

                                progressIndicator.setFraction(counter++ / tableModel.getAccounts().size());
                                if(progressIndicator.isCanceled()) {
                                    break;
                                }

                                if (balance != null) {
                                    account.setBalance(balance);
                                }
                                tableModel.fireTableRowsUpdated((int)counter - 1, (int)counter-1);
                            } catch (Exception e) {
                                e.printStackTrace();
                                console.showErrorMessage("Error getting balance for account : " + account.getAddress());
                                console.showErrorMessage(e.getMessage());
                            }
//                           //TO BigInteger balance = accountListFetcher.getBalance(account, isRemote);
                        }
                        progressIndicator.setFraction(1.0);
                        tableModel.fireTableDataChanged();
                    }
                }, "Fetching balance from remote kernel ...", true, project);

            } finally {

            }
        } else { //For local Avm

        }
    }

    public AlgoAccount getSelectAccount() {

        int selectedRow = accListTable.getSelectedRow();
        if (selectedRow == -1)
            return null;
        else if (selectedRow <= tableModel.getAccounts().size() - 1) {
            return tableModel.getAccounts().get(selectedRow);
        } else {
            return null;
        }
    }

   /* public void updateAccount(List<AlgoAccount> accs) {
        if(accs == null) return;

        if(this.accounts == null)
            this.accounts = new ArrayList<>();

        this.accounts.clear();
        for(AlgoAccount account: accs) {
            this.accounts.add(account);
        }

        tableModel.fireTableDataChanged();
    }*/

    private void initialize() {
        tableModel = new AccountListTableModel(showBalance);
        accListTable.setModel(tableModel);

        messageLabel.setText("Loading account ...");
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                List<AlgoAccount> accs = accountService.getAccounts();
                tableModel.setElements(accs);
                messageLabel.setText("");
            } catch(Exception e) {
                messageLabel.setText("Account loading failed !!!");
            }

        }, ModalityState.stateForComponent(accListTable));
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
