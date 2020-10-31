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
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.pkg.AlgoPkgJsonService;
import com.bloxbean.algodea.idea.pkg.exception.PackageJsonException;
import com.bloxbean.algodea.idea.pkg.model.AlgoPackageJson;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collections;
import java.util.List;

public class CreateAppEntryForm {
    private final static Logger LOG = Logger.getInstance(CreateAppEntryForm.class);

    private JPanel mainPanel;
    private JTextField accountTf;
    private JTextField approvalProgramTf;
    private JTextField clearStateProgramTf;
    private JTextField globalByteslicesTf;
    private JTextField globalIntTf;
    private JTextField localByteslicesTf;
    private JTextField localIntsTf;
    private JButton accountChooser;
    private JTextField mnemonicTf;
    private JComboBox contractCB;
    private List<AlgoPackageJson.StatefulContract> contracts;
    private AlgoPkgJsonService pkgJsonService;
    AlgoPackageJson.StatefulContract selectedContract;

    public CreateAppEntryForm() {
    }

    public void initializeData(Project project, AlgoAccount creatorAccount, String contractName) {
        if(creatorAccount != null) {
            accountTf.setText(creatorAccount.getAddress().toString());

            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    mnemonicTf.setText(creatorAccount.getMnemonic());
                }
            }, ModalityState.any());
        }

        accountChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
                if(algoAccount != null) {
                    accountTf.setText(algoAccount.getAddress());
                    mnemonicTf.setText(algoAccount.getMnemonic());
                }
            }
        });

        mnemonicTf.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                String mnemonic = mnemonicTf.getText();
                try {
                    Account account = new Account(mnemonic);
                    accountTf.setText(account.getAddress().toString());
                } catch (Exception ex) {
                    accountTf.setText("");
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

    public String getAccountAddresss() {
        return accountTf.getText();
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

    public Account getAccount() {
        String mnemonic = mnemonicTf.getText().trim();
        try {
            Account account = new Account(mnemonic);
            return account;
        } catch (Exception e) {
            return null;
        }
    }

    protected @Nullable JComponent getMainPanel() {
        return mainPanel;
    }

    protected @Nullable ValidationInfo doValidate() {

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

        if(StringUtil.isEmpty(accountTf.getText())) {
            return new ValidationInfo("Please select a valid creator account or enter valid mnemonic", accountTf);
        }

        return null;
    }

    public boolean isContractSettingsUpdate() {
        if(selectedContract == null)
            return false;

        if(selectedContract.getGlobalByteSlices() != getGlobalByteslices()
                || selectedContract.getGlobalInts() != getGlobalInts()
                || selectedContract.getLocalByteSlices() != getLocalByteslices()
                || selectedContract.getLocalInts() != getLocalInts()) {
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
        mnemonicTf = new JTextField();
    }

}
