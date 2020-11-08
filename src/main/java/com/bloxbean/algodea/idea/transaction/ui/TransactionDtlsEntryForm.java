package com.bloxbean.algodea.idea.transaction.ui;

import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;
import com.bloxbean.algodea.idea.nodeint.model.ArgType;
import com.bloxbean.algodea.idea.nodeint.model.Lease;
import com.bloxbean.algodea.idea.nodeint.model.Note;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.AlgoBaseService;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.util.ArgTypeToByteConverter;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;

import javax.swing.*;
import java.math.BigInteger;

public class TransactionDtlsEntryForm {
    private final static Logger LOG = Logger.getInstance(TransactionDtlsEntryForm.class);

    private JPanel mainPanel;
    private JTextField noteTf;
    private JComboBox noteTypeCB;
    private JTextField leaseTf;
    private JComboBox leaseCB;
    private JTextField feeTf;
    private JTextField flatFeeTf;
    private JLabel flatFeeHelpLabel;
    private JLabel feePerByteHelpLabel;
    private JLabel firstValidLastValidLabel;
    private JTextField firstValidTf;
    private JTextField lastValidTf;
    private JButton fetchSuggedParamsBtn;

    DefaultComboBoxModel<ArgType> noteTypeDefaultComboBoxModel;
    DefaultComboBoxModel<ArgType> leaseTypeDefaultComboBoxModel;

    Project project;

    public TransactionDtlsEntryForm() {
        feePerByteHelpLabel.setText("<html>Set the fee per bytes value. This value is multiplied by the estimated <br/>size of the transaction  to reach a final transaction fee, or 1000, whichever is higher.</html>");
        flatFeeHelpLabel.setText("<html>Set the flatFee. This value will be used for the transaction fee,<br/> or 1000, whichever is higher.</html>");
        firstValidLastValidLabel.setText("<html>The first valid and last valid round will be automatically <br/>set if not specified.</html>");

        attachListeners();
    }

    public void initializeData(Project project) {
        this.project = project;
    }

    private void attachListeners() {
        fetchSuggedParamsBtn.addActionListener(e -> {
            ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
                @Override
                public void run() {
                    AlgoConsole algoConsole = AlgoConsole.getConsole(project);
                    algoConsole.clearAndshow();
                    ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
                    try {
                        progressIndicator.setFraction(0.2);
                        AlgoBaseService algoBaseService = new AlgoBaseService(project, new LogListenerAdapter(algoConsole));
                        TransactionParametersResponse transactionParametersResponse = algoBaseService.getSuggestedTxnParams();
                        if(transactionParametersResponse.lastRound != null) {
                            firstValidTf.setText(String.valueOf(transactionParametersResponse.lastRound));
                            lastValidTf.setText(String.valueOf(transactionParametersResponse.lastRound + 1000));
                        }
                    } catch (Exception ex) {
                        algoConsole.showErrorMessage("Error getting suggested params", ex);
                        if(LOG.isDebugEnabled()) {
                            LOG.warn(ex);
                        }
                    } finally {
                        progressIndicator.setFraction(1.0);
                    }
                }
            }, "Fetching suggested params ...", true, project);
        });
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

    private BigInteger getFirstValid() {
        if(StringUtil.isEmpty(firstValidTf.getText()))
            return null;

        try {
            return new BigInteger(StringUtil.trim(firstValidTf.getText()));
        } catch (Exception e) {
            return null;
        }
    }

    private BigInteger getLastValid() {
        if(StringUtil.isEmpty(lastValidTf.getText()))
            return null;

        try {
            return new BigInteger(StringUtil.trim(lastValidTf.getText()));
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
        txnDetailsParameters.setFirstValid(getFirstValid());
        txnDetailsParameters.setLastValid(getLastValid());

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
            //e.printStackTrace();
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

        try {
            if(!StringUtil.isEmpty(firstValidTf.getText())) {
                new BigInteger(StringUtil.trim(firstValidTf.getText()));
            }
        } catch (Exception e) {
            return new ValidationInfo("Invalid value for first valid round", firstValidTf);
        }

        try {
            if(!StringUtil.isEmpty(lastValidTf.getText())) {
                new BigInteger(StringUtil.trim(lastValidTf.getText()));
            }
        } catch (Exception e) {
            return new ValidationInfo("Invalid value for last valid round", lastValidTf);
        }

        return null;
    }
}
