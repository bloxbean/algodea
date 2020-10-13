package com.bloxbean.algodea.idea.contracts.ui;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algodea.idea.core.service.AlgoCacheService;
import com.bloxbean.algodea.idea.nodeint.exception.InvalidContractInputParamException;
import com.bloxbean.algodea.idea.nodeint.model.ApplArg;
import com.bloxbean.algodea.idea.nodeint.model.ArgType;
import com.bloxbean.algodea.idea.nodeint.model.Lease;
import com.bloxbean.algodea.idea.nodeint.model.Note;
import com.bloxbean.algodea.idea.nodeint.util.ArgTypeToByteConverter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

public class AppTxnBaseParamEntryForm {
    private JTextField fromAccountTf;
    private JButton fromAccChooserBtn;
    private JPanel mainPanel;
    private JComboBox appIdCB;
    private JTextField fromAccMnemonicTf;
    private JCheckBox useLastDeployedAppCB;

    DefaultComboBoxModel<String> appIdComboBoxModel;

    Project project;

    public AppTxnBaseParamEntryForm() {
//        this.project = project;
//        initializeData();
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
            if(!StringUtil.isEmpty(deploymentServerId))
                cachedAppIds = cacheService.getAppIds(deploymentServerId);

            if(cachedAppIds != null) {
                appIdCB.addItem("");
                for(String appId: cachedAppIds) {
                    appIdCB.addItem(appId);
                }
            }
        }

        fromAccChooserBtn.addActionListener(e -> {
                AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
                if(algoAccount != null) {
                    fromAccountTf.setText(algoAccount.getAddress());
                    fromAccMnemonicTf.setText(algoAccount.getMnemonic());
                }
        });

        fromAccMnemonicTf.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                String mnemonic = fromAccMnemonicTf.getText();
                try {
                    Account account = new Account(mnemonic);
                    fromAccountTf.setText(account.getAddress().toString());
                } catch (Exception ex) {
                    fromAccountTf.setText("");
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
    }

    public @Nullable ValidationInfo doValidate() {

        if(getAppId() == null) {
            return new ValidationInfo("Please select or provide a valid App Id", appIdCB);
        }

        if(StringUtil.isEmpty(fromAccountTf.getText())) {
            return new ValidationInfo("Please select a valid from account or enter valid mnemonic", fromAccountTf);
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

    public Account getFromAccount() {
        String mnemonic = fromAccMnemonicTf.getText().trim();
        try {
            Account account = new Account(mnemonic);
            return account;
        } catch (Exception e) {
            return null;
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        appIdComboBoxModel = new DefaultComboBoxModel<>();
        appIdCB = new ComboBox(appIdComboBoxModel);
    }
}
