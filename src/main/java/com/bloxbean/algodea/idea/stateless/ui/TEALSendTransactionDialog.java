package com.bloxbean.algodea.idea.stateless.ui;

import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.common.Tuple;
import com.bloxbean.algodea.idea.core.action.ui.TxnDialogWrapper;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.AccountAsset;
import com.bloxbean.algodea.idea.nodeint.service.AlgoAccountService;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
import com.bloxbean.algodea.idea.util.AlgoConversionUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.bloxbean.algodea.idea.common.AlgoConstants.ALGO;

public class TEALSendTransactionDialog extends TxnDialogWrapper implements LogicSigSigningAccountForm.ChangeListener {
    private static final Logger LOG = Logger.getInstance(TEALSendTransactionDialog.class);

    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JTextField receiverAccountTf;
    private JButton receiverChooserBtn;
    private JTextField amountTf;

    private TransactionDtlsEntryForm transactionDtlsEntryForm;
    private JRadioButton algoTypeRB;
    private JRadioButton asaTypeRB;
    private JComboBox assetsCB;
    private JLabel unitLabel;
    private JButton fetchAssetsBtn;
    private JButton receiverMultiSigBtn;
    private JTextField closeReminderTo;
    private JButton closeReminderAccountChooserBtn;
    private LogicSigSigningAccountForm logicSigSigningAccountForm;
    private ArgsInputForm argInputForm;
    private JTextField senderAccountTf;
    private JButton closeReminderToMultiSigChooserBtn;

//    private String lsigPath;
    private DefaultComboBoxModel assetsComboBoxModel;
    private AlgoConsole console;
    private AlgoAccountService algoAccountService;
    private String contractHash;

    public TEALSendTransactionDialog(Project project, Module module, String contractHash) throws DeploymentTargetNotConfigured {
        super(project, true);
        init();
        setTitle("Stateless Smart Contract Transaction - Using TEAL file");

        this.contractHash = contractHash;
        transactionDtlsEntryForm.initializeData(project);
        initializeData(project);

        //Asset selection panel
        ButtonGroup assetTypeButtonGroup = new ButtonGroup();
        assetTypeButtonGroup.add(algoTypeRB);
        assetTypeButtonGroup.add(asaTypeRB);
        algoTypeRB.setSelected(true);
        enableOtherAssetPanel(false);
    }

    private void initializeData(Project project) throws DeploymentTargetNotConfigured {

        console = AlgoConsole.getConsole(project);

        logicSigSigningAccountForm.initialize(project);
        argInputForm.initializeData(project);

        if(!StringUtil.isEmpty(contractHash)) //Default mode is contract type
            senderAccountTf.setText(contractHash);

        try {
            algoAccountService = new AlgoAccountService(project, new LogListenerAdapter(console));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            throw deploymentTargetNotConfigured;
        }

        receiverChooserBtn.addActionListener(e -> {
            AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
            if(algoAccount != null) {
                receiverAccountTf.setText(algoAccount.getAddress());
            }
        });

        receiverMultiSigBtn.addActionListener(e -> {
            AlgoMultisigAccount algoMultisigAccount = AccountChooser.getSelectedMultisigAccount(project, true);
            if(algoMultisigAccount != null) {
                receiverAccountTf.setText(algoMultisigAccount.getAddress());
            }
        });

        fetchAssetsBtn.addActionListener(e -> {
            fetchAssetFortheAccount(project);
        });

        algoTypeRB.addActionListener(e -> {
            if(algoTypeRB.isSelected()) {
                enableOtherAssetPanel(false);
                setUnitLabel();
            }
        });

        asaTypeRB.addActionListener(e -> {
            if(asaTypeRB.isSelected()) {
                enableOtherAssetPanel(true);
                setUnitLabel();
            }
        });

        assetsCB.addActionListener(e -> {
            setUnitLabel();
        });

        unitLabel.setText(ALGO);

        closeReminderTo.setToolTipText("<html>When set, it indicates that the transaction is requesting that the Sender account<br/>" +
                " should be closed, and all remaining funds,<br/>" +
                " after the fee and amount are paid, be transferred to this address.</html>");
        closeReminderAccountChooserBtn.addActionListener(e -> {
            AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
            if(algoAccount != null) {
                closeReminderTo.setText(algoAccount.getAddress());
            }
        });

        closeReminderToMultiSigChooserBtn.addActionListener(e -> {
            AlgoMultisigAccount algoMultisigAccount = AccountChooser.getSelectedMultisigAccount(project, true);
            if(algoMultisigAccount != null) {
                closeReminderTo.setText(algoMultisigAccount.getAddress());
            }
        });
    }

    //Change Listener methods
    @Override
    public void contractTypeSelected() {
        senderAccountTf.setText(contractHash);
    }

    @Override
    public void delegationTypeSelect() {
        senderAccountTf.setText("");
    }

    @Override
    public void signerAddressChanged(String address) {
        senderAccountTf.setText(address);
    }
    //Change Listener methods end

    private void enableOtherAssetPanel(boolean flag) {
        assetsCB.setEnabled(flag);
        fetchAssetsBtn.setEnabled(flag);
    }

    private void setUnitLabel() {
        if(isAlgoTransfer()) {
            unitLabel.setText(ALGO);
        } else {
            AccountAsset accountAsset = (AccountAsset) assetsCB.getSelectedItem();
            if (accountAsset == null)
                return;

            if (!StringUtil.isEmpty(accountAsset.getAssetUnit()))
                unitLabel.setText(accountAsset.getAssetUnit());
        }
    }

    public boolean isAlgoTransfer() {
        return algoTypeRB.isSelected();
    }

    public AccountAsset getAsset() {
        if(isAlgoTransfer())
            return null;
        AccountAsset accountAsset = (AccountAsset)assetsCB.getSelectedItem();
        return accountAsset;
    }

    public Long getAssetId() {
        if(isAlgoTransfer())
            return null;

        AccountAsset accountAsset = (AccountAsset)assetsCB.getSelectedItem();
        if(accountAsset == null)
            return null;
        else
            return accountAsset.getAssetId();
    }

    private void fetchAssetFortheAccount(Project project) {
        ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
            @Override
            public void run() {
                ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
                progressIndicator.setIndeterminate(false);

                try {
                    assetsComboBoxModel.removeAllElements();

                    Address senderAddress = getSenderAddress();

                    List<AccountAsset> accountAssets = algoAccountService.getAccountAssets(senderAddress.toString());
                    if (accountAssets == null || accountAssets.size() == 0)
                        return;

                    for (AccountAsset accountAsset : accountAssets) {
                        assetsCB.addItem(accountAsset);
                    }

                    if (assetsCB.getModel().getSize() > 0) {
                        assetsCB.setSelectedIndex(0);
                    }
                } catch (Exception e) {
                    console.showErrorMessage("Error getting asset information for the account", e);
                } finally {
                    progressIndicator.setFraction(1.0);
                }

            }
        }, "Fetching account assets from Algorand node ...", true, project);
    }

    public Address getSenderAddress() {
        String sender = StringUtil.trim(senderAccountTf.getText());
        if(StringUtil.isEmpty(sender))
            return null;

        try {
            return new Address(sender);
        } catch (Exception e) {
            return null;
        }
    }

    public Address getReceiverAddress() {
        String acc = StringUtil.trim(receiverAccountTf.getText());
        if(StringUtil.isEmpty(acc))
            return null;

        try {
            Address address = new Address(acc);
            return address;
        } catch (Exception e) {
            return null;
        }
    }

    public Tuple<BigDecimal, BigInteger> getAmount() {

        try {
            if(isAlgoTransfer()) {
                BigDecimal amtInAlgo = new BigDecimal(amountTf.getText());
                BigInteger microAlgo = AlgoConversionUtil.algoTomAlgo(amtInAlgo);

                return new Tuple(amtInAlgo, microAlgo);
            } else { //Asset transfer
                AccountAsset accountAsset = getAsset();
                BigDecimal assetAmount = new BigDecimal(amountTf.getText());

                BigInteger mAmount = null;
                if(accountAsset != null) {
                    mAmount = AlgoConversionUtil.assetFromDecimal(assetAmount, accountAsset.getDecimals());
                }
                return new Tuple<>(assetAmount, mAmount);
            }
        } catch (Exception e) {
            if(LOG.isDebugEnabled())
                LOG.warn(e);

            return null;
        }
    }

    public Address getCloseReminderTo() throws Exception {
        String closeReminderToAdd = StringUtil.trim(closeReminderTo.getText());
        if(StringUtil.isEmpty(closeReminderToAdd))
            return null;

        Address address = new Address(closeReminderToAdd);
        return address;
    }

    public TransactionDtlsEntryForm getTransactionDtlsEntryForm() {
        return transactionDtlsEntryForm;
    }

    @Override
    protected ValidationInfo doTransactionInputValidation() {
        ValidationInfo validationInfo = logicSigSigningAccountForm.doValidate();
        if(validationInfo != null)
            return validationInfo;

        validationInfo = argInputForm.doValidate();
        if(validationInfo != null)
            return validationInfo;

        if(getReceiverAddress() == null) {
            return new ValidationInfo("Choose or enter a valid account", receiverAccountTf);
        }

        if(getAmount() == null) {
            return new ValidationInfo("Enter a valid amount", amountTf);
        }

        if(!isAlgoTransfer() && getAssetId() == null) {
            return new ValidationInfo("Please select a valid asset", assetsCB);
        }

        try {
            getCloseReminderTo();
        } catch (Exception e) {
            return new ValidationInfo("Invalid Close Reminder To address", closeReminderTo);
        }

        return transactionDtlsEntryForm.doValidate();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        logicSigSigningAccountForm = new LogicSigSigningAccountForm(this);
        transactionDtlsEntryForm = new TransactionDtlsEntryForm();
        assetsComboBoxModel = new DefaultComboBoxModel();
        assetsCB = new ComboBox(assetsComboBoxModel);
    }

    public LogicSigSigningAccountForm getLogicSigSignAccountForm() {
        return logicSigSigningAccountForm;
    }

    public ArgsInputForm getArgsInputForm() {
        return argInputForm;
    }
}
