package com.bloxbean.algodea.idea.account.ui;

import com.algorand.algosdk.v2.client.model.Account;
import com.algorand.algosdk.v2.client.model.Application;
import com.algorand.algosdk.v2.client.model.Asset;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountService;
import com.bloxbean.algodea.idea.configuration.action.ConfigurationAction;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.AccountAsset;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.AlgoAccountService;
import com.bloxbean.algodea.idea.nodeint.service.AssetTransactionService;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.service.StatefulContractService;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.util.AlgoConversionUtil;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.math.BigInteger;
import java.util.List;

public class AccountDetailsDialog extends DialogWrapper {
    private JTextField accountNameTf;
    private JTextField accountTf;
    private JButton accountNameUpdateBtn;
    private JTextField balanceTf;
    private JComboBox createdAppsCB;
    private JButton deleteAppBtn;
    private JComboBox assetsCB;
    private JPanel mainPanel;
    private JComboBox createdAssetsCB;
    private JTextField authAddrTf;
    private AlgoAccount algoAccount;

    private AccountService accountService;
    private AlgoAccountService algoAccountService;

    private DefaultComboBoxModel<AccountAsset> accountAssetsModel;
    private DefaultComboBoxModel<CreatedAssetsMeta> createdAssetModel;
    private DefaultComboBoxModel<Long> applicationsModel;

    private boolean infoUpdate = false;
    private AlgoConsole console;
    private AssetTransactionService assetTransactionService;
    private StatefulContractService statefulContractService;

    protected AccountDetailsDialog(Project project, AlgoAccount account) {
        super(true);
        this.algoAccount = account;
        init();
        setTitle("Account Details");

        console = AlgoConsole.getConsole(project);
        accountNameUpdateBtn.setEnabled(false);
        initializeData(project);
        attachUpdateAccountNameListener();
        attachDeleteApplicationListener(project);
    }

    private void initializeData(Project project) {
        if (algoAccount == null || StringUtil.isEmpty(algoAccount.getAddress())) {
            Messages.showErrorDialog("Invalid account", "Account Details Error");
            return;
        }

        this.accountService = AccountService.getAccountService();

        accountNameTf.setText(algoAccount.getName());
        accountTf.setText(algoAccount.getAddress());

        fetchAccountDetails(project);
    }

    private void fetchAccountDetails(Project project) {
//        AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();

        try {
            ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
                @Override
                public void run() {

                    try {
                        ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
                        progressIndicator.setIndeterminate(false);

                        AlgoAccountService accountService = null;

                        progressIndicator.setText("Fetching account details ....");

                        try {
                            accountService = new AlgoAccountService(project, new LogListenerAdapter(console));
                        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
                            //deploymentTargetNotConfigured.printStackTrace();
                            IdeaUtil.showNotification(project, "Algorand Configuration",
                                    "Algorand deployment node is not configured.", NotificationType.ERROR, ConfigurationAction.ACTION_ID);
                        }
                        if (accountService == null)
                            return;

                        Account account = accountService.getAccount(algoAccount.getAddress());

                        if (account == null)
                            return;

                        if(account.authAddr != null) {
                            authAddrTf.setText(account.authAddr());
                        }

                        Long balance = account.amount;
                        if (balance != null) {
                            balanceTf.setText(AlgoConversionUtil.mAlgoToAlgoFormatted(BigInteger.valueOf(balance)));
                        }

                        List<Application> applications = account.createdApps;
                        if (applications != null) {
                            for (Application app : applications) {
                                if (app.id != null) {
                                    applicationsModel.addElement(app.id);
                                }
                            }
                        }
                        progressIndicator.setFraction(0.5);

                        //Check if progress indicator is cancelled
                        if (progressIndicator.isCanceled())
                            return;

                        progressIndicator.setText("Get assets details");

                        List<AccountAsset> accountAssetList = accountService.getAccountAssets(algoAccount.getAddress());

                        progressIndicator.setFraction(0.7);
                        accountAssetsModel.removeAllElements();
                        if (accountAssetList != null) {
                            for (AccountAsset accountAsset : accountAssetList) {
                                accountAssetsModel.addElement(accountAsset);
                            }
                        }

                        progressIndicator.setFraction(0.9);
                        List<Asset> createdAssets = account.createdAssets;
                        if (createdAssets != null) {
                            for (Asset asset : createdAssets) {
                                CreatedAssetsMeta createdAssetsMeta = new CreatedAssetsMeta(asset);
                                createdAssetModel.addElement(createdAssetsMeta);
                            }
                        }

                        progressIndicator.setFraction(1.0);
                    } catch (Exception e) {
                        console.showErrorMessage("Error getting account details", e);
                    }
                }
            }, "Fetching account details from Algorand Node ...", true, project);

        } finally {

        }
    }

    private void attachUpdateAccountNameListener() {
        accountNameTf.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                accountNameUpdateBtn.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                accountNameUpdateBtn.setEnabled(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                accountNameUpdateBtn.setEnabled(true);
            }
        });

        accountNameUpdateBtn.addActionListener(e -> {

            ApplicationManager.getApplication().runWriteAction(() -> {
                if (this.accountService == null)
                    return;

                String accountName = accountNameTf.getText();
                if (!StringUtil.isEmpty(accountName)) {
                    boolean status = accountService.updateAccountName(algoAccount.getAddress().toString(), accountName);
                    accountNameUpdateBtn.setEnabled(false);
                    infoUpdate = true;
                    if (status) {
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                Messages.showInfoMessage(mainPanel, "Account name updated successfully", "Account Name Update");
                            }
                        }, ModalityState.any());
                    }
                }
            });
        });
    }

    private void attachDeleteApplicationListener(Project project) {
        deleteAppBtn.addActionListener(e -> {
            Long appId = (Long) createdAppsCB.getSelectedItem();
            if (appId == null) return;

            if (statefulContractService == null) {
                try {
                    statefulContractService = new StatefulContractService(project, new LogListenerAdapter(console));
                } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
                    Messages.showErrorDialog(mainPanel, "Algorand node is not configured for this project.");
                    return;
                }
            }

            ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
                @Override
                public void run() {
                    try {
                        com.algorand.algosdk.account.Account account = new com.algorand.algosdk.account.Account(algoAccount.getMnemonic());

                        Result result = statefulContractService.delete(appId, account, account.getAddress(), new TxnDetailsParameters(), RequestMode.TRANSACTION);
                        if (result != null && result.isSuccessful()) {
                            showMessage("Application deleted successfully, App Id: " + appId, "Application Delete", false);
                            createdAppsCB.removeItem(appId);
                            return;
                        } else {
                            showMessage("Application deletion failed, App Id : " + appId, "Application Delete", true);
                            return;
                        }
                    } catch (Exception ex) {
                        showMessage("Application deletion failed: \n Reason: " + ex.getMessage(), "Application Delete", true);
                        return;
                    }
                }
            }, "Deleting Application - " + appId, true, project);


        });
    }

    public boolean getAccountInfoUpdated() {
        return infoUpdate;
    }

    private void showMessage(String message, String title, boolean isError) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                if (isError) {
                    Messages.showErrorDialog(mainPanel, message, title);
                } else {
                    Messages.showInfoMessage(mainPanel, message, title);
                }
            }
        }, ModalityState.any());
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        applicationsModel = new DefaultComboBoxModel<>();
        createdAppsCB = new ComboBox(applicationsModel);

        accountAssetsModel = new DefaultComboBoxModel<>();
        assetsCB = new ComboBox(accountAssetsModel);

        createdAssetModel = new DefaultComboBoxModel<>();
        createdAssetsCB = new ComboBox(createdAssetModel);
    }

    class CreatedAssetsMeta {
        private Long assetId;
        private String assetName;
        private String assetUnit;

        public CreatedAssetsMeta(Asset asset) {
            this.assetId = asset.index;
            this.assetName = asset.params.name;
            this.assetUnit = asset.params.unitName;
        }

        @Override
        public String toString() {
            if (assetId == null)
                assetId = 0L;

            String result = String.valueOf(assetId);
            result += "(" + assetName + ")";

            return result;
        }
    }
}
