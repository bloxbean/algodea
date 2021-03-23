package com.bloxbean.algodea.idea.contracts.ui;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algodea.idea.configuration.service.NodeConfigState;
import com.bloxbean.algodea.idea.core.service.AlgoCacheService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

public class AppTxnBaseParamEntryForm {
    private JTextField signerAccountTf;
    private JButton signerAccChooserBtn;
    private JPanel mainPanel;
    private JComboBox appIdCB;
    private JTextField signerAccMnemonicTf;
    private JCheckBox useLastDeployedAppCB;
    private JLabel contractNameLabel;
    private JTextField senderAddressTf;
    private JButton senderAccountChooser;
    private JButton senderMultiSigChooser;

    DefaultComboBoxModel<String> appIdComboBoxModel;

    Project project;
    private boolean mandatoryAccount = true;
    private boolean mandatoryAppId = true;
    private boolean disableSignerFields = false;

    public AppTxnBaseParamEntryForm() {

    }

    public void initializeData(Project project) {
        this.project = project;

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

        signerAccChooserBtn.addActionListener(e -> {
                AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
                if(algoAccount != null) {
                    signerAccountTf.setText(algoAccount.getAddress());
                    signerAccMnemonicTf.setText(algoAccount.getMnemonic());
                    senderAddressTf.setText(algoAccount.getAddress());
                }
        });

        signerAccMnemonicTf.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                String mnemonic = signerAccMnemonicTf.getText();
                try {
                    Account account = new Account(mnemonic);
                    signerAccountTf.setText(account.getAddress().toString());
                    senderAddressTf.setText(account.getAddress().toString());
                } catch (Exception ex) {
                    signerAccountTf.setText("");
                    senderAddressTf.setText("");
                }
            }
        });

        senderAccountChooser.addActionListener(e -> {
            AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
            if(algoAccount != null) {
                senderAddressTf.setText(algoAccount.getAddress());
            }
        });

        senderMultiSigChooser.addActionListener(e -> {
            AlgoMultisigAccount multisigAccount = AccountChooser.getSelectedMultisigAccount(project, true);
            if(multisigAccount != null) {
                senderAddressTf.setText(multisigAccount.getAddress());
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

    public @Nullable ValidationInfo doValidate() {

        if(getAppId() == null && mandatoryAppId) {
            return new ValidationInfo("Please select or provide a valid App Id", appIdCB);
        }

        if(StringUtil.isEmpty(signerAccountTf.getText()) && mandatoryAccount && !disableSignerFields) {
            return new ValidationInfo("Please select a valid from account or enter valid mnemonic", signerAccountTf);
        }

        if(StringUtil.isEmpty(senderAddressTf.getText()) && mandatoryAccount) {
            return new ValidationInfo("Please select a valid sender address", senderAddressTf);
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

    public Account getSignerAccount() {
        String mnemonic = signerAccMnemonicTf.getText().trim();
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
        signerAccountTf.setEnabled(false);
        signerAccChooserBtn.setEnabled(false);
        signerAccMnemonicTf.setEnabled(false);
    }
}
