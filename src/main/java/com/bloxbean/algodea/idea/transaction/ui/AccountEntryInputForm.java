package com.bloxbean.algodea.idea.transaction.ui;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.account.service.AccountService;
import com.bloxbean.algodea.idea.contracts.ui.CreateAppEntryForm;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.service.AlgoAccountService;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.bloxbean.algodea.idea.util.StringUtility;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.function.Consumer;

public class AccountEntryInputForm {
    private final static Logger LOG = Logger.getInstance(CreateAppEntryForm.class);

    private JPanel mainPanel;
    private JTextField authAddressTf;
    private JButton authAccountChooserBtn;
    private JButton authMultisigSignerAccChooserBtn;
    private JTextField authMnemonicTf;
    private JLabel accountLabel;
    private JLabel menmonicLabel;
    private JLabel orLabel;
    private JTextField senderAddressTf;
    private JButton senderAddressChooser;
    private JButton senderAddressMultiSigChooser;
    private JLabel senderAddressLabel;

    private boolean enableMnemonic = true;
    private boolean enableMultiSig = true;

    private boolean isMandatory;
    private AlgoConsole console;
    private AlgoAccountService algoAccountService;

    public AccountEntryInputForm() {

    }

    public AccountEntryInputForm(boolean mandatory, boolean enableMultiSig) {
        this.isMandatory = mandatory;
        this.enableMultiSig = enableMultiSig;
    }

    public void initializeData(Project project) throws DeploymentTargetNotConfigured {
        console = AlgoConsole.getConsole(project);
        try {
            algoAccountService = new AlgoAccountService(project, new LogListenerAdapter(console));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            throw deploymentTargetNotConfigured;
        }

        if(!enableMnemonic) {
            menmonicLabel.setVisible(false);
            authMnemonicTf.setVisible(false);
            orLabel.setVisible(false);
        }

        if(!enableMultiSig) {
            authMultisigSignerAccChooserBtn.setEnabled(false);
            authMultisigSignerAccChooserBtn.setVisible(false);
        }

        attachedListeners(project);
    }

    private void attachedListeners(Project project) {
        authAccountChooserBtn.addActionListener(e -> {
            AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
            if (algoAccount != null) {
                authAddressTf.setText(algoAccount.getAddress());
                authMnemonicTf.setText(algoAccount.getMnemonic());
               // senderAddressTf.setText(algoAccount.getAddress());
            }
        });

        if(enableMultiSig) {
            authMultisigSignerAccChooserBtn.addActionListener(e -> {
                AlgoMultisigAccount algoMultisigAccount = AccountChooser.getSelectedMultisigAccount(project, true);
                if (algoMultisigAccount != null) {
                    authAddressTf.setText(algoMultisigAccount.getAddress());
                   // senderAddressTf.setText(algoMultisigAccount.getAddress());
                }
            });
        }

        if(enableMnemonic) {
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
                        //senderAddressTf.setText(account.getAddress().toString());
                    } catch (Exception ex) {
                        authAddressTf.setText("");
                        //senderAddressTf.setText("");
                    }
                }
            });
        }

        senderAddressChooser.addActionListener(e -> {
            AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
            if (algoAccount != null) {
                senderAddressTf.setText(algoAccount.getAddress());
                authAddressTf.setText("");
                authMnemonicTf.setText("");

                setAuthAddress(project, algoAccount);
            }
        });

        senderAddressMultiSigChooser.addActionListener(e -> {
            AlgoMultisigAccount algoMultisigAccount = AccountChooser.getSelectedMultisigAccount(project, true);
            if (algoMultisigAccount != null) {
                senderAddressTf.setText(algoMultisigAccount.getAddress());

                authAddressTf.setText("");
                authMnemonicTf.setText("");

                setAuthAddressForMultiSigSender(project, algoMultisigAccount);
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
                try {
                    Address address = new Address(senderAddressTf.getText());
                    AlgoAccount algoAccount = AccountService.getAccountService().getAccountByAddress(address.toString());
                    if(algoAccount == null) {
                        algoAccount = new AlgoAccount(address.toString());
                    }

                    setAuthAddress(project, algoAccount);
                } catch (Exception ex) {
                    console.showErrorMessage("Error getting auth addr", ex);
                }
            }
        });
    }

    /***** Get Auth Address code start -- //TODO move to a common class later ****/

    private void setAuthAddress(Project project, AlgoAccount sender) {
        getAuthAddress(project, sender.getAddress(), (accountAuthAddr) -> {
            if(!StringUtil.isEmpty(accountAuthAddr)) { //auth-addr found
                AccountService accountService = AccountService.getAccountService();
                AlgoAccount algoAccount = accountService.getAccountByAddress(accountAuthAddr);
                if (algoAccount != null && !StringUtil.isEmpty(algoAccount.getMnemonic())) {
                    authAddressTf.setText(algoAccount.getAddress());
                    authMnemonicTf.setText(algoAccount.getMnemonic());
                } else {
                    //TODO alert
                    authAddressTf.setText(accountAuthAddr);
                    IdeaUtil.authorizedAddressNotFoundWarning();
                }
            } else { //No auth-addr
                if(!StringUtil.isEmpty(sender.getMnemonic())) {
                    authAddressTf.setText(sender.getAddress());
                    authMnemonicTf.setText(sender.getMnemonic());
                }
            }
        });
    }

    private void setAuthAddressForMultiSigSender(Project project, AlgoMultisigAccount sender) {
        getAuthAddress(project, sender.getAddress(), (accountAuthAddr) -> {
            if(!StringUtil.isEmpty(accountAuthAddr)) {
                AccountService accountService = AccountService.getAccountService();
                AlgoAccount algoAccount = accountService.getAccountByAddress(accountAuthAddr);
                if (algoAccount != null) {
                    authAddressTf.setText(algoAccount.getAddress());
                    authMnemonicTf.setText(algoAccount.getMnemonic());
                } else {
                    //TODO alert
                }
            }

        });
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

    /**** Get Auth Address ends here ****/

    public @Nullable ValidationInfo doValidate() {

        if(isMandatory) {
            if (StringUtil.isEmpty(senderAddressTf.getText())) {
                senderAddressTf.setToolTipText("Please select a valid sender address");
                return new ValidationInfo("Please select a valid sender address", senderAddressTf);
            } else {
                senderAddressTf.setToolTipText("");
            }

            if (StringUtil.isEmpty(authAddressTf.getText())) {
                authAddressTf.setToolTipText("Please select a valid Authorized Account or enter valid mnemonic");
                return new ValidationInfo("Please select a valid Authorized Account or enter valid mnemonic", authAddressTf);
            } else {
                authAddressTf.setToolTipText("");
            }

            //If auth account is set , auth mnemonic should be set
            if(!StringUtil.isEmpty(authAddressTf.getText())
                    && StringUtil.isEmpty(authMnemonicTf.getText())) {
                return new ValidationInfo("Please provide a valid mnemonic for the Authorized Address.",
                        authMnemonicTf);
            }
        }

        return null;
    }

    public void setSigningAccountLabel(String label) {
        accountLabel.setToolTipText(label);
        accountLabel.setText(StringUtility.padLeft(label, 20));
    }

    public void setMenmonicLabel(String label) {
        menmonicLabel.setText(StringUtility.padLeft(label, 20));
    }

    public void setSenderAddressLabel(String label) {
        senderAddressLabel.setText(StringUtility.padLeft(label, 20));
    }

    public void setEnableMnemonic(boolean flag) {
        enableMnemonic = flag;
    }

    public void setEnableMultiSig(boolean flag) {
        enableMultiSig = flag;
    }

    public void disableSenderAddressFields() {
        senderAddressTf.setEnabled(false);
        senderAddressChooser.setEnabled(false);
        senderAddressMultiSigChooser.setEnabled(false);
    }

    public Account getSignerAccount() {
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
            return new Address(address);
        } catch (Exception e) {
            return null;
        }
    }

    public void setMnemonic(String mnemonic) {
        if(StringUtil.isEmpty(mnemonic))
            return;

        authMnemonicTf.setText(mnemonic);
        try {
            Account account = new Account(mnemonic);
            if(account != null) {
                authAddressTf.setText(account.getAddress().toString());
                senderAddressTf.setText(account.getAddress().toString());
            }
        } catch (Exception e) {

        }
    }

    public void setSenderAddress(Project project, AlgoAccount acc) {
        if(acc == null)
            return;
        senderAddressTf.setText(acc.getAddress().toString());
        setAuthAddress(project, acc);
    }

    public void clearFields() {
        senderAddressTf.setText("");
        authAddressTf.setText("");
        authMnemonicTf.setText("");
    }
}
