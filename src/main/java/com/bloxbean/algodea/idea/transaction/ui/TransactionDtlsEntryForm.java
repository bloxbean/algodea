package com.bloxbean.algodea.idea.transaction.ui;

import com.bloxbean.algodea.idea.nodeint.model.ArgType;
import com.bloxbean.algodea.idea.nodeint.model.Lease;
import com.bloxbean.algodea.idea.nodeint.model.Note;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.util.ArgTypeToByteConverter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;

import javax.swing.*;
import java.math.BigInteger;

public class TransactionDtlsEntryForm {
    private JPanel mainPanel;
    private JTextField noteTf;
    private JComboBox noteTypeCB;
    private JTextField leaseTf;
    private JComboBox leaseCB;
    private JTextField feeTf;
    private JTextField flatFeeTf;
    private JLabel flatFeeHelpLabel;
    private JLabel feePerByteHelpLabel;

    DefaultComboBoxModel<ArgType> noteTypeDefaultComboBoxModel;
    DefaultComboBoxModel<ArgType> leaseTypeDefaultComboBoxModel;

    Project project;

    public TransactionDtlsEntryForm() {
        feePerByteHelpLabel.setText("<html>Set the fee per bytes value. This value is multiplied by the estimated <br/>size of the transaction  to reach a final transaction fee, or 1000, whichever is higher.</html>");
        flatFeeHelpLabel.setText("<html>Set the flatFee. This value will be used for the transaction fee,<br/> or 1000, whichever is higher.</html>");
    }

    public void initializeData(Project project) {
        this.project = project;
    }

    private Note getNote() {
        ArgType noteType = (ArgType) noteTypeCB.getSelectedItem();
        if(noteType == null)
            return null;
        else {
            return new Note(noteType, StringUtil.trim(noteTf.getText()));
        }
    }

    private byte[] getNoteBytes() throws Exception {
        ArgType noteType = (ArgType) noteTypeCB.getSelectedItem();
        if(noteType == null)
            return null;
        else {
            byte[] bytes = ArgTypeToByteConverter.convert(noteType, StringUtil.trim(noteTf.getText()));
            return bytes;
        }
    }

    private Lease getLease() {
        ArgType type = (ArgType) leaseCB.getSelectedItem();
        if(type == null)
            return null;
        else {
            return new Lease(type, StringUtil.trim(leaseTf.getText()));
        }
    }

    private byte[] getLeaseBytes() throws Exception {
        ArgType type = (ArgType) leaseCB.getSelectedItem();
        if(type == null)
            return null;
        else {
            byte[] bytes = ArgTypeToByteConverter.convert(type, StringUtil.trim(leaseTf.getText()));
            return bytes;
        }
    }

    private BigInteger getFee() {
        if(StringUtil.isEmpty(feeTf.getText()))
            return null;

        try {
            return new BigInteger(StringUtil.trim(feeTf.getText()));
        } catch (Exception e) {
            return null;
        }
    }

    private BigInteger getFlatFee() {
        if(StringUtil.isEmpty(flatFeeTf.getText()))
            return null;

        try {
            return new BigInteger(StringUtil.trim(flatFeeTf.getText()));
        } catch (Exception e) {
            return null;
        }
    }

    public TxnDetailsParameters getTxnDetailsParameters() throws Exception {
        TxnDetailsParameters txnDetailsParameters = new TxnDetailsParameters();
        txnDetailsParameters.setNote(getNoteBytes());
        txnDetailsParameters.setLease(getLeaseBytes());
        txnDetailsParameters.setFee(getFee());
        txnDetailsParameters.setFlatFee(getFlatFee());

        return txnDetailsParameters;
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

        if(!StringUtil.isEmpty(feeTf.getText()) && !StringUtil.isEmpty(flatFeeTf.getText())) {
            return new ValidationInfo("Fee per bytes and Flat fee can not be specified together", flatFeeTf);
        }

        try {
            if(!StringUtil.isEmpty(feeTf.getText())) {
                new BigInteger(StringUtil.trim(feeTf.getText()));
            }
        } catch (Exception e) {
            return new ValidationInfo("Invalid fee", feeTf);
        }

        try {
            if(!StringUtil.isEmpty(flatFeeTf.getText())) {
                new BigInteger(StringUtil.trim(flatFeeTf.getText()));
            }
        } catch (Exception e) {
            return new ValidationInfo("Invalid Flat Fee", flatFeeTf);
        }

        return null;
    }
}
