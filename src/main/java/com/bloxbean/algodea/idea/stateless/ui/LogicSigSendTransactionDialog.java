package com.bloxbean.algodea.idea.stateless.ui;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.common.Tuple;
import com.bloxbean.algodea.idea.compile.model.LogicSigMetaData;
import com.bloxbean.algodea.idea.core.action.ui.TxnDialogWrapper;
import com.bloxbean.algodea.idea.core.action.util.AlgoContractModuleHelper;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.AccountAsset;
import com.bloxbean.algodea.idea.nodeint.model.LogicSigType;
import com.bloxbean.algodea.idea.nodeint.service.AlgoAccountService;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.util.AlgoLogicsigUtil;
import com.bloxbean.algodea.idea.nodeint.util.LogicSigUtil;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
import com.bloxbean.algodea.idea.util.AlgoConversionUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBRadioButton;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.bloxbean.algodea.idea.common.AlgoConstants.ALGO;

public class LogicSigSendTransactionDialog extends TxnDialogWrapper {
    private static final Logger LOG = Logger.getInstance(LogicSigSendTransactionDialog.class);

    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private TextFieldWithBrowseButton senderLogicSigTextFieldWithBrowse;
    private JTextField senderAccountTf;
//    private JTextField senderMnemonicTf;
    private JButton senderAccountChooserBtn;
    private JTextField receiverAccountTf;
    private JButton receiverChooserBtn;
    private JTextField amountTf;
    private JRadioButton contractAccountRadioButton;
    private JRadioButton accountDelegationRadioButton;
    private JLabel logicSigTypeLabel;
    private TransactionDtlsEntryForm transactionDtlsEntryForm;
    private JButton multiSigChooserBtn;
    private JRadioButton algoTypeRB;
    private JRadioButton asaTypeRB;
    private JComboBox assetsCB;
    private JLabel unitLabel;
    private JButton fetchAssetsBtn;
    private JButton receiverMultiSigBtn;
    private JTextField closeReminderTo;
    private JButton closeReminderAccountChooserBtn;
    private JTextField senderLogSigTf;

    private ButtonGroup contractType;
    private String buildFolder;
    private String lsigPath;
    private DefaultComboBoxModel assetsComboBoxModel;
    private AlgoConsole console;
    private AlgoAccountService algoAccountService;

    public LogicSigSendTransactionDialog(Project project, Module module) throws DeploymentTargetNotConfigured {
        this(project, module,null);
    }

    public LogicSigSendTransactionDialog(Project project, Module module, String lsigPath) throws DeploymentTargetNotConfigured {
        super(project, true);
        init();
        setTitle("Stateless Smart Contract Transaction - Logic Sig");

        buildFolder = AlgoContractModuleHelper.getBuildFolder(project, module);
        transactionDtlsEntryForm.initializeData(project);
        initializeData(project, lsigPath);

        //Asset selection panel
        ButtonGroup assetTypeButtonGroup = new ButtonGroup();
        assetTypeButtonGroup.add(algoTypeRB);
        assetTypeButtonGroup.add(asaTypeRB);
        algoTypeRB.setSelected(true);
        enableOtherAssetPanel(false);
    }

    private void initializeData(Project project, String lsigPath) throws DeploymentTargetNotConfigured {

        console = AlgoConsole.getConsole(project);
        try {
            algoAccountService = new AlgoAccountService(project, new LogListenerAdapter(console));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            throw deploymentTargetNotConfigured;
        }

        if(!StringUtil.isEmpty(lsigPath)) {
            senderLogicSigTextFieldWithBrowse.setText(lsigPath);
            senderLogicSigTextFieldWithBrowse.setEnabled(false);

            loadLogicSigFile(lsigPath);
        }

        //Default value
        contractAccountRadioButton.setSelected(true);
        enableDisableSenderAccountFields(false);

        contractAccountRadioButton.addActionListener(e -> {
            if(contractAccountRadioButton.isSelected()) {
                enableDisableSenderAccountFields(false);
            }
        });

        accountDelegationRadioButton.addActionListener(e -> {
            if(accountDelegationRadioButton.isSelected()) {
                enableDisableSenderAccountFields(true);
            }
        });

        senderAccountChooserBtn.addActionListener(e -> {
            AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
            if(algoAccount != null) {
                senderAccountTf.setText(algoAccount.getAddress());
            }
        });

        multiSigChooserBtn.addActionListener(e -> {
            AlgoMultisigAccount algoMultisigAccount = AccountChooser.getSelectedMultisigAccount(project, true);
            if(algoMultisigAccount != null) {
                senderAccountTf.setText(algoMultisigAccount.getAddress());
            }
        });

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

                //enable close reminder
                //TODO enableCloseReminderSection(true);
            }
        });

        asaTypeRB.addActionListener(e -> {
            if(asaTypeRB.isSelected()) {
                enableOtherAssetPanel(true);
                setUnitLabel();

                //disable close remninder to
                //TODO enableCloseReminderSection(false);
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
    }

    private void loadLogicSigFile(String lsigPath) {
        logicSigTypeLabel.setText("Loading Logic sig ...");
        this.lsigPath = lsigPath;
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    LogicsigSignature logicsigSignature = AlgoLogicsigUtil.getLogicSigFromFile(lsigPath);
                    LogicSigType type = AlgoLogicsigUtil.getType(logicsigSignature);

                    LogicSigMetaData logicSigMetaData = null;
                    try {
                        //Check if the metadata file is there and populate
                        logicSigMetaData = LogicSigUtil.getLogicSigMetaData(lsigPath);
                    } catch (Exception e) {}

                    if(type != null) {
                        if (LogicSigType.DELEGATION_ACCOUNT.equals(type)) {
                            contractAccountRadioButton.setEnabled(false);
                            accountDelegationRadioButton.setSelected(true);
                            enableDisableSenderAccountFields(true);

                            if(AlgoLogicsigUtil.isMultisigDelegatedAccount(logicsigSignature)) {
                                multiSigChooserBtn.setEnabled(true);
                                senderAccountChooserBtn.setEnabled(false);
                                logicSigTypeLabel.setText("Delegated Signature(Multi-Sig) Logic Sig");

                                if(logicSigMetaData != null
                                        && !StringUtil.isEmpty(logicSigMetaData.multisigAddress)) {
                                    senderAccountTf.setText(logicSigMetaData.multisigAddress);
                                }
                            } else {
                                logicSigTypeLabel.setText("Delegated Signature Logic Sig");
                                multiSigChooserBtn.setEnabled(false);
                                senderAccountChooserBtn.setEnabled(true);

                                if(logicSigMetaData != null
                                        && logicSigMetaData.signingAddresses != null && logicSigMetaData.signingAddresses.size() != 0) {
                                    senderAccountTf.setText(logicSigMetaData.signingAddresses.get(0));
                                }
                            }

                        } else if(LogicSigType.CONTRACT_ACCOUNT.equals(type)) {
                            contractAccountRadioButton.setSelected(true);
                            accountDelegationRadioButton.setEnabled(false);
                            enableDisableSenderAccountFields(false);

                            String contractAddress = logicsigSignature.toAddress().toString();
                            if(!StringUtil.isEmpty(contractAddress)) {
                                logicSigTypeLabel.setText("Contract Account ");
                                senderAccountTf.setText(contractAddress);
                            }
                        }
                    }

                } catch (Exception e) {
                    if(LOG.isDebugEnabled())
                        LOG.warn(e);
                }
            }
        }, ModalityState.any());
    }

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

    public boolean isContractAccountType() {
        return contractAccountRadioButton.isSelected();
    }

    public boolean isAccountDelegationType() {
        return accountDelegationRadioButton.isSelected();
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

    public String getLsigPath() {
        return lsigPath;
    }

    @Override
    protected ValidationInfo doTransactionInputValidation() {
        if(StringUtil.isEmpty(getLsigPath())) {
            return new ValidationInfo("Please select a valid lsig fie", senderLogSigTf);
        }

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

    private void enableDisableSenderAccountFields(boolean flag) {
        senderAccountTf.setText("");
        senderAccountTf.setEditable(flag);
        senderAccountChooserBtn.setEnabled(flag);
        multiSigChooserBtn.setEnabled(flag);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        transactionDtlsEntryForm = new TransactionDtlsEntryForm();
        contractType = new ButtonGroup();

        contractAccountRadioButton = new JBRadioButton();
        accountDelegationRadioButton = new JBRadioButton();
        contractType.add(contractAccountRadioButton);
        contractType.add(accountDelegationRadioButton);


        senderLogSigTf = new JTextField();
        senderLogSigTf.setEditable(false);
        senderLogicSigTextFieldWithBrowse = new TextFieldWithBrowseButton(senderLogSigTf, e -> {
            JFileChooser fc = new JFileChooser();
            if(buildFolder != null) {
                File lsigFolder = new File(buildFolder, "lsigs");
                if(lsigFolder.exists()) {
                    fc.setCurrentDirectory(lsigFolder);
                } else {
                    fc.setCurrentDirectory(new File(buildFolder));
                }
            }
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if(f.getName().endsWith(".lsig") || f.getName().endsWith(".LSIG"))
                        return true;
                    else
                        return false;
                }

                @Override
                public String getDescription() {
                    return "Logic Sig file";
                }
            });
            fc.showDialog(mainPanel, "Select");
            File file = fc.getSelectedFile();
            if (file == null) {
                return;
            }

            //if(!StringUtil.isEmpty(buildFolder)) {
                senderLogSigTf.setText(file.getAbsolutePath());
                loadLogicSigFile(file.getAbsolutePath());
           // }
        });

        assetsComboBoxModel = new DefaultComboBoxModel();
        assetsCB = new ComboBox(assetsComboBoxModel);
    }
}
