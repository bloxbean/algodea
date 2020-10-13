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
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
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

public class CreateAppEntryForm {
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

    public CreateAppEntryForm() {
    }

    public void initializeData(Project project, AlgoAccount creatorAccount, String approvalProgram, String clearStateProgram,
                               int globalByteslices, int globalInts, int localByteslices, int localInts) {
        if(creatorAccount != null) {
            accountTf.setText(creatorAccount.getAddress().toString());

            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    mnemonicTf.setText(creatorAccount.getMnemonic());
                }
            }, ModalityState.any());
        }

        if(!StringUtil.isEmpty(approvalProgram)) {
            approvalProgramTf.setText(approvalProgram);
            approvalProgramTf.setEditable(false);
        }

        if(!StringUtil.isEmpty(clearStateProgram)) {
            clearStateProgramTf.setText(clearStateProgram);
            clearStateProgramTf.setEditable(false);
        }

        globalByteslicesTf.setText(String.valueOf(globalByteslices));
        globalIntTf.setText(String.valueOf(globalInts));
        localByteslicesTf.setText(String.valueOf(localByteslices));
        localIntsTf.setText(String.valueOf(localInts));

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

    }

    public String getAccountAddresss() {
        return accountTf.getText();
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

//        String mnemonic = mnemonicTf.getText();
//        try {
//            Account account = new Account(mnemonic);
//        } catch (GeneralSecurityException e) {
//            return new ValidationInfo("Please enter valid mnemonic or select a valid account. " + StringUtil.trimLog(e.getMessage(), 50), mnemonicTf);
//        }

        return null;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        mnemonicTf = new JTextField();
    }

}
