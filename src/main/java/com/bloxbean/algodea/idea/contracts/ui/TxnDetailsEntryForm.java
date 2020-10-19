package com.bloxbean.algodea.idea.contracts.ui;

import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.nodeint.exception.InvalidInputParamException;
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
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

public class TxnDetailsEntryForm {
    private JTextField foreignAppsTf;
    private JTextField foreignAssetsTf;
    private JTextField accountsTf;
    private JList accountsList;
    private JList argList;
    private JButton accountAddBtn;
    private JButton argAddBtn;
    private JPanel mainPanel;
    private JComboBox<ArgType> argTypeCB;
    private JTextField argTf;
    private JTextField noteTf;
    private JComboBox noteTypeCB;
    private JButton accountsDelBtn;
    private JButton argDelBtn;
    private JTextField leaseTf;
    private JComboBox leaseCB;
    private JButton accountsChooserBtn;

    DefaultComboBoxModel<ArgType> argTypeComboBoxModel;
    DefaultListModel<ApplArg> argListModel;
    DefaultComboBoxModel<ArgType> noteTypeDefaultComboBoxModel;
    DefaultComboBoxModel<ArgType> leaseTypeDefaultComboBoxModel;

    Project project;

    public TxnDetailsEntryForm() {

    }

    public void initializeData(Project project) {
        this.project = project;
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

    public List<Address> getAccounts() throws InvalidInputParamException {
        Enumeration<String> elems = ((DefaultListModel)accountsList.getModel()).elements();
        if(elems == null) return Collections.EMPTY_LIST;

        List<Address> accounts = new ArrayList<>();
        while(elems.hasMoreElements()) {
            String addrStr = elems.nextElement();
            try {
                Address address = new Address(addrStr);
                accounts.add(address);
            } catch (NoSuchAlgorithmException e) {
                throw new InvalidInputParamException("Invalid account : " + addrStr);
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
