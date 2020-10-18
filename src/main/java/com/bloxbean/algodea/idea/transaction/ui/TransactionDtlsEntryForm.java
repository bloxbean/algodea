package com.bloxbean.algodea.idea.transaction.ui;

import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.nodeint.model.ApplArg;
import com.bloxbean.algodea.idea.nodeint.model.ArgType;
import com.bloxbean.algodea.idea.nodeint.model.Lease;
import com.bloxbean.algodea.idea.nodeint.model.Note;
import com.bloxbean.algodea.idea.nodeint.util.ArgTypeToByteConverter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;

import javax.swing.*;

public class TransactionDtlsEntryForm {
    private JPanel mainPanel;
    private JTextField noteTf;
    private JComboBox noteTypeCB;
    private JTextField leaseTf;
    private JComboBox leaseCB;

    DefaultComboBoxModel<ArgType> noteTypeDefaultComboBoxModel;
    DefaultComboBoxModel<ArgType> leaseTypeDefaultComboBoxModel;

    Project project;

    public TransactionDtlsEntryForm() {

    }

    public void initializeData(Project project) {
        this.project = project;
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


    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        noteTypeDefaultComboBoxModel = new DefaultComboBoxModel(new ArgType[] {ArgType.String, ArgType.Base64});
        noteTypeCB = new ComboBox(noteTypeDefaultComboBoxModel);

        leaseTypeDefaultComboBoxModel = new DefaultComboBoxModel<>(new ArgType[] {ArgType.String, ArgType.Base64});
        leaseCB = new ComboBox(leaseTypeDefaultComboBoxModel);
    }

    public ValidationInfo doValidate() {
        try {
            getNoteBytes();
        } catch (Exception e) {
            return new ValidationInfo("Invalid note", noteTf);
        }

        try {
            if(!StringUtil.isEmpty(leaseTf.getText())) {
                byte[] bytes = getLeaseBytes();

                if (bytes != null && (bytes.length != 0 && bytes.length != 32)) {
                    return new ValidationInfo("Lease should be 0 or 32 bytes", leaseTf);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
