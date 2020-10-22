package com.bloxbean.algodea.idea.assets.ui;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.model.Asset;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountService;
import com.bloxbean.algodea.idea.assets.action.AssetActionType;
import com.bloxbean.algodea.idea.assets.model.AssetMeta;
import com.bloxbean.algodea.idea.assets.service.AssetCacheService;
import com.bloxbean.algodea.idea.core.service.AlgoCacheService;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.ArgType;
import com.bloxbean.algodea.idea.nodeint.model.AssetTxnParameters;
import com.bloxbean.algodea.idea.nodeint.service.AssetTransactionService;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.util.ArgTypeToByteConverter;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.transaction.ui.AccountEntryInputForm;
import com.bloxbean.algodea.idea.transaction.ui.ManagedAccountEntryInputForm;
import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBCheckBox;
import org.apache.commons.lang.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public class AssetConfigurationDialog extends DialogWrapper {
    private JTabbedPane tabbedPane1;
    private JPanel mainPanel;
    private JTextField assetNameTf;
    private JTextField unitNameTf;
    private JTextField totalSupplyTf;
    private JTextField decimalsTf;
    private JTextField assetUrlTf;
    private ManagedAccountEntryInputForm freezeAddressInputForm;
    private AccountEntryInputForm creatorAddressInputForm;
    private ManagedAccountEntryInputForm managerAddressInputForm;
    private ManagedAccountEntryInputForm reserveAddressInputForm;
    private ManagedAccountEntryInputForm clawbackAddressInputForm;
    private TransactionDtlsEntryForm transactionDtlsEntryForm;
    private JTextField metadataHashTf;
    private JComboBox metadataHashType;
    private JCheckBox defaultFrozenCB;
    private JButton assetSearchBtn;
    private JLabel assetIdLabel;
    private JComboBox assetIdCB;
    private JPanel assetIdPanel;

//    private boolean modifyMode;
    private AssetActionType actionType;

    private DefaultComboBoxModel<AssetMeta> assetIdComboBoxModel;

    public AssetConfigurationDialog(@Nullable Project project) {
        this(project, AssetActionType.CREATE);
    }

    public AssetConfigurationDialog(@Nullable Project project, AssetActionType actionType) {
        super(project, true);
        init();
        setTitle("Asset Configuration");

//        if(AssetActionType.MODIFY.equals(actionType)) {
//            this.modifyMode = true;
//        }

        this.actionType = actionType;

        if(AssetActionType.CREATE.equals(actionType)) {
            initializeData(project);
        } else if(AssetActionType.MODIFY.equals(actionType)) {
            initializeModify(project);
        } else {
            initializeViewMode(project);
        }

        managerAddressInputForm.setTooltipText("<html>The manager account is the only account that can authorize" +
                " <br/>transactions to reconfigure and destroy an asset.</html>");
        reserveAddressInputForm.setTooltipText("<html>The reserve account that holds the reserve<br/>" +
                "This address has no specific authority in the protocol itself. It is used in the case where you<br/>" +
                "want to signal to holders of your asset that the non-minted units of the asset reside in an account<br/>" +
                " that is different from the default creator account (the sender) (non-minted) units of the asset.</html>");

        freezeAddressInputForm.setTooltipText("<html>The freeze account is used to freeze holdings of this asset. <br/>" +
                "If empty, freezing is not permitted.</html>");

        clawbackAddressInputForm.setTooltipText("<html>The clawback account that can clawback holdings of this asset.<br/>" +
                " If empty, clawback is not permitted.</html>");
    }

    private void initializeData(Project project) {
        //disable assetId and search fields
        assetIdLabel.setVisible(false);
        assetIdPanel.setVisible(false);
        assetIdCB.setVisible(false);
        assetSearchBtn.setVisible(false);

        _initialize(project);
    }

    private void initializeModify(Project project) {
        creatorAddressInputForm.setAccountLabel("Txn Sender (Manager) "); //Sender here is manager
        _initialize(project);

        disableFieldsForModifyMode();

        attachAssetIdSearchHandler(project);
    }

    private void initializeViewMode(Project project) {
        if(AssetActionType.OPT_IN.equals(actionType)) {
            creatorAddressInputForm.setAccountLabel("Txn Sender (OptIn account)"); //Sender here is manager
            setOKButtonText("Opt In");
        } else {
            creatorAddressInputForm.setAccountLabel("Txn Sender "); //Sender here is manager
        }

        _initialize(project);
        disableFieldsForReadonlyMode();
        attachAssetIdSearchHandler(project);
    }

    private void attachAssetIdSearchHandler(Project project) {
        AlgoConsole algoConsole = AlgoConsole.getConsole(project);
        algoConsole.clearAndshow();

        AssetTransactionService assetTransactionService = null;
        try {
            assetTransactionService = new AssetTransactionService(project, new LogListenerAdapter(algoConsole));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            deploymentTargetNotConfigured.printStackTrace();
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
        assetSearchBtn.addActionListener(e -> {
            clearFieldsForModifyMode();
            AssetMeta assetMeta = (AssetMeta) assetIdCB.getSelectedItem();//assetIdTf.getText();
            String assetId = assetMeta.getId();

            assetSearchBtn.setEnabled(false);
            setOKActionEnabled(false);

            Long lassetId;
            try {
                lassetId = Long.parseLong(assetId);
            } catch (NumberFormatException ex) {
                Messages.showErrorDialog("Invalid asset id", "Asset Search");
                assetSearchBtn.setEnabled(true);
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
                        assetSearchBtn.setEnabled(true);
                        setOKActionEnabled(true);
                    });
                }

                @Override
                public void onSuccess() {
                    if(asset != null) {
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                populateWithAssetInfo(asset, assetId);
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

    private void populateWithAssetInfo(Asset asset, String assetId) {
        if(asset == null) {
            Messages.showErrorDialog(String.format("Asset %s not found", assetId), "Asset Search");
            return;
        }

        //populate fields
        assetNameTf.setText(asset.params.name);
        unitNameTf.setText(asset.params.unitName);
        totalSupplyTf.setText(String.valueOf(asset.params.total));
        decimalsTf.setText(String.valueOf(asset.params.decimals));
        assetUrlTf.setText(asset.params.url);
        metadataHashTf.setText(asset.params.metadataHash());
        defaultFrozenCB.setSelected(asset.params.defaultFrozen);

        if(!StringUtil.isEmpty(asset.params.manager))
            managerAddressInputForm.setAccount(asset.params.manager);
        else
            managerAddressInputForm.setEnable(false);

        if(!StringUtil.isEmpty(asset.params.freeze))
            freezeAddressInputForm.setAccount(asset.params.freeze);
        else
            freezeAddressInputForm.setEnable(false);

        if(!StringUtil.isEmpty(asset.params.reserve))
            reserveAddressInputForm.setAccount(asset.params.reserve);
        else
            reserveAddressInputForm.setEnable(false);

        if(!StringUtil.isEmpty(asset.params.clawback))
            clawbackAddressInputForm.setAccount(asset.params.clawback);
        else
            clawbackAddressInputForm.setEnable(false);

        //Creator account
        if(AssetActionType.MODIFY.equals(actionType)) {
            if (!StringUtil.isEmpty(asset.params.manager)) {
                AccountService accountService = AccountService.getAccountService();
                AlgoAccount managerAcc = accountService.getAccountByAddress(asset.params.manager);
                if (managerAcc != null) {
                    creatorAddressInputForm.setMnemonic(managerAcc.getMnemonic());
                }
            }
        }
    }

    private void showErrorMessage(String message, String title) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                Messages.showErrorDialog(message, title);
            }
        }, ModalityState.any());
    }

    private void disableFieldsForModifyMode() {
        assetNameTf.setEditable(false);
        unitNameTf.setEditable(false);
        totalSupplyTf.setEditable(false);
        decimalsTf.setEditable(false);
        assetUrlTf.setEditable(false);
        metadataHashTf.setEditable(false);
        metadataHashType.setEnabled(false);
        defaultFrozenCB.setEnabled(false);
    }


    private void disableFieldsForReadonlyMode() {
        disableFieldsForModifyMode();

        managerAddressInputForm.setEnable(false);
        freezeAddressInputForm.setEnable(false);
        reserveAddressInputForm.setEnable(false);
        clawbackAddressInputForm.setEnable(false);
    }

    private void clearFieldsForModifyMode() {
        assetNameTf.setText("");
        unitNameTf.setText("");
        totalSupplyTf.setText("");
        decimalsTf.setText("");
        assetUrlTf.setText("");
        metadataHashTf.setText("");
        defaultFrozenCB.setSelected(false);

        managerAddressInputForm.setAccount("");
        freezeAddressInputForm.setAccount("");
        reserveAddressInputForm.setAccount("");
        clawbackAddressInputForm.setAccount("");
    }

    private void _initialize(Project project) {
        decimalsTf.setText("0");
        metadataHashTf.setToolTipText("0 or 32 bytes");

        creatorAddressInputForm.initializeData(project);
        managerAddressInputForm.initializeData(project);
        reserveAddressInputForm.initializeData(project);
        freezeAddressInputForm.initializeData(project);
        clawbackAddressInputForm.initializeData(project);
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {

        if(AssetActionType.CREATE.equals(actionType)) {
            //Assets form validation
            try {
                if (getTotalSupply() == null) {
                    return new ValidationInfo("Please enter a valid uint64 value", totalSupplyTf);
                }
            } catch (Exception e) {
                return new ValidationInfo("Please enter a valid uint64 value", totalSupplyTf);
            }

            try {
                getDecimal();
            } catch (Exception e) {
                return new ValidationInfo("Please enter a valid uint32 value", decimalsTf);
            }

            try {
                if (!StringUtil.isEmpty(metadataHashTf.getText())) {
                    byte[] bytes = getMetadataHashBytes();

                    if (bytes != null && (bytes.length != 0 && bytes.length != 32)) {
                        return new ValidationInfo("Metadata hash should be 0 or 32 bytes", metadataHashTf);
                    }
                }
            } catch (Exception e) {

            }

            //validate URL length 32 bytes
            if (!StringUtil.isEmpty(getUrl()) && getUrl().length() > 32) {
                return new ValidationInfo("Max size for url is 32 bytes", assetUrlTf);
            }
        } else {
            if(getAssetId() == null) {
                return new ValidationInfo("Please enter a valid asset id", assetIdCB);
            }
        }

        ValidationInfo createAddressVal = creatorAddressInputForm.doValidate();
        if (createAddressVal != null)
            return createAddressVal;

        if(AssetActionType.CREATE.equals(actionType) || AssetActionType.MODIFY.equals(actionType)) {
            ValidationInfo managerAddressVal = managerAddressInputForm.doValidate();
            if (managerAddressVal != null)
                return managerAddressVal;

            ValidationInfo reserveAddressVal = reserveAddressInputForm.doValidate();
            if (reserveAddressVal != null)
                return reserveAddressVal;

            ValidationInfo freezeAddressVal = freezeAddressInputForm.doValidate();
            if (freezeAddressVal != null)
                return freezeAddressVal;

            ValidationInfo clawbackAddressVal = clawbackAddressInputForm.doValidate();
            if (clawbackAddressVal != null)
                return clawbackAddressVal;

            ValidationInfo txnDtalVal = transactionDtlsEntryForm.doValidate();
            if (txnDtalVal != null)
                return txnDtalVal;
        }

        return null;
    }

    public Long getAssetId() {
        try {
            AssetMeta assetMeta = (AssetMeta)assetIdCB.getSelectedItem();
            if(assetMeta == null || assetMeta.getId() == null)
                return null;
            return Long.parseLong(StringUtil.trim(assetMeta.getId()));
        } catch (Exception e) {
            return null;
        }
    }

    public String getAssetName() {
        return StringUtil.trim(assetNameTf.getText());
    }

    public String getUnitName() {
        return StringUtil.trim(unitNameTf.getText());
    }

    public BigInteger getTotalSupply() {
        if(!NumberUtils.isNumber(totalSupplyTf.getText())) {
            return null;
        }

        return new BigInteger(StringUtil.trim(totalSupplyTf.getText()));
    }

    public int getDecimal() {
        return Integer.parseInt(decimalsTf.getText());
    }

    public String getUrl() {
        return StringUtil.trim(assetUrlTf.getText());
    }

    public boolean isDefaultFrozen() {
        return defaultFrozenCB.isSelected();
    }

    public byte[] getMetadataHashBytes() throws Exception {
        ArgType noteType = (ArgType) metadataHashType.getSelectedItem();
        if(noteType == null)
            return null;
        else {
            byte[] bytes = ArgTypeToByteConverter.convert(noteType, StringUtil.trim(metadataHashTf.getText()));
            return bytes;
        }
    }

    public TransactionDtlsEntryForm getTransactionDtlsEntryForm() {
        return transactionDtlsEntryForm;
    }

    public Account getCreatorAddress() {
        return creatorAddressInputForm.getAccount();
    }

    public Address getManagerAddress() {
        return managerAddressInputForm.getAddress();
    }

    public Address getReserveAddress() {
        return reserveAddressInputForm.getAddress();
    }

    public Address getFreezeAddress() {
        return freezeAddressInputForm.getAddress();
    }

    public Address getClawbackAddress() {
        return clawbackAddressInputForm.getAddress();
    }

    public AssetTxnParameters getAssetTxnParameters() throws Exception {
        AssetTxnParameters assetTxnParameters = new AssetTxnParameters();
        if(AssetActionType.CREATE.equals(actionType)) {
            assetTxnParameters.assetName = getAssetName();
            assetTxnParameters.unitName = getUnitName();
            assetTxnParameters.total = getTotalSupply();
            assetTxnParameters.decimal = getDecimal();
            assetTxnParameters.url = getUrl();
            assetTxnParameters.defaultFrozen = isDefaultFrozen();
            assetTxnParameters.metadataHash = getMetadataHashBytes();
        } else {
            assetTxnParameters.assetId = getAssetId();
        }

        if(AssetActionType.CREATE.equals(actionType) || AssetActionType.MODIFY.equals(actionType)) {
            assetTxnParameters.managerAddres = getManagerAddress();
            assetTxnParameters.reserveAddress = getReserveAddress();
            assetTxnParameters.freezeAddress = getFreezeAddress();
            assetTxnParameters.clawbackAddress = getClawbackAddress();
        }

        return assetTxnParameters;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        creatorAddressInputForm = new AccountEntryInputForm(true, false);
        creatorAddressInputForm.setAccountLabel("Creator Address ");

        managerAddressInputForm = new ManagedAccountEntryInputForm(false, false);
        managerAddressInputForm.setAccountLabel("Manager Address");

        reserveAddressInputForm = new ManagedAccountEntryInputForm(false, false);
        reserveAddressInputForm.setAccountLabel("Reserve Address");

        freezeAddressInputForm = new ManagedAccountEntryInputForm(false, false);
        freezeAddressInputForm.setAccountLabel("Freeze Address");

        clawbackAddressInputForm = new ManagedAccountEntryInputForm(false, false);
        clawbackAddressInputForm.setAccountLabel("Clawback Address");

        transactionDtlsEntryForm = new TransactionDtlsEntryForm();


        DefaultComboBoxModel<ArgType> metadataHashTypeComboBoxModel = new DefaultComboBoxModel(new ArgType[] {ArgType.String, ArgType.Base64});
        metadataHashType = new ComboBox(metadataHashTypeComboBoxModel);

        assetIdComboBoxModel = new DefaultComboBoxModel<>();
        assetIdCB = new ComboBox(assetIdComboBoxModel);
        assetIdCB.setEditable(true);
    }
}
