package com.bloxbean.algodea.idea.stateless.ui;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.account.service.AccountService;
import com.bloxbean.algodea.idea.common.Tuple;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.service.AlgoAccountService;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.util.AlgoConversionUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBRadioButton;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigInteger;
import java.util.function.Consumer;

public class LogicSigSigningAccountForm {
    private final static Logger LOG = Logger.getInstance(LogicSigSigningAccountForm.class);

    private JTextField authAddressTf;
    private JButton authAddrChooserBtn;
    private JPanel mainPanel;
    private JTextField authMnemonicTf;
    private JRadioButton contractAccountRadioButton;
    private JRadioButton accountDelegationRadioButton;
    private JTextField senderAddressTf;
    private JButton senderChooser;
    private JButton multiSigSenderChooser;

    private Project project;
    private ButtonGroup statelessContractTypeBtnGrp;
    private AlgoAccountService algoAccountService;
    private AlgoConsole console;
    private ChangeListener changeListener;

    protected LogicSigSigningAccountForm() {
        changeListener = new ChangeListener(){};
    }

    protected LogicSigSigningAccountForm(ChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public void initialize(Project project) throws DeploymentTargetNotConfigured {
        this.project = project;
        console = AlgoConsole.getConsole(project);
        try {
            algoAccountService = new AlgoAccountService(project, new LogListenerAdapter(console));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            throw deploymentTargetNotConfigured;
        }

        contractAccountRadioButton.addActionListener(e -> {
            if(contractAccountRadioButton.isSelected()) {
                enableDisableSignerAccountFields(false);
                changeListener.contractTypeSelected();
            }
        });

        accountDelegationRadioButton.addActionListener(e -> {
            if(accountDelegationRadioButton.isSelected()) {
                enableDisableSignerAccountFields(true);
                changeListener.delegationTypeSelect();
            }
        });

        contractAccountRadioButton.setSelected(true);
        enableDisableSignerAccountFields(false);

        authAddrChooserBtn.addActionListener(e -> {
            AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
            if(algoAccount != null) {
                authAddressTf.setText(algoAccount.getAddress());
                authMnemonicTf.setText(algoAccount.getMnemonic());
//                senderAddressTf.setText(algoAccount.getAddress());
                changeListener.signerAddressChanged(algoAccount.getAddress());
            }
        });

        authMnemonicTf.addFocusListener(new FocusListener() {
            String oldMnemonic;
            @Override
            public void focusGained(FocusEvent e) {
                oldMnemonic = authMnemonicTf.getText();
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(oldMnemonic != null && oldMnemonic.equals(authMnemonicTf.getText())) {
                    oldMnemonic = null;
                    return;
                }
                oldMnemonic = null; //reset old mnemonic

                String mnemonic = authMnemonicTf.getText();
                try {
                    Account account = new Account(mnemonic);
                    authAddressTf.setText(account.getAddress().toString());
                   // senderAddressTf.setText(account.getAddress().toString());
                    changeListener.signerAddressChanged(account.getAddress().toString());
                } catch (Exception ex) {
                    authAddressTf.setText("");
                    //senderAddressTf.setText("");
                }
            }
        });

        senderChooser.addActionListener(e -> {
            AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
            if(algoAccount != null) {
                senderAddressTf.setText(algoAccount.getAddress());
                authAddressTf.setText("");
                authMnemonicTf.setText("");
                fireBalanceUpdateEvent(null); //reset balance

                setAuthAddressAndBalance(project, algoAccount);
                changeListener.signerAddressChanged(algoAccount.getAddress());
            }
        });

        multiSigSenderChooser.addActionListener(e -> {
            AlgoMultisigAccount multisigAccount = AccountChooser.getSelectedMultisigAccount(project, true);
            if(multisigAccount != null) {
                senderAddressTf.setText(multisigAccount.getAddress());
                authAddressTf.setText("");
                authMnemonicTf.setText("");
                fireBalanceUpdateEvent(null); //reset balance

                setAuthAddressAndBalanceForMultiSigSender(project, multisigAccount);
                changeListener.signerAddressChanged(multisigAccount.getAddress());
            }
        });

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

                authAddressTf.setText("");
                authMnemonicTf.setText("");
                fireBalanceUpdateEvent(null);
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

    }

    private void enableDisableSignerAccountFields(boolean flag) {
        authAddressTf.setText("");
        authMnemonicTf.setText("");
        senderAddressTf.setText("");
        authAddressTf.setEnabled(flag);
        authAddrChooserBtn.setEnabled(flag);
        authMnemonicTf.setEnabled(flag);
        senderAddressTf.setEditable(flag);
        senderChooser.setEnabled(flag);
        multiSigSenderChooser.setEnabled(flag);
    }

    private void setAuthAddressAndBalance(Project project, AlgoAccount sender) {
        getAuthAddressAndBalance(project, sender.getAddress(), (tuple) -> {
            if(!StringUtil.isEmpty(tuple._1())) { //auth-addr found
                AccountService accountService = AccountService.getAccountService();
                AlgoAccount algoAccount = accountService.getAccountByAddress(tuple._1());
                if (algoAccount != null && !StringUtil.isEmpty(algoAccount.getMnemonic())) {
                    authAddressTf.setText(algoAccount.getAddress());
                    authMnemonicTf.setText(algoAccount.getMnemonic());
                } else {
                    //TODO alert
                    //IdeaUtil.authorizedAddressNotFoundWarning();
                    authAddressTf.setText(tuple._1());
                }
            } else { //No auth-addr
                if(!StringUtil.isEmpty(sender.getMnemonic())) {
                    authAddressTf.setText(sender.getAddress());
                    authMnemonicTf.setText(sender.getMnemonic());
                }
            }

            fireBalanceUpdateEvent(tuple._2());

            if(isContractAccountType() && !StringUtil.isEmpty(authAddressTf.getText())) {
                //auth addr set for this contract address. Enable fields, so that manual change is possible.
                authAddressTf.setEnabled(true);
                authMnemonicTf.setEnabled(true);
            }

        });
    }

    private void setAuthAddressAndBalanceForMultiSigSender(Project project, AlgoMultisigAccount sender) {
        getAuthAddressAndBalance(project, sender.getAddress(), (tuple) -> {
            if(!StringUtil.isEmpty(tuple._1())) { //auth address
                AccountService accountService = AccountService.getAccountService();
                AlgoAccount algoAccount = accountService.getAccountByAddress(tuple._1());
                if (algoAccount != null) {
                    authAddressTf.setText(algoAccount.getAddress());
                    authMnemonicTf.setText(algoAccount.getMnemonic());
                } else {
                    //TODO alert
                }
            }

            fireBalanceUpdateEvent(tuple._2());
        });
    }

    private void getAuthAddressAndBalance(Project project, String address, Consumer<Tuple<String, Long>> authAddressCheck) {
        if(algoAccountService == null || StringUtil.isEmpty(address))
            return;

        ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {

            @Override
            public void run() {
                try {
                    com.algorand.algosdk.v2.client.model.Account account = algoAccountService.getAccount(address);
                    String authAddr = account.authAddr();
                    Long balance = account.amount;
                    authAddressCheck.accept(new Tuple<String, Long>(authAddr, balance));
                } catch (Exception e) {
                    console.showErrorMessage("Unable to get auth-addr", e);
                    authAddressCheck.accept(new Tuple<String, Long>(null, null));
                } finally {

                }
            }
        }, "Fetching Authorized Address ...", true, project);
    }

    private void fireBalanceUpdateEvent(Long balance) {
        if(balance != null) {
            String formattedBal = AlgoConversionUtil.mAlgoToAlgoFormatted(BigInteger.valueOf(balance));
            changeListener.balanceUpdated(formattedBal);
        } else {
            changeListener.balanceUpdated(null);
        }
    }

    public @Nullable ValidationInfo doValidate() {
        //If auth account is set , auth mnemonic should be set
        if(!StringUtil.isEmpty(authAddressTf.getText())
                && StringUtil.isEmpty(authMnemonicTf.getText())) {
            return new ValidationInfo("Please provide a valid mnemonic for the Authorized Address.",
                    authMnemonicTf);
        }

        if(isAccountDelegationType()) {
            if (StringUtil.isEmpty(senderAddressTf.getText())) {
                senderAddressTf.setToolTipText("Please select a valid sender address");
                return new ValidationInfo("Please select a valid sender address", senderAddressTf);
            } else {
                senderAddressTf.setToolTipText("");
            }

            if (StringUtil.isEmpty(authAddressTf.getText())) {
                authAddressTf.setToolTipText("Please select a valid Authorized Address or enter valid Authorized Mnemonic");
                return new ValidationInfo("Please select a valid Authorized Address or enter valid Authorized Mnemonic", authAddressTf);
            } else {
                authAddressTf.setToolTipText("");
            }
        }

        return null;
    }

    public Account getAccount() {
        String mnemonic = authMnemonicTf.getText().trim();
        try {
            Account account = new Account(mnemonic);
            return account;
        } catch (Exception e) {
            return null;
        }
    }

    public Address getSenderAddress() {
        String address = senderAddressTf.getText().trim();
        try {
            Address sender = new Address(address);
            return sender;
        } catch (Exception e) {
            return null;
        }
    }

    public void setSenderAddress(String address) {
        senderAddressTf.setText(address);
        AlgoAccount algoAccount = new AlgoAccount(address);
        setAuthAddressAndBalance(project, algoAccount);
    }

    public boolean isContractAccountType() {
        return contractAccountRadioButton.isSelected();
    }

    public boolean isAccountDelegationType() {
        return accountDelegationRadioButton.isSelected();
    }

    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        statelessContractTypeBtnGrp = new ButtonGroup();
        contractAccountRadioButton = new JBRadioButton();
        accountDelegationRadioButton = new JBRadioButton();

        statelessContractTypeBtnGrp.add(contractAccountRadioButton);
        statelessContractTypeBtnGrp.add(accountDelegationRadioButton);
    }

    public interface ChangeListener {
        default public void signerAddressChanged(String address){};
        default public void contractTypeSelected(){};
        default public void delegationTypeSelect(){};
        default public void balanceUpdated(String balance){}
    }
}
