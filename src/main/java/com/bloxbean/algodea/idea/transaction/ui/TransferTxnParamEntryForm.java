package com.bloxbean.algodea.idea.transaction.ui;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.account.service.AccountService;
import com.bloxbean.algodea.idea.common.AlgoConstants;
import com.bloxbean.algodea.idea.common.Tuple;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.AccountAsset;
import com.bloxbean.algodea.idea.nodeint.service.AlgoAccountService;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.util.AlgoConversionUtil;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.function.Consumer;

import static com.bloxbean.algodea.idea.common.AlgoConstants.MICRO_ALGO;

public class TransferTxnParamEntryForm {
    private final static Logger LOG = Logger.getInstance(TransactionDtlsEntryForm.class);

    private JTextField authAccountTf;
    private JButton authAccChooserBtn;
    private JPanel mainPanel;
    private JTextField authAccMnemonicTf;
    private JTextField receiverAddressTf;
    private JButton recvAddressChooserBtn;
    private JTextField amountTf;
    private JButton recvAddressMultiSigBtn;
    private JRadioButton algoRadioButton;
    private JRadioButton otherAssetsRadioButton;
    private JComboBox assetIdCB;
    private JButton fetchButton;
    private JLabel unitLabel;
    private JTextField algoBalanceTf;
    private JTextField senderAddressTf;
    private JButton senderAddressChooser;
    private JButton senderAddressMultiSigChooser;
    private JLabel senderAddressLabel;
    private JLabel authorizedAddressLabel;
    private JLabel authorizedMnemonicLabel;
    private JLabel receiverAddressLabel;
    private JLabel transferTypeLabel;
    private JLabel availableBalanceAlgoLabel;
    private JLabel amountLabel;
    private JComboBox algoUnitCB;
    private JLabel selectAssetLabel;
    private DefaultComboBoxModel assetIdComboBoxModel;
    private AlgoAccountService algoAccountService;
    private AlgoConsole console;

    public TransferTxnParamEntryForm() {
        ButtonGroup assetTypeButtonGroup = new ButtonGroup();
        assetTypeButtonGroup.add(algoRadioButton);
        assetTypeButtonGroup.add(otherAssetsRadioButton);
        algoRadioButton.setSelected(true);
        unitLabel.setVisible(false);
        enableOtherAssetPanel(false);

        alignLabels();
    }

    //TODO
    private void alignLabels() {
//        senderAddressLabel.setText(StringUtility.padLeft("Sender Address", 25));
//        authorizedAddressLabel.setText(StringUtility.padLeft("Authorized Address", 25));
//        authorizedMnemonicLabel.setText(StringUtility.padLeft("Authorized Mnemonic", 25));
//        receiverAddressLabel.setText(StringUtility.padLeft("Receiver Address", 25));
//        transferTypeLabel.setText(StringUtility.padLeft("Type*", 25));
//        selectAssetLabel.setText(StringUtility.padLeft("Select ASA", 25));
//        availableBalanceAlgoLabel.setText(StringUtility.padLeft("Available Balance (Algo)", 25));
//        amountLabel.setText(StringUtility.padLeft("Amount", 25));

    }

    public void initializeData(Project project) throws DeploymentTargetNotConfigured {
        console = AlgoConsole.getConsole(project);
        try {
            algoAccountService = new AlgoAccountService(project, new LogListenerAdapter(console));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            throw deploymentTargetNotConfigured;
        }

        authAccChooserBtn.addActionListener(e -> {
                AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
                if(algoAccount != null) {
                    authAccountTf.setText(algoAccount.getAddress());
                    authAccMnemonicTf.setText(algoAccount.getMnemonic());
                }
        });

        authAccMnemonicTf.addFocusListener(new FocusListener() {
            String oldMnemonic;
            @Override
            public void focusGained(FocusEvent e) {
                oldMnemonic = authAccMnemonicTf.getText();
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(oldMnemonic != null && oldMnemonic.equals(authAccMnemonicTf.getText())) {
                    oldMnemonic = null;
                    return;
                }
                oldMnemonic = null; //reset old mnemonic

                String mnemonic = authAccMnemonicTf.getText();
                try {
                    Account account = new Account(mnemonic);
                    authAccountTf.setText(account.getAddress().toString());
                } catch (Exception ex) {
                    authAccountTf.setText("");
                    clearAssetRelatedData();
                }
            }
        });

        //TODO
        senderAddressTf.addFocusListener(new FocusAdapter() {
            String oldSender;
            @Override
            public void focusGained(FocusEvent e) {
                oldSender = senderAddressTf.getText();
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(oldSender != null && oldSender.equals(senderAddressTf.getText())) {
                    oldSender = null;
                    return;
                }
                oldSender = null;

                clearAssetRelatedData();
                authAccountTf.setText("");
                authAccMnemonicTf.setText("");
                try {
                    Address address = new Address(senderAddressTf.getText());
                    AlgoAccount algoAccount = AccountService.getAccountService().getAccountByAddress(address.toString());
                    if(algoAccount == null) {
                        algoAccount = new AlgoAccount(address.toString());
                    }

                    setAuthAddressAndBalance(project, algoAccount);
                } catch (Exception ex) {

                }
            }
        });

        senderAddressChooser.addActionListener(e -> {
            AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
            if(algoAccount != null) {
                senderAddressTf.setText(algoAccount.getAddress());
                authAccountTf.setText("");
                authAccMnemonicTf.setText("");

                clearAssetRelatedData();
                setAuthAddressAndBalance(project, algoAccount);
            }
        });

        senderAddressMultiSigChooser.addActionListener(e -> {
            AlgoMultisigAccount algoMultisigAccount = AccountChooser.getSelectedMultisigAccount(project, true);
            if(algoMultisigAccount != null) {
                senderAddressTf.setText(algoMultisigAccount.getAddress());
                authAccountTf.setText("");
                authAccMnemonicTf.setText("");

                clearAssetRelatedData();
                setAuthAddressAndBalanceForMultiSigSender(project, algoMultisigAccount);
            }
        });

        recvAddressChooserBtn.addActionListener(e -> {
            AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
            if(algoAccount != null) {
                receiverAddressTf.setText(algoAccount.getAddress());
            }
        });

        recvAddressMultiSigBtn.addActionListener(e -> {
            AlgoMultisigAccount algoMultiSigAccount = AccountChooser.getSelectedMultisigAccount(project, true);
            if(algoMultiSigAccount != null) {
                receiverAddressTf.setText(algoMultiSigAccount.getAddress());
            }
        });

        algoRadioButton.addActionListener(e -> {
            if(algoRadioButton.isSelected()) {
                enableOtherAssetPanel(false);
               setUnitLabel();

               algoUnitCB.setVisible(true);
               unitLabel.setVisible(false);

                try {
                    Address fromAccount = new Address(StringUtil.trim(authAccountTf.getText()));
                    //getAvailableAlgoBalance(project, fromAccount.toString(), (l) -> algoBalanceLabel.setText(String.valueOf(l)));
                } catch(Exception ex) {
                    console.showErrorMessage("Unable fetch balance", ex);
                }
            }
        });

        otherAssetsRadioButton.addActionListener(e -> {
            if(otherAssetsRadioButton.isSelected()) {
                enableOtherAssetPanel(true);
                setUnitLabel();

                algoUnitCB.setVisible(false);
                unitLabel.setVisible(true);

            }
        });

        fetchButton.addActionListener(e -> {
            fetchAssetFortheAccount(project);
        });

        assetIdCB.addActionListener(e -> {
            setUnitLabel();
        });
    }

    private void setUnitLabel() {
        if(isAlgoTransfer()) {
            unitLabel.setText("");
        } else {
            AccountAsset accountAsset = (AccountAsset) assetIdCB.getSelectedItem();
            if (accountAsset == null)
                return;

            if (!StringUtil.isEmpty(accountAsset.getAssetUnit()))
                unitLabel.setText(accountAsset.getAssetUnit());
        }
    }

    private void setAuthAddressAndBalance(Project project, AlgoAccount sender) {
        getAuthAddress(project, sender.getAddress(), (accountAuthAddr) -> {
            if(!StringUtil.isEmpty(accountAuthAddr)) { //auth-addr found
                AccountService accountService = AccountService.getAccountService();
                AlgoAccount algoAccount = accountService.getAccountByAddress(accountAuthAddr);
                if (algoAccount != null && !StringUtil.isEmpty(algoAccount.getMnemonic())) {
                    authAccountTf.setText(algoAccount.getAddress());
                    authAccMnemonicTf.setText(algoAccount.getMnemonic());
                } else {
                    //TODO alert
                    authAccountTf.setText(accountAuthAddr);
                    IdeaUtil.authorizedAddressNotFoundWarning();
                }
            } else { //No auth-addr
                if(!StringUtil.isEmpty(sender.getMnemonic())) {
                    authAccountTf.setText(sender.getAddress());
                    authAccMnemonicTf.setText(sender.getMnemonic());
                }
            }

            setAlgoBalanceForFromAccountSync(project, sender.getAddress());

        });
    }

    private void setAuthAddressAndBalanceForMultiSigSender(Project project, AlgoMultisigAccount sender) {
        getAuthAddress(project, sender.getAddress(), (accountAuthAddr) -> {
            if(!StringUtil.isEmpty(accountAuthAddr)) {
                AccountService accountService = AccountService.getAccountService();
                AlgoAccount algoAccount = accountService.getAccountByAddress(accountAuthAddr);
                if (algoAccount != null) {
                    authAccountTf.setText(algoAccount.getAddress());
                    authAccMnemonicTf.setText(algoAccount.getMnemonic());
                } else {
                    //TODO alert
                }
            }

            setAlgoBalanceForFromAccountSync(project, sender.getAddress());

        });
    }

    private void setAlgoBalanceForFromAccountSync(Project project, String fromAddress) {
       Long balance = getAvailableAlgoBalanceSync(fromAddress);
       if(balance != null) {
           String formattedBal = AlgoConversionUtil.mAlgoToAlgoFormatted(BigInteger.valueOf(balance));
           algoBalanceTf.setText(formattedBal);
       }

    }

    private Long getAvailableAlgoBalanceSync(String account) {
        if (algoAccountService == null || StringUtil.isEmpty(account))
            return null;

        try {
            Long balance = algoAccountService.getBalance(account);
            return balance;
        } catch (Exception e) {
            if (LOG.isDebugEnabled())
                LOG.warn(e);
            console.showErrorMessage("Unable to fetch balance", e);
        } finally {

        }

        return null;
    }

    private void getAvailableAlgoBalanceAsync(Project project, String account, Consumer<Long> balanceCallback) {
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

    private void getAuthAddress(Project project, String address, Consumer<String> authAddressCheck) {
        if(algoAccountService == null || StringUtil.isEmpty(address))
            return;

        ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {

            @Override
            public void run() {
                try {
                    com.algorand.algosdk.v2.client.model.Account account = algoAccountService.getAccount(address);
                    authAddressCheck.accept(account.authAddr());
                } catch (Exception e) {
                    if(LOG.isDebugEnabled())
                        LOG.warn(e);
                } finally {

                }
            }
        }, "Fetching Authorized Address ...", true, project);
    }

    private void enableOtherAssetPanel(boolean flag) {
        assetIdCB.setEnabled(flag);
        fetchButton.setEnabled(flag);
    }

    private void clearAssetRelatedData() {
        unitLabel.setText("");
        algoBalanceTf.setText("");
        assetIdComboBoxModel.removeAllElements();
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

        if(StringUtil.isEmpty(authAccountTf.getText())) {
            return new ValidationInfo("Please select a valid Authorized Account or enter valid Authorized Mnemonic", authAccountTf);
        }

        if(!StringUtil.isEmpty(authAccountTf.getText())
                && StringUtil.isEmpty(authAccMnemonicTf.getText())) {
            return new ValidationInfo("Please provide a valid mnemonic for the Authorized Address.", authAccMnemonicTf);
        }

        if(!isAlgoTransfer()) {
            if(getAsset() == null) {
                return new ValidationInfo("Please select an asset", assetIdCB);
            }
        }

        if(getFromAddress() == null) {
            return new ValidationInfo("Please enter a valid sender address", senderAddressTf);
        }

        if(getToAccount() == null) {
            return new ValidationInfo("Please select a valid receiver address", receiverAddressTf);
        }

        try {
            Double.parseDouble(amountTf.getText());
        } catch (Exception e) {
            return new ValidationInfo("Invalid amount", amountTf);
        }

        return null;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public Account getSigningAccount() {
        String mnemonic = authAccMnemonicTf.getText().trim();
        try {
            Account account = new Account(mnemonic);
            return account;
        } catch (Exception e) {
            return null;
        }
    }

    //Only use for read-only account where mnemonic is empty
    public Address getFromAddress() {
        String fromAddress = senderAddressTf.getText().trim();
        try {
            return new Address(fromAddress);
        } catch (Exception e) {
            return null;
        }
    }

    public Address getToAccount() {
        String acc = receiverAddressTf.getText().trim();
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
                    if(algoUnitCB.getSelectedItem() != null
                            && algoUnitCB.getSelectedItem().equals(MICRO_ALGO)) {
                        BigInteger amtInmAlgo = new BigInteger(amountTf.getText());
                        BigDecimal amtInAlgo = AlgoConversionUtil.mAlgoToAlgo(amtInmAlgo);

                        return new Tuple(amtInAlgo, amtInmAlgo);
                    } else {
                        BigDecimal amtInAlgo = new BigDecimal(amountTf.getText());
                        BigInteger microAlgo = AlgoConversionUtil.algoTomAlgo(amtInAlgo);

                        return new Tuple(amtInAlgo, microAlgo);
                    }
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

        DefaultComboBoxModel<String> algoUnitCBM
                = new DefaultComboBoxModel<>(new String[] {AlgoConstants.ALGO, AlgoConstants.MICRO_ALGO});
        algoUnitCB = new ComboBox(algoUnitCBM);
        algoUnitCB.setSelectedIndex(0);
    }
}
