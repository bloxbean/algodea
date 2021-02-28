package com.bloxbean.algodea.idea.transaction.ui;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.common.Tuple;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.AccountAsset;
import com.bloxbean.algodea.idea.nodeint.service.AlgoAccountService;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.util.AlgoConversionUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.function.Consumer;

import static com.bloxbean.algodea.idea.common.AlgoConstants.ALGO;

public class TransferTxnParamEntryForm {
    private final static Logger LOG = Logger.getInstance(TransactionDtlsEntryForm.class);

    private JTextField fromAccountTf;
    private JButton fromAccChooserBtn;
    private JPanel mainPanel;
    private JTextField fromAccMnemonicTf;
    private JTextField toAccountTf;
    private JButton toAccChooserBtn;
    private JTextField amountTf;
    private JButton multiSigBtn;
    private JRadioButton algoRadioButton;
    private JRadioButton otherAssetsRadioButton;
    private JComboBox assetIdCB;
    private JButton fetchButton;
    private JLabel unitLabel;
    private JTextField algoBalanceTf;
    private JTextField closeReminderTo;
    private JButton closeReminderAccountChooserBtn;
    private DefaultComboBoxModel assetIdComboBoxModel;
    private AlgoAccountService algoAccountService;
    private AlgoConsole console;

    public TransferTxnParamEntryForm() {
        ButtonGroup assetTypeButtonGroup = new ButtonGroup();
        assetTypeButtonGroup.add(algoRadioButton);
        assetTypeButtonGroup.add(otherAssetsRadioButton);
        algoRadioButton.setSelected(true);
        enableOtherAssetPanel(false);
    }

    public void initializeData(Project project) throws DeploymentTargetNotConfigured {
        console = AlgoConsole.getConsole(project);
        try {
            algoAccountService = new AlgoAccountService(project, new LogListenerAdapter(console));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            throw deploymentTargetNotConfigured;
        }

        fromAccChooserBtn.addActionListener(e -> {
                AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
                if(algoAccount != null) {
                    fromAccountTf.setText(algoAccount.getAddress());
                    fromAccMnemonicTf.setText(algoAccount.getMnemonic());

                    clearAssetRelatedData();
                    //Set Algo balance
                    setAlgoBalanceForFromAccount(project, algoAccount.getAddress().toString());
                }
        });

        fromAccMnemonicTf.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                String mnemonic = fromAccMnemonicTf.getText();
                try {
                    Account account = new Account(mnemonic);
                    String oldFromAcc = fromAccountTf.getText();

                    fromAccountTf.setText(account.getAddress().toString());

                    if(oldFromAcc != null && !oldFromAcc.equals(fromAccountTf.getText())) {
                        clearAssetRelatedData();

                        setAlgoBalanceForFromAccount(project, account.getAddress().toString());
                    }

                } catch (Exception ex) {
                    fromAccountTf.setText("");
                    clearAssetRelatedData();
                }
            }
        });


        toAccChooserBtn.addActionListener(e -> {
            AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
            if(algoAccount != null) {
                toAccountTf.setText(algoAccount.getAddress());
            }
        });

        multiSigBtn.addActionListener(e -> {
            AlgoMultisigAccount algoMultiSigAccount = AccountChooser.getSelectedMultisigAccount(project, true);
            if(algoMultiSigAccount != null) {
                toAccountTf.setText(algoMultiSigAccount.getAddress());
            }
        });

        algoRadioButton.addActionListener(e -> {
            if(algoRadioButton.isSelected()) {
                enableOtherAssetPanel(false);
               setUnitLabel();

                try {
                    Address fromAccount = new Address(StringUtil.trim(fromAccountTf.getText()));
                    //getAvailableAlgoBalance(project, fromAccount.toString(), (l) -> algoBalanceLabel.setText(String.valueOf(l)));
                } catch(Exception ex) {
                    console.showErrorMessage("Unable fetch balance", ex);
                }

                //enable close reminder
                enableCloseReminderSection(true);
            }
        });

        otherAssetsRadioButton.addActionListener(e -> {
            if(otherAssetsRadioButton.isSelected()) {
                enableOtherAssetPanel(true);
                setUnitLabel();

                //disable close remninder to
                enableCloseReminderSection(false);
            }
        });

        fetchButton.addActionListener(e -> {
            fetchAssetFortheAccount(project);
        });

        assetIdCB.addActionListener(e -> {
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

    private void setUnitLabel() {
        if(isAlgoTransfer()) {
            unitLabel.setText(ALGO);
        } else {
            AccountAsset accountAsset = (AccountAsset) assetIdCB.getSelectedItem();
            if (accountAsset == null)
                return;

            if (!StringUtil.isEmpty(accountAsset.getAssetUnit()))
                unitLabel.setText(accountAsset.getAssetUnit());
        }
    }

    private void setAlgoBalanceForFromAccount(Project project, String fromAddress) {
        getAvailableAlgoBalance(project, fromAddress, (bal) -> {
            String formattedBal = AlgoConversionUtil.mAlgoToAlgoFormatted(BigInteger.valueOf(bal));
            algoBalanceTf.setText(formattedBal);
        });
    }

    private void getAvailableAlgoBalance(Project project, String account, Consumer<Long> balanceCallback) {
        if(algoAccountService == null || StringUtil.isEmpty(account))
            return;

        ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {

            @Override
            public void run() {
                try {
                    Long balance = algoAccountService.getBalance(account);
                    balanceCallback.accept(balance);
                } catch (Exception e) {
                   if(LOG.isDebugEnabled())
                       LOG.warn(e);
                } finally {

                }
            }
        }, "Fetching account balance ...", true, project);
    }

    private void enableOtherAssetPanel(boolean flag) {
        assetIdCB.setEnabled(flag);
        fetchButton.setEnabled(flag);
    }

    private void clearAssetRelatedData() {
        if(algoRadioButton.isSelected()) //Keep default unit as Algo
            unitLabel.setText(ALGO);
        else
            unitLabel.setText("");
        algoBalanceTf.setText("");
        assetIdComboBoxModel.removeAllElements();
    }

    private void enableCloseReminderSection(boolean flag) {
        closeReminderTo.setText("");
        closeReminderTo.setEnabled(flag);
        closeReminderAccountChooserBtn.setEnabled(flag);
    }

    private void fetchAssetFortheAccount(Project project) {
        ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
            @Override
            public void run() {
                ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
                progressIndicator.setIndeterminate(false);

                try {
                    assetIdComboBoxModel.removeAllElements();

                    Address fromAddress = getFromAddress();
                    if(fromAddress == null) {
                        console.showErrorMessage("Invalid From Account");
                        return;
                    }

                    List<AccountAsset> accountAssets = algoAccountService.getAccountAssets(fromAddress.toString());
                    if (accountAssets == null || accountAssets.size() == 0)
                        return;

                    for (AccountAsset accountAsset : accountAssets) {
                        assetIdCB.addItem(accountAsset);
                    }

                    if (assetIdCB.getModel().getSize() > 0) {
                        assetIdCB.setSelectedIndex(0);
                    }
                } catch (Exception e) {
                    console.showErrorMessage("Error getting asset information for the account", e);
                } finally {
                    progressIndicator.setFraction(1.0);
                }

            }
        }, "Fetching account assets from Algorand node ...", true, project);
    }

    public @Nullable ValidationInfo doValidate() {

        if(StringUtil.isEmpty(fromAccountTf.getText())) {
            return new ValidationInfo("Please select a valid from account or enter valid mnemonic", fromAccountTf);
        }

        if(!isAlgoTransfer()) {
            if(getAsset() == null) {
                return new ValidationInfo("Please select an asset", assetIdCB);
            }
        }

        if(getToAccount() == null) {
            return new ValidationInfo("Please select a valid to account", toAccountTf);
        }

        try {
            Double.parseDouble(amountTf.getText());
        } catch (Exception e) {
            return new ValidationInfo("Invalid amount", amountTf);
        }

        try {
            getCloseReminderTo();
        } catch (Exception e) {
            return new ValidationInfo("Invalid Close Reminder To address", closeReminderTo);
        }

        return null;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public Account getFromAccount() {
        String mnemonic = fromAccMnemonicTf.getText().trim();
        try {
            Account account = new Account(mnemonic);
            return account;
        } catch (Exception e) {
            return null;
        }
    }

    //Only use for read-only account where mnemonic is empty
    public Address getFromAddress() {
        Account fromAccount = getFromAccount();
        if(fromAccount != null) {
            return fromAccount.getAddress();
        } else {
            String fromAddress = fromAccountTf.getText().trim();
            try {
                return new Address(fromAddress);
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
        }
    }

    public Address getToAccount() {
        String acc = toAccountTf.getText().trim();
        try {
            Address address = new Address(acc);
            return address;
        } catch (Exception e) {
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

    public boolean isAlgoTransfer() {
        return algoRadioButton.isSelected();
    }

    public AccountAsset getAsset() {
        if(isAlgoTransfer())
            return null;
        AccountAsset accountAsset = (AccountAsset)assetIdCB.getSelectedItem();
        return accountAsset;
    }

    public Long getAssetId() {
        if(isAlgoTransfer())
            return null;

        AccountAsset accountAsset = (AccountAsset)assetIdCB.getSelectedItem();
        if(accountAsset == null)
            return null;
        else
            return accountAsset.getAssetId();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        assetIdComboBoxModel = new DefaultComboBoxModel();
        assetIdCB = new ComboBox(assetIdComboBoxModel);
    }
}
