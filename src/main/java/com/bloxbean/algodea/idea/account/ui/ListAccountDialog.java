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
import com.intellij.codeInsight.hints.presentation.MouseButton;
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
import com.twelvemonkeys.lang.StringUtil;
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

public class ListAccountDialog extends DialogWrapper {
    private JPanel mainPanel;
    private JTable accListTable;
    private JButton fetchBalanceButton;
    private JLabel messageLabel;
    private JButton importAccBtn;
    private JButton newAcctBtn;
    private Project project;
    private AccountListTableModel tableModel;
    private boolean isRemote;
    private boolean showBalance;
    private AccountService accountService;
    private AlgoConsole console;

    public ListAccountDialog(Project project, boolean isRemote) {
        this(project, isRemote, true);
        this.console = AlgoConsole.getConsole(project);
    }

    public ListAccountDialog(Project project, boolean isRemote, boolean showBalance) {
        super(project, true);
        init();
        setTitle("Accounts");

        this.accountService = AccountService.getAccountService();
        this.project = project;
        this.isRemote = isRemote;
        this.showBalance = showBalance;

        initialize();

        if(showBalance) {
            try {
                //Right align balance column
                DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
                rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
                accListTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
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

        attachImportAccountListener();
        attachTableListener();
        attachActionButtonHandlers();

        accListTable.getColumnModel().getColumn(0).setMaxWidth(150);
        accListTable.getColumnModel().getColumn(0).setMinWidth(50);
        accListTable.getColumnModel().getColumn(0).setPreferredWidth(150);

        if(showBalance) {
            accListTable.getColumnModel().getColumn(2).setMaxWidth(250);
            accListTable.getColumnModel().getColumn(2).setMinWidth(50);
            accListTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        }
//        accListTable.getColumnModel().getColumn(2).setPreferredWidth(30/);
//        setJTableColumnsWidth(accListTable, accListTable.getWidth(), 5,80,15);
    }

    private void attachActionButtonHandlers() {
        newAcctBtn.setIcon(AllIcons.General.Add);
        importAccBtn.setIcon(AllIcons.General.Add);
        fetchBalanceButton.setIcon(AllIcons.General.Information);
        newAcctBtn.addActionListener(e -> {
            String accountName = Messages.showInputDialog(mainPanel, "Enter a name for the new account", "New Account", AllIcons.General.Information);
            if(!StringUtil.isEmpty(accountName))
                accountName = accountName.trim();
            else
                return; //cancel

            try {
                accountService.createNewAccount(accountName);
                poulateAccounts();
                Messages.showInfoMessage(project, String.format("New account '%s' has been added successfully. " +
                        "\nIf you don't see the account, please close and re-open the account list dialog", accountName), "Account  Create");
            } catch (Exception exception) {
                Messages.showErrorDialog(mainPanel, "Error adding new account");
                return;
            }
        });
    }

    private void attachImportAccountListener() {
        importAccBtn.addActionListener(e -> {
            ImportAccountDialog dialog = new ImportAccountDialog();
            boolean ok = dialog.showAndGet();
            if(!ok)
                return;

            AlgoAccount account = dialog.getAccount();

            if(account == null) {
                Messages.showErrorDialog("Invalid account. Account could not be imported", "Import Account");
                return;
            }

            ApplicationManager.getApplication().runWriteAction(() -> {
                AccountService accountService = AccountService.getAccountService();
                boolean result = accountService.importAccount(account);

                ApplicationManager.getApplication().invokeLater(() -> {
                    if(result) {
                        tableModel.addElement(account);
                        tableModel.fireTableRowsInserted(tableModel.getRowCount() - 1, tableModel.getRowCount() - 1);
                        Messages.showInfoMessage("Account imported successfully", "Import Account");
                    } else {
                        Messages.showWarningDialog("Account could not be imported.\nPlease check if account already exists.",
                                "Import Account");
                    }
                });

            });
        });
    }

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
        AlgoAccount account = tableModel.getAccounts().get(rowindex);
        if(account == null)
            return;
        if(column == 0) {
            showAccountDetails(account);
        } else if(column == 1) {
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
            AlgoAccount account = tableModel.getAccounts().get(rowindex);
            ListPopup popup = createPopup(account);
            RelativePoint relativePoint = new RelativePoint(e.getComponent(), new Point(e.getX(), e.getY()));
            popup.show(relativePoint);
        }
    }

    private ListPopup createPopup(AlgoAccount account) {
        final DefaultActionGroup group = new DefaultActionGroup();

        group.add(createCopyAction(account));
        if(!StringUtil.isEmpty(account.getMnemonic())) {
            group.add(createCopyMnemonicAction(account));
        }
        group.add(createShowAccountDetailsAction(account));
        group.add(createRemoveAction(account));

        DataContext dataContext = DataManager.getInstance().getDataContext(accListTable);
        return JBPopupFactory.getInstance().createActionGroupPopup("",
                group, dataContext, JBPopupFactory.ActionSelectionAid.MNEMONICS, true);
    }

    private AnAction createShowAccountDetailsAction(AlgoAccount account) {
        return new AnAction("Show Details", "Show Details", AllIcons.General.InspectionsEye) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                showAccountDetails(account);
            }
        };
    }

    private void showAccountDetails(AlgoAccount account) {
        AccountDetailsDialog dialog = new AccountDetailsDialog(project, account);
        boolean ok = dialog.showAndGet();

        if(dialog.getAccountInfoUpdated()) {
            //Update table.
            poulateAccounts();
        }
    }

    @NotNull
    private AnAction createRemoveAction(AlgoAccount account) {
        return new AnAction("Remove", "Remove", AllIcons.General.Remove) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                AlgoAccount algoAccount = getSelectAccount();
                if (algoAccount == null)
                    return;
                int response = Messages.showYesNoDialog("Do you really want to delete this account ? \n " + algoAccount.getAddress(),
                        "Delete Account", AllIcons.General.Warning);
                if (response == Messages.YES) {
                    if (accountService.removeAccount(algoAccount)) {
                        int index = tableModel.getAccounts().indexOf(account);
                        if (index != -1 && index < tableModel.getAccounts().size()) {
                            tableModel.getAccounts().remove(index);
                            tableModel.fireTableDataChanged();
                        }
                    }
                }

            }
        };
    }

    private AnAction createCopyAction(AlgoAccount account) {
        return new AnAction("Copy Address", "Copy Address", AllIcons.General.CopyHovered) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                AlgoAccount algoAccount = getSelectAccount();
                copyAddress(algoAccount);
            }
        };
    }

    private void copyAddress(AlgoAccount algoAccount) {
        StringSelection stringSelection = new StringSelection(algoAccount.getAddress().toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        Messages.showInfoMessage("Address copied to the clipboard", "Copy Address");
    }

    private AnAction createCopyMnemonicAction(AlgoAccount account) {
        return new AnAction("Copy Mnemonic", "Copy Mnemonic", AllIcons.General.CopyHovered) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                AlgoAccount algoAccount = getSelectAccount();
                StringSelection stringSelection = new StringSelection(algoAccount.getMnemonic().toString());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                Messages.showInfoMessage("Mnemonic phase copied to the clipboard", "Copy Mnemonic");
            }
        };
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
                        ProgressIndicator progressIndicator = null;
                        try {
                            progressIndicator = ProgressManager.getInstance().getProgressIndicator();
                            progressIndicator.setIndeterminate(false);

                            float counter = 0;
                            AlgoAccountService accountService = null;
                            try {
                                accountService = new AlgoAccountService(project, new LogListenerAdapter(console));
                            } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
                                IdeaUtil.showNotification(project, "Algorand Configuration",
                                        "Algorand deployment node is not configured.", NotificationType.ERROR, ConfigurationAction.ACTION_ID);
                            }
                            if (accountService == null)
                                return;

                            int totalCount = tableModel.getAccounts().size();
                            for (AlgoAccount account : tableModel.getAccounts()) {
                                //TODO fetch balance
                                try {
                                    Long balance = accountService.getBalance(account.getAddress());

                                    progressIndicator.setFraction(counter++ / totalCount);
                                    if (progressIndicator.isCanceled()) {
                                        break;
                                    }

                                    if (balance != null) {
                                        account.setBalance(balance);
                                    }
                                    tableModel.fireTableRowsUpdated((int) counter - 1, (int) counter - 1);
                                } catch (Exception e) {
                                    console.showErrorMessage("Error getting balance for account : " + account.getAddress());
                                    console.showErrorMessage(e.getMessage());
                                }
//                           //TO BigInteger balance = accountListFetcher.getBalance(account, isRemote);
                            }
                            progressIndicator.setFraction(1.0);
                            tableModel.fireTableDataChanged();
                        } catch (Exception e) {
                            console.showErrorMessage("Error fetching balance", e);
                        } finally {
                            if(progressIndicator != null) {
                                try {
                                    progressIndicator.setFraction(1.0);
                                } catch (Exception e) {

                                }
                            }
                        }
                    }
                }, "Fetching balance from remote kernel ...", true, project);

            } finally {

            }
        } else {

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
        poulateAccounts();
    }

    private void poulateAccounts() {
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
