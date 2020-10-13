package com.bloxbean.algodea.idea.contracts.ui;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algodea.idea.nodeint.exception.InvalidContractInputParamException;
import com.bloxbean.algodea.idea.nodeint.model.*;
import com.bloxbean.algodea.idea.core.service.AlgoCacheService;
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
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

public class AppTxnParamEntryForm  {
    private JTextField fromAccountTf;
    private JTextField foreignAppsTf;
    private JTextField foreignAssetsTf;
    private JTextField accountsTf;
    private JList accountsList;
    private JList argList;
    private JButton fromAccChooserBtn;
    private JButton accountAddBtn;
    private JButton argAddBtn;
    private JPanel mainPanel;
    private JComboBox<ArgType> argTypeCB;
    private JTextField argTf;
    private JTextField noteTf;
    private JComboBox noteTypeCB;
    private JButton accountsDelBtn;
    private JComboBox appIdCB;
    private JTextField fromAccMnemonicTf;
    private JButton argDelBtn;
    private JCheckBox useLastDeployedAppCB;
    private JTextField leaseTf;
    private JComboBox leaseCB;
    private JButton accountsChooserBtn;

    DefaultComboBoxModel<String> appIdComboBoxModel;
    DefaultComboBoxModel<ArgType> argTypeComboBoxModel;
    DefaultListModel<ApplArg> argListModel;
    DefaultComboBoxModel<ArgType> noteTypeDefaultComboBoxModel;
    DefaultComboBoxModel<ArgType> leaseTypeDefaultComboBoxModel;

    Project project;

    public AppTxnParamEntryForm(Project project) {
//        super(project, false);
//        init();
//        setTitle(title);

        this.project = project;
        initializeData();
    }

    public void initializeData() {

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

        argAddBtn.addActionListener(e -> {
                ArgType argType = (ArgType)argTypeCB.getSelectedItem();

                if(argType != null) {
                    ApplArg applArg = new ApplArg(argType, argTf.getText());
                    argListModel.addElement(applArg);
                    argTf.setText("");
                }
        });

        argDelBtn.addActionListener(e -> {
                Object selectedValue = argList.getSelectedValue();
                if(selectedValue != null) {
                    argListModel.removeElement(selectedValue);
            }
        });

        accountsChooserBtn.addActionListener(e -> {
            AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
            if(algoAccount != null) {
                accountsTf.setText(algoAccount.getAddress());
            }
        });

        accountAddBtn.addActionListener( e -> {
                if(!StringUtil.isEmpty(accountsTf.getText())) {
                    ((DefaultListModel) accountsList.getModel()).addElement(StringUtil.trim(accountsTf.getText()));
                    accountsTf.setText("");
                }
        });

        accountsDelBtn.addActionListener(e -> {
            Object selectedValue = accountsList.getSelectedValue();
            if(selectedValue != null) {
                ((DefaultListModel) accountsList.getModel()).removeElement(selectedValue);
            }
        });

    }

    protected @Nullable ValidationInfo doValidate() {

        if(getAppId() == null) {
            return new ValidationInfo("Please select or provide a valid App Id", appIdCB);
        }

        if(StringUtil.isEmpty(fromAccountTf.getText())) {
            return new ValidationInfo("Please select a valid from account or enter valid mnemonic", fromAccountTf);
        }

        List<Long> fApps = getForeignApps();
        if(fApps == null) { //For successful case it should be empty or a valid list with elements
            return new ValidationInfo("Invalid Foreign App Index", foreignAppsTf);
        }

        List<Long> fAssets = getForeignAssets();
        if(fAssets == null) { //For successful case it should be empty or a valid list with elements
            return new ValidationInfo("Invalid Foreign Asset Index", foreignAssetsTf);
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

    public List<ApplArg> getArgs() {
        Enumeration<ApplArg> elems = argListModel.elements();
        if(elems == null) return Collections.EMPTY_LIST;

        return Collections.list(elems);
    }

    public List<byte[]> getArgsAsBytes() throws Exception {
        Enumeration<ApplArg> elems = argListModel.elements();
        if(elems == null) return Collections.EMPTY_LIST;

        List<byte[]> argsBytes = new ArrayList<>();
        while(elems.hasMoreElements()) {
            ApplArg applArg = elems.nextElement();
            byte[] bytes = ArgTypeToByteConverter.convert(applArg.getType(), applArg.getValue());
            argsBytes.add(bytes);
        }

        return argsBytes;
    }

    public Note getNote() {
        ArgType noteType = (ArgType) noteTypeCB.getSelectedItem();
        if(noteType == null)
            return null;
        else {
            return new Note(noteType, StringUtil.trim(noteTf.getText()));
        }
    }

    public byte[] getNoteBytes() throws Exception {
        ArgType noteType = (ArgType) noteTypeCB.getSelectedItem();
        if(noteType == null)
            return null;
        else {
            byte[] bytes = ArgTypeToByteConverter.convert(noteType, StringUtil.trim(noteTf.getText()));
            return bytes;
        }
    }

    public Lease getLease() {
        ArgType type = (ArgType) leaseCB.getSelectedItem();
        if(type == null)
            return null;
        else {
            return new Lease(type, StringUtil.trim(leaseTf.getText()));
        }
    }

    public byte[] getLeaseBytes() throws Exception {
        ArgType type = (ArgType) leaseCB.getSelectedItem();
        if(type == null)
            return null;
        else {
            byte[] bytes = ArgTypeToByteConverter.convert(type, StringUtil.trim(leaseTf.getText()));
            return bytes;
        }
    }

    public List<Address> getAccounts() throws InvalidContractInputParamException {
        Enumeration<String> elems = ((DefaultListModel)accountsList.getModel()).elements();
        if(elems == null) return Collections.EMPTY_LIST;

        List<Address> accounts = new ArrayList<>();
        while(elems.hasMoreElements()) {
            String addrStr = elems.nextElement();
            try {
                Address address = new Address(addrStr);
                accounts.add(address);
            } catch (NoSuchAlgorithmException e) {
                throw new InvalidContractInputParamException("Invalid account : " + addrStr);
            }
        }

        return accounts;
    }

    public List<Long> getForeignApps() {
        try {
            String foreignAppsStr = foreignAppsTf.getText();
            if(StringUtil.isEmpty(foreignAppsStr))
                return Collections.EMPTY_LIST;

            List<String> foreingAppIds = StringUtil.split(foreignAppsStr, ",");
            return foreingAppIds.stream()
                    .filter(id -> !StringUtil.isEmpty(id))
                    .map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }

    public List<Long> getForeignAssets() {
        try {
            String foreignAssetsStr = foreignAssetsTf.getText();
            if (StringUtil.isEmpty(foreignAssetsStr))
                return Collections.EMPTY_LIST;

            List<String> foreignAssetsIds = StringUtil.split(foreignAssetsStr, ",");
            return foreignAssetsIds.stream()
                    .filter(id -> !StringUtil.isEmpty(id))
                    .map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        appIdComboBoxModel = new DefaultComboBoxModel<>();
        appIdCB = new ComboBox(appIdComboBoxModel);

        argTypeComboBoxModel
                = new DefaultComboBoxModel(new ArgType[] {ArgType.String, ArgType.Integer, ArgType.Address, ArgType.Base64});
        argTypeCB = new ComboBox(argTypeComboBoxModel);

        argListModel = new DefaultListModel<>();
        argList = new JBList(argListModel);

        noteTypeDefaultComboBoxModel = new DefaultComboBoxModel(new ArgType[] {ArgType.String, ArgType.Base64});
        noteTypeCB = new ComboBox(noteTypeDefaultComboBoxModel);

        leaseTypeDefaultComboBoxModel = new DefaultComboBoxModel<>(new ArgType[] {ArgType.String, ArgType.Base64});
        leaseCB = new ComboBox(leaseTypeDefaultComboBoxModel);

        accountsList = new JBList(new DefaultListModel());
    }
}
