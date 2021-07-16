/*
 * Copyright (c) 2020 BloxBean Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bloxbean.algodea.idea.contracts.ui;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.account.service.AccountService;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.service.AlgoAccountService;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.pkg.AlgoPkgJsonService;
import com.bloxbean.algodea.idea.pkg.exception.PackageJsonException;
import com.bloxbean.algodea.idea.pkg.model.AlgoPackageJson;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class CreateAppEntryForm {
    private final static Logger LOG = Logger.getInstance(CreateAppEntryForm.class);

    private JPanel mainPanel;
    private JTextField authAccountTf;
    private JTextField approvalProgramTf;
    private JTextField clearStateProgramTf;
    private JTextField globalByteslicesTf;
    private JTextField globalIntTf;
    private JTextField localByteslicesTf;
    private JTextField localIntsTf;
    private JButton authAccountChooser;
    private JTextField authMnemonicTf;
    private JComboBox contractCB;
    private JTextField senderAddressTf;
    private JButton senderChooser;
    private JButton senderMultiSigChooser;
    private JTextField extraPagesTf;
    private List<AlgoPackageJson.StatefulContract> contracts;
    private AlgoPkgJsonService pkgJsonService;
    private AlgoPackageJson.StatefulContract selectedContract;

    private AlgoConsole console;
    private AlgoAccountService algoAccountService;

    public CreateAppEntryForm() {
    }

    public void initializeData(Project project, AlgoAccount creatorAccount, String contractName) throws DeploymentTargetNotConfigured {
        console = AlgoConsole.getConsole(project);
        try {
            algoAccountService = new AlgoAccountService(project, new LogListenerAdapter(console));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            throw deploymentTargetNotConfigured;
        }

        creatorAccount = null;//TODO For now, let's not use cache due to authAddr dependency
        if(creatorAccount != null) {
            senderAddressTf.setText(creatorAccount.getAddress().toString());

            //TODO - Uncomment when caching is enabled
//            ApplicationManager.getApplication().invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    //setAuthAddress(project, creatorAccount);
//                }
//            }, ModalityState.any());
        }

        authAccountChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
                if(algoAccount != null) {
                    authAccountTf.setText(algoAccount.getAddress());
                    authMnemonicTf.setText(algoAccount.getMnemonic());
                }
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
                    authAccountTf.setText(account.getAddress().toString());
                } catch (Exception ex) {
                    authAccountTf.setText("");
                }
            }
        });

        senderChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
                if(algoAccount != null) {
                    senderAddressTf.setText(algoAccount.getAddress());
                    authAccountTf.setText("");
                    authMnemonicTf.setText("");

                    setAuthAddress(project, algoAccount);
                }
            }
        });

        senderMultiSigChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AlgoMultisigAccount algoMultisigAccount = AccountChooser.getSelectedMultisigAccount(project, true);
                if(algoMultisigAccount != null) {
                    senderAddressTf.setText(algoMultisigAccount.getAddress());
                    authAccountTf.setText("");
                    authMnemonicTf.setText("");

                    setAuthAddressForMultiSigSender(project, algoMultisigAccount);
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
                authAccountTf.setText("");
                authMnemonicTf.setText("");

                try {
                    Address address = new Address(senderAddressTf.getText());
                    AlgoAccount algoAccount = AccountService.getAccountService().getAccountByAddress(address.toString());
                    if(algoAccount == null) {
                        algoAccount = new AlgoAccount(address.toString());
                    }

                    setAuthAddress(project, algoAccount);
                } catch (Exception ex) {

                }
            }
        });

        approvalProgramTf.setEditable(false);
        clearStateProgramTf.setEditable(false);

        this.pkgJsonService = AlgoPkgJsonService.getInstance(project);
        try {
            AlgoPackageJson packageJson = pkgJsonService.getPackageJson();
            if(packageJson != null) {
                contracts = packageJson.getStatefulContractList();
                if(contracts == null) contracts = Collections.EMPTY_LIST;

                contracts.forEach(c -> contractCB.addItem(c.getName()));
            }

            contractCB.addActionListener(evt -> {
                String selectedContract = (String)contractCB.getSelectedItem();
                if(!StringUtil.isEmpty(selectedContract)) {
                    this.selectedContract = packageJson.getStatefulContractByName(selectedContract);
                    if(this.selectedContract != null) {
                        approvalProgramTf.setText(this.selectedContract.getApprovalProgram());
                        clearStateProgramTf.setText(this.selectedContract.getClearStateProgram());

                        globalByteslicesTf.setText(String.valueOf(this.selectedContract.getGlobalByteSlices()));
                        globalIntTf.setText(String.valueOf(this.selectedContract.getGlobalInts()));
                        localByteslicesTf.setText(String.valueOf(this.selectedContract.getLocalByteSlices()));
                        localIntsTf.setText(String.valueOf(this.selectedContract.getLocalInts()));
                        extraPagesTf.setText(String.valueOf(this.selectedContract.getExtraPages()));
                    }
                }
            });

            if(!StringUtil.isEmpty(contractName))
                contractCB.setSelectedItem(contractName);
            else {
                if(contractCB.getModel().getSize() > 0)
                    contractCB.setSelectedIndex(0);
            }

        } catch (PackageJsonException e) {
            IdeaUtil.showNotification(project, "Create App", "algo-package.json could not be read", NotificationType.ERROR, null);
        }
    }

    private void setAuthAddress(Project project, AlgoAccount sender) {
        getAuthAddress(project, sender.getAddress(), (accountAuthAddr) -> {
            if(!StringUtil.isEmpty(accountAuthAddr)) { //auth-addr found
                AccountService accountService = AccountService.getAccountService();
                AlgoAccount algoAccount = accountService.getAccountByAddress(accountAuthAddr);
                if (algoAccount != null && !StringUtil.isEmpty(algoAccount.getMnemonic())) {
                    authAccountTf.setText(algoAccount.getAddress());
                    authMnemonicTf.setText(algoAccount.getMnemonic());
                } else {
                    //TODO alert
                    authAccountTf.setText(accountAuthAddr);
                    IdeaUtil.authorizedAddressNotFoundWarning();
                }
            } else { //No auth-addr
                if(!StringUtil.isEmpty(sender.getMnemonic())) {
                    authAccountTf.setText(sender.getAddress());
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
                    authAccountTf.setText(algoAccount.getAddress());
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


    public String getContractName() {
        return (String)contractCB.getSelectedItem();
    }

    public String getApprovalProgram() {
        return approvalProgramTf.getText();
    }

    public String getClearStateProgram() {
        return clearStateProgramTf.getText();
    }

    public int getGlobalByteslices() {
        return Integer.parseInt(globalByteslicesTf.getText());
    }

    public int getGlobalInts() {
        return Integer.parseInt(globalIntTf.getText());
    }

    public int getLocalByteslices() {
        return Integer.parseInt(localByteslicesTf.getText());
    }

    public int getLocalInts() {
        return Integer.parseInt(localIntsTf.getText());
    }

    public int getExtraPages() {
        return Integer.parseInt(extraPagesTf.getText());
    }

    public Account getAuthorizedAccount() {
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

    protected @Nullable JComponent getMainPanel() {
        return mainPanel;
    }

    protected @Nullable ValidationInfo doValidate() {

        if(StringUtil.isEmpty(senderAddressTf.getText())) {
            return new ValidationInfo("Please select a valid sender address", senderAddressTf);
        }

        if(StringUtil.isEmpty(authAccountTf.getText())) {
            return new ValidationInfo("Please select a valid Authorized account or enter valid mnemonic", authAccountTf);
        }

        //If auth account is set , auth mnemonic should be set
        if(!StringUtil.isEmpty(authAccountTf.getText())
                && StringUtil.isEmpty(authMnemonicTf.getText())) {
            return new ValidationInfo("Please provide a valid mnemonic for the Authorized Address.",
                    authMnemonicTf);
        }


        if(!NumberUtils.isNumber(globalByteslicesTf.getText())) {
            return new ValidationInfo("Invalid Global Byteslices. Integer value expected.", globalByteslicesTf);
        }

        if(!NumberUtils.isNumber(globalIntTf.getText())) {
            return new ValidationInfo("Invalid Global Ints. Integer value expected.", globalIntTf);
        }

        if(!NumberUtils.isNumber(localByteslicesTf.getText())) {
            return new ValidationInfo("Invalid Local Byteslices. Integer value expected.", localByteslicesTf);
        }

        if(!NumberUtils.isNumber(localIntsTf.getText())) {
            return new ValidationInfo("Invalid Local Ints. Integer value expected.", localIntsTf);
        }

        if(!NumberUtils.isNumber(extraPagesTf.getText())) {
            return new ValidationInfo("Invalid Extra Pages. Integer value expected.", extraPagesTf);
        }

        return null;
    }

    public boolean isContractSettingsUpdate() {
        if(selectedContract == null)
            return false;

        if(selectedContract.getGlobalByteSlices() != getGlobalByteslices()
                || selectedContract.getGlobalInts() != getGlobalInts()
                || selectedContract.getLocalByteSlices() != getLocalByteslices()
                || selectedContract.getLocalInts() != getLocalInts()
                || selectedContract.getExtraPages() != getExtraPages()
        ) {
            return true;
        } else {
            return false;
        }

    }

    public void saveUpdatedContractSettings() {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                if(pkgJsonService != null) {
                    try {
                        String contractName = getContractName();
                        AlgoPackageJson.StatefulContract contract = pkgJsonService.getStatefulContract(contractName);
                        if (contract != null) {
                            contract.setGlobalByteSlices(getGlobalByteslices());
                            contract.setGlobalInts(getGlobalInts());
                            contract.setLocalByteSlices(getLocalByteslices());
                            contract.setLocalInts(getLocalInts());
                            contract.setExtraPages(getExtraPages());
                        }

                        pkgJsonService.save();
                        pkgJsonService.markDirty();
                    } catch (Exception e) {
                        if(LOG.isDebugEnabled()) {
                            LOG.error(e);
                        }
                    }
                }
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        authMnemonicTf = new JTextField();
    }

}
