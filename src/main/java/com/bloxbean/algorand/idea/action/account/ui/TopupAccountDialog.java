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

package com.bloxbean.algorand.idea.action.account.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;

public class TopupAccountDialog extends DialogWrapper {
    private JTextField accountTf;
    private JPanel mainPanel;
    private JTextField balanceTf;
    private JLabel balanceLabel;
    private JLabel privateKeyLabel;
    private JButton chooseAccountButton;
    private JPasswordField privateKeyTf;

    public TopupAccountDialog(AnActionEvent event, Project project, boolean isRemote) {
        super(project, true);
        init();

//        if(isRemote) { //No need to show balance for remote mode
//            balanceLabel.setVisible(false);
//            balanceTf.setVisible(false);
//        } else { //No need to pass private key for local mode
//            privateKeyLabel.setVisible(false);
//            privateKeyTf.setVisible(false);
//        }
//        setTitle("Fund an Account");
//
//        final String moduleWokingDir;
//        if(isRemote) { //No need to find working dir
//            moduleWokingDir = null;
//        } else //If local. Needed for maven run
//            moduleWokingDir = PsiCustomUtil.getWorkingDirFromActionEvent(event, project);
//
//        chooseAccountButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                Account selectedAccount = null;
//
//                if(isRemote) {
//                    selectedAccount = AccountChooser.getSelectedAccount(project, true);
//                } else {
//                    selectedAccount = AccountChooser.getLocalAvmSelectedAccount(project, moduleWokingDir, true);
//                }
//
//                if(selectedAccount != null) {
//                    accountTf.setText(selectedAccount.getAddress());
//                    privateKeyTf.setText(selectedAccount.getPrivateKey());
//                }
//            }
//        });
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    public String getAccount() {
        return accountTf.getText().trim();
    }

    public String getPrivateKey() {
        return privateKeyTf.getText().trim();
    }

    public BigInteger getBalance() {
//        try {
//            double aionValue = Double.parseDouble(balanceTf.getText().trim());
//            return AionConversionUtil.aionTonAmp(aionValue);
//        } catch(Exception e) {
//            return BigInteger.ZERO;
//        }
        return BigInteger.ZERO;
    }
}
