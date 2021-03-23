package com.bloxbean.algodea.idea.stateless.ui;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.model.Asset;
import com.bloxbean.algodea.idea.assets.model.AssetMeta;
import com.bloxbean.algodea.idea.assets.service.AssetCacheService;
import com.bloxbean.algodea.idea.core.action.ui.TxnDialogWrapper;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.service.AlgoAccountService;
import com.bloxbean.algodea.idea.nodeint.service.AssetTransactionService;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
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
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class TEALOptInAssetDialog extends TxnDialogWrapper implements LogicSigSigningAccountForm.ChangeListener {
    private static final Logger LOG = Logger.getInstance(TEALOptInAssetDialog.class);

    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private TransactionDtlsEntryForm transactionDtlsEntryForm;
    private JComboBox assetIdCB;
    private JButton searchBtn;
    private JTextField assetName;
    private ArgsInputForm argInputForm;
    private LogicSigSigningAccountForm logicSigSigningAccountForm;

    private AlgoConsole console;
    private AlgoAccountService algoAccountService;
    private DefaultComboBoxModel<Object> assetIdComboBoxModel;

    private String contractHash;

    public TEALOptInAssetDialog(Project project, Module module, String contractHash) throws DeploymentTargetNotConfigured {
        super(project, true);
        init();
        setTitle("Opt In Asset - Using TEAL File");

        this.contractHash = contractHash;
        transactionDtlsEntryForm.initializeData(project);
        initializeData(project);
        attachAssetIdSearchHandler(project);
    }

    private void initializeData(Project project) throws DeploymentTargetNotConfigured {

        console = AlgoConsole.getConsole(project);

        logicSigSigningAccountForm.initialize(project);
        argInputForm.initializeData(project);

        if(!StringUtil.isEmpty(contractHash)) //Default mode is contract type
            logicSigSigningAccountForm.setSenderAddress(contractHash);

        try {
            algoAccountService = new AlgoAccountService(project, new LogListenerAdapter(console));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            throw deploymentTargetNotConfigured;
        }
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

    //Change Listener methods
    @Override
    public void contractTypeSelected() {
        logicSigSigningAccountForm.setSenderAddress(contractHash);
    }

    @Override
    public void delegationTypeSelect() {
        logicSigSigningAccountForm.setSenderAddress("");
    }

    @Override
    public void signerAddressChanged(String address) {
        //No need to handle as it's already handled in LogicSigSigningAccountForm
    }
    //Change Listener methods end

    public Address getSenderAddress() {
        return logicSigSigningAccountForm.getSenderAddress();
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

        if(getAssetId() == null) {
            return new ValidationInfo("Please enter or select a valid Asset Id", assetIdCB);
        }

        return transactionDtlsEntryForm.doValidate();
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
        logicSigSigningAccountForm = new LogicSigSigningAccountForm(this);
        assetIdComboBoxModel = new DefaultComboBoxModel<>();
        assetIdCB = new ComboBox(assetIdComboBoxModel);
        assetIdCB.setEditable(true);
    }

    public LogicSigSigningAccountForm getLogicSigSignAccountForm() {
        return logicSigSigningAccountForm;
    }

    public ArgsInputForm getArgsInputForm() {
        return argInputForm;
    }
}
