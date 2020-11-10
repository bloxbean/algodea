package com.bloxbean.algodea.idea.stateless.ui;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.common.Tuple;
import com.bloxbean.algodea.idea.core.action.ui.TxnDialogWrapper;
import com.bloxbean.algodea.idea.core.action.util.AlgoContractModuleHelper;
import com.bloxbean.algodea.idea.nodeint.model.LogicSigType;
import com.bloxbean.algodea.idea.nodeint.util.AlgoLogicsigUtil;
import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
import com.bloxbean.algodea.idea.util.AlgoConversionUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBRadioButton;
import org.apache.commons.logging.Log;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;

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
    private JTextField senderLogSigTf;

    private ButtonGroup contractType;
    private String buildFolder;
    private String lsigPath;

    public LogicSigSendTransactionDialog(Project project) {
        this(project, null);
    }

    public LogicSigSendTransactionDialog(Project project, String lsigPath) {
        super(project, true);
        init();
        setTitle("Stateless Smart Contract Transaction - Logic Sig");

        buildFolder = AlgoContractModuleHelper.getBuildFolder(project);
        transactionDtlsEntryForm.initializeData(project);
        initializeData(project, lsigPath);
    }

    private void initializeData(Project project, String lsigPath) {

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

                    if(type != null) {
                        if (LogicSigType.DELEGATION_ACCOUNT.equals(type)) {
                            contractAccountRadioButton.setEnabled(false);
                            accountDelegationRadioButton.setSelected(true);
                            enableDisableSenderAccountFields(true);

                            if(AlgoLogicsigUtil.isMultisigDelegatedAccount(logicsigSignature)) {
                                multiSigChooserBtn.setEnabled(true);
                                senderAccountChooserBtn.setEnabled(false);
                                logicSigTypeLabel.setText("Multi-Signature Delegated Logic Sig");
                            } else {
                                logicSigTypeLabel.setText("Account Delegated Logic Sig");
                                multiSigChooserBtn.setEnabled(false);
                                senderAccountChooserBtn.setEnabled(true);
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
//                    if(logicsigSignature != null) {
//                        String contractAddress = logicsigSignature.toAddress().toString();
//                        if(StringUtil.isEmpty(contractAddress)) {
//                            logicSigTypeLabel.setText(contractAddress);
//                        }
//                    }

                } catch (Exception e) {
                    if(LOG.isDebugEnabled())
                        LOG.warn(e);
                }
            }
        }, ModalityState.any());
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

    public Tuple<Double, BigInteger> getAmount() {
        try {
            BigDecimal amtInAlgo = new BigDecimal(amountTf.getText());
            BigInteger microAlgo = AlgoConversionUtil.algoTomAlgo(amtInAlgo);

            return new Tuple(amtInAlgo, microAlgo);
        } catch (Exception e) {
            return null;
        }
    }

    public TransactionDtlsEntryForm getTransactionDtlsEntryForm() {
        return transactionDtlsEntryForm;
    }

    public String getLsigPath() {
        return lsigPath;
    }

    @Override
    protected ValidationInfo doTransactionInputValidation() {
        if(getReceiverAddress() == null) {
            return new ValidationInfo("Choose or enter a valid account", receiverAccountTf);
        }

        if(getAmount() == null) {
            return new ValidationInfo("Enter a valid amount", amountTf);
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

            if(!StringUtil.isEmpty(buildFolder)) {
                senderLogSigTf.setText(file.getAbsolutePath());
                loadLogicSigFile(file.getAbsolutePath());
            }
        });
    }
}
