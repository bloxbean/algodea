package com.bloxbean.algodea.idea.atomic.ui;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.transaction.Transaction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ShowTxnDetailsDialog extends DialogWrapper {
    private JEditorPane editorField;
    private JPanel mainPanel;
    private JTextField senderTf;
    private JTextField receiverTf;
    private JTextField assetSenderTf;
    private JTextField assetReceiverTf;
    private JLabel senderLabel;
    private JLabel receiverLabel;
    private JLabel assetSenderLabel;
    private JLabel assetReceiverLabel;
    private String emptyAddressString;

    protected ShowTxnDetailsDialog(@Nullable Project project, Transaction transaction, String content) {
        super(project, true);
        createCustomActions();
        init();
        setTitle("Transaction Details");
        editorField.setContentType("application/json");
        editorField.setText(content);

        this.emptyAddressString = new Address().toString();
        initializeData(transaction);
    }

    protected void createCustomActions() {
    }

    private void initializeData(Transaction transaction) {
        if(transaction == null) return;

        if(transaction.sender != null && !emptyAddressString.equals(transaction.sender.toString())) {
            senderTf.setText(transaction.sender.toString());
            senderTf.setVisible(true);
            senderLabel.setVisible(true);
        } else {
            senderTf.setVisible(false);
            senderLabel.setVisible(false);
        }

        if(transaction.receiver != null && !emptyAddressString.equals(transaction.receiver.toString())) {
            receiverTf.setText(transaction.receiver.toString());

            receiverTf.setVisible(true);
            receiverLabel.setVisible(true);
        } else {
            receiverTf.setVisible(false);
            receiverLabel.setVisible(false);
        }

        if(transaction.assetSender != null
                && !emptyAddressString.equals(transaction.assetSender.toString())) {
            assetSenderTf.setText(transaction.assetSender.toString());

            assetSenderTf.setVisible(true);
            assetSenderLabel.setVisible(true);
        } else {
            assetSenderTf.setVisible(false);
            assetSenderLabel.setVisible(false);
        }

        if(transaction.assetReceiver != null && !emptyAddressString.equals(transaction.assetReceiver.toString())) {
            assetReceiverTf.setText(transaction.assetReceiver.toString());

            assetReceiverTf.setVisible(true);
            assetReceiverLabel.setVisible(true);
        } else {
            assetReceiverTf.setVisible(false);
            assetReceiverLabel.setVisible(false);
        }
    }

    public void setEnableContentField(boolean flag) {
        editorField.setEditable(false);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }
}
