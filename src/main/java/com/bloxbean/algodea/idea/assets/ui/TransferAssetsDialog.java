package com.bloxbean.algodea.idea.assets.ui;

import com.bloxbean.algodea.idea.assets.model.AssetMeta;
import com.bloxbean.algodea.idea.transaction.ui.AccountEntryInputForm;
import com.bloxbean.algodea.idea.transaction.ui.ManagedAccountEntryInputForm;
import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
import com.bloxbean.algodea.idea.util.StringUtility;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TransferAssetsDialog extends DialogWrapper  {
    private JTextField amountTf;
    private JLabel assetIdLabel;
    private AssetsChooserDialog assetChooserDialog;
    private AccountEntryInputForm senderAccountEntryInputForm;
    private ManagedAccountEntryInputForm receiverAccountPanel;
    private JLabel amountLabel;
    private JTabbedPane tabbedPane1;
    private TransactionDtlsEntryForm transactionDtlsEntryForm;
    private JPanel mainPanel;
    private JLabel unitNameLabel;

    public TransferAssetsDialog(@Nullable Project project) {
        super(project, true);
        init();
        setTitle("Asset Transfer");

        amountLabel.setText(StringUtility.padLeft("Amount", 26)); //TODO Not sure why it's not align with 30. check later
        setOKButtonText("Transfer");
        initialize(project);
    }

    public void initialize(Project project) {

        senderAccountEntryInputForm.initializeData(project);
        receiverAccountPanel.initializeData(project);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        assetIdLabel = new JLabel();
        assetIdLabel.setText(StringUtility.padLeft("AssetId", 30));

        assetChooserDialog = new AssetsChooserDialog() {
            @Override
            protected void onSuccessfulSearch(String assetId, AssetMeta asset) {

            }
        };

        assetChooserDialog.setAssetIdLable(StringUtility.padLeft("Asset Idd", 30));

        senderAccountEntryInputForm = new AccountEntryInputForm(true, false);
        senderAccountEntryInputForm.setSigningAccountLabel(StringUtility.padLeft("Sender Address", 30));

        receiverAccountPanel = new ManagedAccountEntryInputForm(true, true);
        receiverAccountPanel.setAccountLabel(StringUtility.padLeft("Receiver Address", 30));

        amountLabel = new JBLabel();
        unitNameLabel = new JBLabel();

        //transaction details tab
        transactionDtlsEntryForm = new TransactionDtlsEntryForm();

    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }
}
