package com.bloxbean.algodea.idea.stateless.ui;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.LogicsigSignature;
import com.algorand.algosdk.v2.client.model.Asset;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.assets.model.AssetMeta;
import com.bloxbean.algodea.idea.assets.service.AssetCacheService;
import com.bloxbean.algodea.idea.compile.model.LogicSigMetaData;
import com.bloxbean.algodea.idea.core.action.ui.TxnDialogWrapper;
import com.bloxbean.algodea.idea.core.action.util.AlgoContractModuleHelper;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.LogicSigType;
import com.bloxbean.algodea.idea.nodeint.service.AlgoAccountService;
import com.bloxbean.algodea.idea.nodeint.service.AssetTransactionService;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.util.AlgoLogicsigUtil;
import com.bloxbean.algodea.idea.nodeint.util.LogicSigUtil;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBRadioButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.List;

public class LogicSigOptInAssetDialog extends TxnDialogWrapper {
    private static final Logger LOG = Logger.getInstance(LogicSigOptInAssetDialog.class);

    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private TextFieldWithBrowseButton senderLogicSigTextFieldWithBrowse;
    private JTextField senderAccountTf;
    private JButton senderAccountChooserBtn;
    private JRadioButton contractAccountRadioButton;
    private JRadioButton accountDelegationRadioButton;
    private JLabel logicSigTypeLabel;
    private TransactionDtlsEntryForm transactionDtlsEntryForm;
    private JButton multiSigChooserBtn;
    private JComboBox assetIdCB;
    private JButton searchBtn;
    private JTextField assetName;
    private JTextField senderLogSigTf;

    private ButtonGroup contractType;
    private String buildFolder;
    private String lsigPath;
    private DefaultComboBoxModel assetsComboBoxModel;
    private AlgoConsole console;
    private AlgoAccountService algoAccountService;
    private DefaultComboBoxModel<Object> assetIdComboBoxModel;

    public LogicSigOptInAssetDialog(Project project, Module module) throws DeploymentTargetNotConfigured {
        this(project, module,null);
    }

    public LogicSigOptInAssetDialog(Project project, Module module, String lsigPath) throws DeploymentTargetNotConfigured {
        super(project, true);
        init();
        setTitle("Opt In Asset - Logic Sig");

        buildFolder = AlgoContractModuleHelper.getBuildFolder(project, module);
        transactionDtlsEntryForm.initializeData(project);
        initializeData(project, lsigPath);
        attachAssetIdSearchHandler(project);
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

    private void attachAssetIdSearchHandler(Project project) {
        AlgoConsole algoConsole = AlgoConsole.getConsole(project);
        algoConsole.clearAndshow();

        AssetTransactionService assetTransactionService = null;
        try {
            assetTransactionService = new AssetTransactionService(project, new LogListenerAdapter(algoConsole));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            //deploymentTargetNotConfigured.printStackTrace();
            showErrorMessage("Algorand Node is not configured for deployment target", "Asset Search");
            return;
        }

        AssetCacheService assetCacheService = AssetCacheService.getInstance();
        List<AssetMeta> assets = null;
        if(assetCacheService != null) {
            if(!StringUtil.isEmpty(assetTransactionService.getNetworkGenesisHash()))
                assets = assetCacheService.getAssets(assetTransactionService.getNetworkGenesisHash());

            if(assets != null) {
                assetIdCB.addItem(new AssetMeta("", ""));
                for(AssetMeta asset: assets) {
                    assetIdCB.addItem(asset);
                }
            }
        }

        final AssetTransactionService finalAssetTransactionService = assetTransactionService;
        searchBtn.addActionListener(e -> {
            //clearFieldsForModifyMode();
            assetName.setText("");
            Object selectedItem = assetIdCB.getSelectedItem();
            if(selectedItem == null)
                return;

            String assetId;
            if(selectedItem instanceof AssetMeta) {
                AssetMeta assetMeta = (AssetMeta) assetIdCB.getSelectedItem();//assetIdTf.getText();
                assetId = assetMeta.getId();
            } else {
                assetId = String.valueOf(selectedItem);
            }

            searchBtn.setEnabled(false);
            setOKActionEnabled(false);

            Long lassetId;
            try {
                lassetId = Long.parseLong(assetId);
            } catch (NumberFormatException ex) {
                Messages.showErrorDialog("Invalid asset id", "Asset Search");
                searchBtn.setEnabled(true);
                setOKActionEnabled(true);
                return;
            }

            Task.Backgroundable task = new Task.Backgroundable(project, "Serach Asset") {
                Asset asset;

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    try {

                        asset = finalAssetTransactionService.getAsset(lassetId);

                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }

                @Override
                public void onThrowable(@NotNull Throwable error) {
                    if(error != null && error.getCause() != null
                            &&error.getCause() instanceof DeploymentTargetNotConfigured) {
                        showErrorMessage("Algorand Node is not configured for deployment target", "Asset Search");
                    } else {
                        showErrorMessage(String.format("Error getting asset details for asset id: %s", assetId), "Fetching Asset details");
                    }
                }

                @Override
                public void onFinished() {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        searchBtn.setEnabled(true);
                        setOKActionEnabled(true);
                    });
                }

                @Override
                public void onSuccess() {
                    if(asset != null) {
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                assetName.setText(asset.params.name);
                                //populateWithAssetInfo(asset, assetId);
                            }
                        });
                    } else {
                        showErrorMessage(String.format("Error getting asset details for asset id: %s", assetId), "Fetching Asset details");
                    }
                }

                @Override
                public void onCancel() {
                    algoConsole.showWarningMessage("Asset search was cancelled");

                }
            };

            BackgroundableProcessIndicator processIndicator = new BackgroundableProcessIndicator(task);
            ProgressManager.getInstance().runProcessWithProgressAsynchronously(task,processIndicator);
        });
    }


    public Long getAssetId() {
        try {
            Object selected = assetIdCB.getSelectedItem();
            if (selected != null && selected instanceof AssetMeta) {
                return Long.parseLong(((AssetMeta) selected).getId());
            } else
                return selected != null ? Long.parseLong(selected.toString()) : null;
        } catch (Exception e) {
            if(LOG.isDebugEnabled()) {
                LOG.debug("Error getting asset id", e);
            }
            return null;
        }
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

//        if(getReceiverAddress() == null) {
//            return new ValidationInfo("Choose or enter a valid account", receiverAccountTf);
//        }
//
//        if(getAmount() == null) {
//            return new ValidationInfo("Enter a valid amount", amountTf);
//        }

        if(getAssetId() == null) {
            return new ValidationInfo("Please enter or select a valid Asset Id", assetIdCB);
        }

        return transactionDtlsEntryForm.doValidate();
    }

    private void enableDisableSenderAccountFields(boolean flag) {
        senderAccountTf.setText("");
        senderAccountTf.setEditable(flag);
        senderAccountChooserBtn.setEnabled(flag);
        multiSigChooserBtn.setEnabled(flag);
    }

    private void showErrorMessage(String message, String title) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                Messages.showErrorDialog(message, title);
            }
        }, ModalityState.any());
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

        assetIdComboBoxModel = new DefaultComboBoxModel<>();
        assetIdCB = new ComboBox(assetIdComboBoxModel);
        assetIdCB.setEditable(true);
    }
}
