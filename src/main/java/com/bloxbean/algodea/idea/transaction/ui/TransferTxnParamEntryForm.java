package com.bloxbean.algodea.idea.transaction.ui;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.common.Tuple;
import com.bloxbean.algodea.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algodea.idea.core.service.AlgoCacheService;
import com.bloxbean.algodea.idea.util.AlgoConversionUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigInteger;
import java.util.List;

public class TransferTxnParamEntryForm {
    private JTextField fromAccountTf;
    private JButton fromAccChooserBtn;
    private JPanel mainPanel;
    private JTextField fromAccMnemonicTf;
    private JTextField toAccountTf;
    private JButton toAccChooserBtn;
    private JTextField amountTf;
    private JButton multiSigBtn;

    public TransferTxnParamEntryForm() {

    }

    public void initializeData(Project project) {
        fromAccChooserBtn.addActionListener(e -> {
                AlgoAccount algoAccount = AccountChooser.getSelectedAccount(project, true);
                if(algoAccount != null) {
                    fromAccountTf.setText(algoAccount.getAddress());
                    fromAccMnemonicTf.setText(algoAccount.getMnemonic());
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
                    fromAccountTf.setText(account.getAddress().toString());
                } catch (Exception ex) {
                    fromAccountTf.setText("");
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

    }

    public @Nullable ValidationInfo doValidate() {

        if(StringUtil.isEmpty(fromAccountTf.getText())) {
            return new ValidationInfo("Please select a valid from account or enter valid mnemonic", fromAccountTf);
        }

        if(getToAccount() == null) {
            return new ValidationInfo("Please select a valid to account", toAccountTf);
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

    public Account getFromAccount() {
        String mnemonic = fromAccMnemonicTf.getText().trim();
        try {
            Account account = new Account(mnemonic);
            return account;
        } catch (Exception e) {
            return null;
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

    public Tuple<Double, BigInteger> getAmount() {
        try {
            double amtInAlgo = Double.parseDouble(amountTf.getText());
            BigInteger microAlgo = AlgoConversionUtil.algoTomAlgo(amtInAlgo);

            return new Tuple<>(amtInAlgo, microAlgo);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
