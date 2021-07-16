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

import com.bloxbean.algodea.idea.account.action.CreateMultisigAccountAction;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algodea.idea.account.service.AccountService;
import com.bloxbean.algodea.idea.configuration.action.ConfigurationAction;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.service.AlgoAccountService;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static java.awt.event.MouseEvent.BUTTON1;

public class ListMultisigAccountDialog extends DialogWrapper {
    private JPanel mainPanel;
    private JTable accListTable;
    private JButton fetchBalanceButton;
    private JList accountList;
    private JTextField multisigAccTf;
    private JTextField thresholdTf;
    private JLabel messageLabel;
    private JButton newBtn;
    private Project project;
    private MultisigAccountListTableModel tableModel;
    private boolean isRemote;
    private boolean showBalance;
    private AccountService accountService;
    private DefaultListModel defaultAccountsListModel;
    private AlgoConsole console;

    public ListMultisigAccountDialog(Project project) {
        this(project, true);
        console = AlgoConsole.getConsole(project);
    }

    public ListMultisigAccountDialog(Project project, boolean showBalance) {
        super(project, true);
        init();
        this.accountService = AccountService.getAccountService();
        this.project = project;
        this.showBalance = showBalance;
        setTitle("Multi-Signature Accounts");

        initialize();

        if(showBalance) {
            try {
//            Right align balance column
                DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
                rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
                accListTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
            } catch (Exception e) {}
        } else {
            fetchBalanceButton.setVisible(false);
        }

        fetchBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchBalance(isRemote);
            }
        });

        attachTableListener();
    }

    public void fetchBalance(boolean isRemote) {
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
                            IdeaUtil.showNotification(project, "Algorand Configuration",
                                    "Algorand deployment node is not configured.", NotificationType.ERROR, ConfigurationAction.ACTION_ID);
                        }
                        if(accountService == null)
                            return;

                        for (AlgoMultisigAccount account : tableModel.getAccounts()) {
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
                                console.showErrorMessage("Error getting balance for account : " + account.getAddress());
                                console.showErrorMessage(e.getMessage());
                            }
                        }
                        progressIndicator.setFraction(1.0);
                        tableModel.fireTableDataChanged();
                    }
                }, "Fetching balance from remote kernel ...", true, project);

            } finally {

            }

    }

    public AlgoMultisigAccount getSelectAccount() {

        int selectedRow = accListTable.getSelectedRow();
        if (selectedRow == -1)
            return null;
        else if (selectedRow <= tableModel.getAccounts().size() - 1) {
            return tableModel.getAccounts().get(selectedRow);
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

        populateMultisigAccounts();

        ListSelectionModel listSelectionModel = accListTable.getSelectionModel();
        listSelectionModel.addListSelectionListener(e -> {
            int index = accListTable.getSelectedRow();
            if(index != -1 && index <= tableModel.getAccounts().size() - 1) {
                AlgoMultisigAccount multisigAccount = tableModel.getAccounts().get(index);
                multisigAccTf.setText(multisigAccount.getAddress());
                thresholdTf.setText(multisigAccount.getThreshold() + "");
                defaultAccountsListModel.clear();
                multisigAccount.getAccounts().stream()
                        .forEach(acc -> defaultAccountsListModel.addElement(acc));
            } else {
                defaultAccountsListModel.clear();
            }
        });

        newBtn.addActionListener(e -> {
            try {
                AlgoMultisigAccount algoMultisigAccount = CreateMultisigAccountAction.createAccount(project);
                if(algoMultisigAccount != null) {
                    populateMultisigAccounts();
                    Messages.showInfoMessage("New multisig account has been added successfully.", "Multi-sig account create");
                }
            } catch (Exception ex) {
                Messages.showErrorDialog(mainPanel, "Error adding new account");
                return;
            }
        });
    }

    private void populateMultisigAccounts() {
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                List<AlgoMultisigAccount> accounts = accountService.getMultisigAccounts();

                tableModel.setElements(accounts);
            } catch(Exception e) {
                messageLabel.setText("Account loading failed !!!");
            }

            messageLabel.setText("");
        }, ModalityState.stateForComponent(accListTable));
    }

    //Create popup
    private void attachTableListener() {
        accListTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getClickCount() == 1)
                    tableRowPopupMenuHandler(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getClickCount() == 1)
                    tableRowPopupMenuHandler(e);
            }

            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2 && me.getButton() == BUTTON1) {     // to detect doble click events
                    handleDoubleClickEvents();
                }
            }
        });

    }

    private void handleDoubleClickEvents() {
        int rowindex = accListTable.getSelectedRow();
        if (rowindex < 0)
            return;

        int column = accListTable.getSelectedColumn(); // select a column
        AlgoMultisigAccount account = tableModel.getAccounts().get(rowindex);
        if(account == null)
            return;

        if(column == 0) {
            copyAddress(account);
        }
    }

    private void tableRowPopupMenuHandler(MouseEvent e) {
        int r = accListTable.rowAtPoint(e.getPoint());
        if (r >= 0 && r < accListTable.getRowCount()) {
            accListTable.setRowSelectionInterval(r, r);
        } else {
            accListTable.clearSelection();
        }

        int rowindex = accListTable.getSelectedRow();
        if (rowindex < 0)
            return;
        if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
            AlgoMultisigAccount account = tableModel.getAccounts().get(rowindex);
            ListPopup popup = createPopup(account);
            RelativePoint relativePoint = new RelativePoint(e.getComponent(), new Point(e.getX(), e.getY()));
            popup.show(relativePoint);
        }
    }

    private ListPopup createPopup(AlgoMultisigAccount account) {
        final DefaultActionGroup group = new DefaultActionGroup();

        group.add(createCopyAction(account));
//        if(!StringUtil.isEmpty(account.getMnemonic())) {
//            group.add(createCopyMnemonicAction(account));
//        }
//        group.add(createShowAccountDetailsAction(account));
        group.add(createRemoveAction(account));

        DataContext dataContext = DataManager.getInstance().getDataContext(accListTable);
        return JBPopupFactory.getInstance().createActionGroupPopup("",
                group, dataContext, JBPopupFactory.ActionSelectionAid.MNEMONICS, true);
    }

    @NotNull
    private AnAction createRemoveAction(AlgoMultisigAccount account) {
        return new AnAction("Remove", "Remove", AllIcons.General.Remove) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                AlgoMultisigAccount algoAccount = getSelectAccount();
                if (algoAccount == null)
                    return;
                int response = Messages.showYesNoDialog("Do you really want to delete this account ? \n " + algoAccount.getAddress(),
                        "Delete Account", AllIcons.General.Warning);
                if (response == Messages.YES) {
                    if (accountService.removeMultisigAccount(algoAccount)) {
                        int index = tableModel.getAccounts().indexOf(account);
                        if (index != -1 && index < tableModel.getAccounts().size()) {
                            tableModel.getAccounts().remove(index);
                            tableModel.fireTableDataChanged();
                        }
                    }
                }

            }

            @Override
            public boolean isDumbAware() {
                return true;
            }
        };
    }

    private AnAction createCopyAction(AlgoMultisigAccount account) {
        return new AnAction("Copy Address", "Copy Address", AllIcons.General.CopyHovered) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                AlgoMultisigAccount algoAccount = getSelectAccount();
                copyAddress(algoAccount);
            }

            @Override
            public boolean isDumbAware() {
                return true;
            }
        };
    }

    private void copyAddress(AlgoMultisigAccount algoAccount) {
        StringSelection stringSelection = new StringSelection(algoAccount.getAddress());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        Messages.showInfoMessage("Address copied to the clipboard", "Copy Address");
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
