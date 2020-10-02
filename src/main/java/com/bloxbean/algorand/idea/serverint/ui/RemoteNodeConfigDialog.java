package com.bloxbean.algorand.idea.serverint.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.twelvemonkeys.lang.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.*;

public class RemoteNodeConfigDialog extends DialogWrapper{
    private JPanel contentPane;
    private JTextField serverName;
    private JTextField nodeApiEndpoint;
    private JTextField apiKey;
    private JTextField indexerApiEndpoint;

    public RemoteNodeConfigDialog(Project project) {
        super(project);
        init();
        setTitle("Algorand Node Configuration");
    }

    public String getServerName() {
        return serverName.getText();
    }

    public String getNodeApiUrl() {
        return nodeApiEndpoint.getText();
    }

    public String getApiKey() {
        return apiKey.getText();
    }

    public String getIndexerApiUrl() {
        return indexerApiEndpoint.getText();
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        if(StringUtil.isEmpty(serverName.getText())) {
            return new ValidationInfo("Invalid or empty name", serverName);
        }

        if(StringUtil.isEmpty(nodeApiEndpoint.getText())) {
            return new ValidationInfo("Node api endpoint cannot be empty", nodeApiEndpoint);
        }

        if(!nodeApiEndpoint.getText().startsWith("http://") && !nodeApiEndpoint.getText().startsWith("https://")) {
            return new ValidationInfo("Invalid Node api endpoint", nodeApiEndpoint);
        }

        if(!StringUtil.isEmpty(indexerApiEndpoint.getText())
                && !indexerApiEndpoint.getText().startsWith("http://")
                && !indexerApiEndpoint.getText().startsWith("https://")) {
            return new ValidationInfo("Invalid Indexer api endpoint", indexerApiEndpoint);
        }

        return null;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }

    public JPanel getMainPanel() {
        return contentPane;
    }
}
