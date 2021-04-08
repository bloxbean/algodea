package com.bloxbean.algodea.idea.contracts.ui;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.account.service.AccountService;
import com.bloxbean.algodea.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algodea.idea.configuration.service.NodeConfigState;
import com.bloxbean.algodea.idea.core.service.AlgoCacheService;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.service.AlgoAccountService;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import java.util.function.Consumer;

public class AppTxnBaseParamEntryForm {
    private final static Logger LOG = Logger.getInstance(CreateAppEntryForm.class);

    private JTextField authAccountTf;
    private JButton authAddressChooserBtn;
    private JPanel mainPanel;
    private JComboBox appIdCB;
    private JTextField authMnemonicTf;
    private JCheckBox useLastDeployedAppCB;
    private JLabel contractNameLabel;
    private JTextField senderAddressTf;
    private JButton senderChooser;
    private JButton senderMultiSigChooser;

    DefaultComboBoxModel<String> appIdComboBoxModel;

    Project project;
    private boolean mandatoryAccount = true;
    private boolean mandatoryAppId = true;
    private boolean disableSignerFields = false;

    private AlgoConsole console;
    private AlgoAccountService algoAccountService;

    public AppTxnBaseParamEntryForm() {

    }

    public void initializeData(Project project) throws DeploymentTargetNotConfigured {
        this.project = project;

        console = AlgoConsole.getConsole(project);
        try {
            algoAccountService = new AlgoAccountService(project, new LogListenerAdapter(console));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            throw deploymentTargetNotConfigured;
        }

        AlgoProjectState projectState = AlgoProjectState.getInstance(project);
        String deploymentServerId = null;
        if(projectState != null) {
            deploymentServerId = projectState.getState().getDeploymentServerId();
        }

        AlgoCacheService cacheService = AlgoCacheService.getInstance(project);
        List<String> cachedAppIds = null;
        if(cacheService != null) {
            if(!StringUtil.isEmpty(deploymentServerId)) {
                //Get genesisHash of this deploymentServer
                String genesisHash = NodeConfigState.getGenesisHash(deploymentServerId);
                if(StringUtil.isEmpty(genesisHash))
                    genesisHash = deploymentServerId;

                cachedAppIds = cacheService.getAppIds(genesisHash);
            }

            if(cachedAppIds != null) {
                appIdCB.addItem("");
                for(String appId: cachedAppIds) {
                    appIdCB.addItem(appId);
                }
            }
        }

        authAddressChooserBtn.addActionListener(e -> {
                AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
                if(algoAccount != null) {
                    authAccountTf.setText(algoAccount.getAddress());
                    authMnemonicTf.setText(algoAccount.getMnemonic());
                }
        });

        authMnemonicTf.addFocusListener(new FocusListener() {
            String oldMnemonic;
            @Override
            public void focusGained(FocusEvent e) {
                oldMnemonic = authMnemonicTf.getText();
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(oldMnemonic != null && oldMnemonic.equals(authMnemonicTf.getText())) {
                    oldMnemonic = null;
                    return;
                }
                oldMnemonic = null; //reset old mnemonic

                String mnemonic = authMnemonicTf.getText();
                try {
                    Account account = new Account(mnemonic);
                    authAccountTf.setText(account.getAddress().toString());
                } catch (Exception ex) {
                    authAccountTf.setText("");
                }
            }
        });

        senderChooser.addActionListener(e -> {
            AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
            if(algoAccount != null) {
                senderAddressTf.setText(algoAccount.getAddress());
                authAccountTf.setText("");
                authMnemonicTf.setText("");

                setAuthAddress(project, algoAccount);
            }
        });

        senderMultiSigChooser.addActionListener(e -> {
            AlgoMultisigAccount multisigAccount = AccountChooser.getSelectedMultisigAccount(project, true);
            if(multisigAccount != null) {
                senderAddressTf.setText(multisigAccount.getAddress());
                authAccountTf.setText("");
                authMnemonicTf.setText("");

                setAuthAddressForMultiSigSender(project, multisigAccount);
            }
        });

        //TODO
        senderAddressTf.addFocusListener(new FocusAdapter() {
            String oldSender;
            @Override
            public void focusGained(FocusEvent e) {
                oldSender = senderAddressTf.getText();
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(oldSender != null && oldSender.equals(senderAddressTf.getText())) {
                    oldSender = null;
                    return;
                }
                oldSender = null;
                authAccountTf.setText("");
                authMnemonicTf.setText("");

                try {
                    Address address = new Address(senderAddressTf.getText());
                    AlgoAccount algoAccount = AccountService.getAccountService().getAccountByAddress(address.toString());
                    if(algoAccount == null) {
                        algoAccount = new AlgoAccount(address.toString());
                    }

                    setAuthAddress(project, algoAccount);
                } catch (Exception ex) {

                }
            }
        });

        useLastDeployedAppCB.addActionListener(e -> {
                if(useLastDeployedAppCB.isSelected()) {
                    if(appIdCB.getModel().getSize() >= 2) {
                        appIdCB.setSelectedIndex(1);
                    }
                    appIdCB.setEnabled(false);
                } else {
                    appIdCB.setEnabled(true);
                }
        });

        appIdCB.addActionListener(e -> {
            String appId = (String) appIdCB.getSelectedItem();
            if(StringUtil.isEmpty(appId)) {
                contractNameLabel.setText(" ");
                return;
            }

            String contractName = cacheService.getContractNameForAppId(appId);
            if(!StringUtil.isEmpty(contractName)) {
                contractNameLabel.setText(String.format("(%s)",contractName));
            } else {
                contractNameLabel.setText(" ");
            }
        });
    }

    /***** Get Auth Address code start ****/

    private void setAuthAddress(Project project, AlgoAccount sender) {
        if(disableSignerFields) //If signer field is disabled, no need to set auth fields (used from ReadState action)
            return;

        getAuthAddress(project, sender.getAddress(), (accountAuthAddr) -> {
            if(!StringUtil.isEmpty(accountAuthAddr)) { //auth-addr found
                AccountService accountService = AccountService.getAccountService();
                AlgoAccount algoAccount = accountService.getAccountByAddress(accountAuthAddr);
                if (algoAccount != null && !StringUtil.isEmpty(algoAccount.getMnemonic())) {
                    authAccountTf.setText(algoAccount.getAddress());
                    authMnemonicTf.setText(algoAccount.getMnemonic());
                } else {
                    //TODO alert
                    authAccountTf.setText(accountAuthAddr);
                    IdeaUtil.authorizedAddressNotFoundWarning();
                }
            } else { //No auth-addr
                if(!StringUtil.isEmpty(sender.getMnemonic())) {
                    authAccountTf.setText(sender.getAddress());
                    authMnemonicTf.setText(sender.getMnemonic());
                }
            }
        });
    }

    private void setAuthAddressForMultiSigSender(Project project, AlgoMultisigAccount sender) {
        getAuthAddress(project, sender.getAddress(), (accountAuthAddr) -> {
            if(!StringUtil.isEmpty(accountAuthAddr)) {
                AccountService accountService = AccountService.getAccountService();
                AlgoAccount algoAccount = accountService.getAccountByAddress(accountAuthAddr);
                if (algoAccount != null) {
                    authAccountTf.setText(algoAccount.getAddress());
                    authMnemonicTf.setText(algoAccount.getMnemonic());
                } else {
                    //TODO alert
                }
            }

        });
    }

    private void getAuthAddress(Project project, String address, Consumer<String> authAddressCheck) {
        if(algoAccountService == null || StringUtil.isEmpty(address))
            return;

        ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {

            @Override
            public void run() {
                try {
                    com.algorand.algosdk.v2.client.model.Account account = algoAccountService.getAccount(address);
                    authAddressCheck.accept(account.authAddr());
                } catch (Exception e) {
                    if(LOG.isDebugEnabled())
                        LOG.warn(e);
                } finally {

                }
            }
        }, "Fetching Authorized Address ...", true, project);
    }

    /**** Get Auth Address ends here ****/

    public @Nullable ValidationInfo doValidate() {

        if(getAppId() == null && mandatoryAppId) {
            return new ValidationInfo("Please select or provide a valid App Id", appIdCB);
        }

        if(StringUtil.isEmpty(senderAddressTf.getText()) && mandatoryAccount) {
            return new ValidationInfo("Please select a valid sender address", senderAddressTf);
        }

        if(mandatoryAccount && !disableSignerFields) {
            if (StringUtil.isEmpty(authAccountTf.getText())) {
                return new ValidationInfo("Please select a valid Authorized Address or enter valid Authorized Mnemonic", authAccountTf);
            }

            //If auth account is set , auth mnemonic should be set
            if (!StringUtil.isEmpty(authAccountTf.getText())
                    && StringUtil.isEmpty(authMnemonicTf.getText())) {
                return new ValidationInfo("Please provide a valid mnemonic for the Authorized Address.",
                        authMnemonicTf);
            }
        }

        return null;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public Long getAppId() {
        try {
            String appId = appIdCB.getSelectedItem() != null ? appIdCB.getSelectedItem().toString().trim() : null;
            return Long.parseLong(appId);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public Account getAuthorizedAccount() {
        String mnemonic = authMnemonicTf.getText().trim();
        try {
            Account account = new Account(mnemonic);
            return account;
        } catch (Exception e) {
            return null;
        }
    }

    public Address getSenderAddress() {
        String address = senderAddressTf.getText().trim();
        try {
            return new Address(address);
        } catch (Exception e) {
            return null;
        }
    }

    public void setMandatoryAccountCheck(boolean flag) {
        this.mandatoryAccount = flag;
    }

    public void setMandatoryAppIdCheck(boolean flag) {
        this.mandatoryAppId = flag;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        appIdComboBoxModel = new DefaultComboBoxModel<>();
        appIdCB = new ComboBox(appIdComboBoxModel);
    }

    public void disbleSignerFields() {
        disableSignerFields = true;
        authAccountTf.setEnabled(false);
        authAddressChooserBtn.setEnabled(false);
        authMnemonicTf.setEnabled(false);
    }
}
