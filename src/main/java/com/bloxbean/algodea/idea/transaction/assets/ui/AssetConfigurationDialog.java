package com.bloxbean.algodea.idea.transaction.assets.ui;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.nodeint.model.ArgType;
import com.bloxbean.algodea.idea.nodeint.model.AssetTxnParameters;
import com.bloxbean.algodea.idea.nodeint.util.ArgTypeToByteConverter;
import com.bloxbean.algodea.idea.transaction.ui.AccountEntryInputForm;
import com.bloxbean.algodea.idea.transaction.ui.ManagedAccountEntryInputForm;
import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.math.BigInteger;

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

    public AssetConfigurationDialog(@Nullable Project project) {
        super(project, true);
        init();
        setTitle("Asset Configuration");

        initializeData(project);
    }

    private void initializeData(Project project) {
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
        //Assets form validation
        try {
            if(getTotalSupply() == null) {
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
            if(!StringUtil.isEmpty(metadataHashTf.getText())) {
                byte[] bytes = getMetadataHashBytes();

                if (bytes != null && (bytes.length != 0 && bytes.length != 32)) {
                    return new ValidationInfo("Metadata hash should be 0 or 32 bytes", metadataHashTf);
                }
            }
        } catch (Exception e) {

        }

        //validate URL length 32 bytes
        if(!StringUtil.isEmpty(getUrl()) && getUrl().length() > 32) {
            return new ValidationInfo("Max size for url is 32 bytes", assetUrlTf);
        }


        ValidationInfo createAddressVal = creatorAddressInputForm.doValidate();
        if(createAddressVal != null)
            return createAddressVal;

        ValidationInfo managerAddressVal = managerAddressInputForm.doValidate();
        if(managerAddressVal != null)
            return managerAddressVal;

        ValidationInfo reserveAddressVal = reserveAddressInputForm.doValidate();
        if(reserveAddressVal != null)
            return reserveAddressVal;

        ValidationInfo freezeAddressVal = freezeAddressInputForm.doValidate();
        if(freezeAddressVal != null)
            return freezeAddressVal;

        ValidationInfo clawbackAddressVal = clawbackAddressInputForm.doValidate();
        if(clawbackAddressVal != null)
            return clawbackAddressVal;

        ValidationInfo txnDtalVal = transactionDtlsEntryForm.doValidate();
        if(txnDtalVal != null)
            return txnDtalVal;

        return null;
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
        assetTxnParameters.assetName = getAssetName();
        assetTxnParameters.unitName = getUnitName();
        assetTxnParameters.total = getTotalSupply();
        assetTxnParameters.decimal = getDecimal();
        assetTxnParameters.url = getUrl();
        assetTxnParameters.defaultFrozen = isDefaultFrozen();
        assetTxnParameters.metadataHash = getMetadataHashBytes();

        assetTxnParameters.managerAddres = getManagerAddress();
        assetTxnParameters.reserveAddress = getReserveAddress();
        assetTxnParameters.freezeAddress = getFreezeAddress();
        assetTxnParameters.clawbackAddress = getClawbackAddress();

        return assetTxnParameters;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        creatorAddressInputForm = new AccountEntryInputForm(true, true);
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
    }
}
