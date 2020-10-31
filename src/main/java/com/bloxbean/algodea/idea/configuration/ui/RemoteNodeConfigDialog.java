package com.bloxbean.algodea.idea.configuration.ui;

import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.service.NetworkService;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class RemoteNodeConfigDialog extends DialogWrapper{
    private JPanel contentPane;
    private JTextField serverName;
    private JTextField nodeApiEndpoint;
    private JTextField apiKey;
    private JTextField indexerApiEndpoint;
    private JButton fetchNetworkInfoBtn;
    private JLabel connectionStatusLabel;
    private JTextField genesisHashTf;
    private JTextField genesisIdTf;
    private JLabel fetchNetworkInfoHelpLabel;
    private JCheckBox ignoreConnectionCheckCB;

    public RemoteNodeConfigDialog(Project project) {
        this(project, null);
    }

    public RemoteNodeConfigDialog(Project project, NodeInfo existingNodeInfo) {
        super(project);
        init();
        setTitle("Algorand Node Configuration");

        fetchNetworkInfoHelpLabel.setText("<html>Please click on  \"Fetch Network Info\" to fetch the genesis details of " +
                "the network <br/> and test the connection.</html/>");

        if(existingNodeInfo != null) {
            serverName.setText(existingNodeInfo.getName());
            nodeApiEndpoint.setText(existingNodeInfo.getNodeAPIUrl());
            apiKey.setText(existingNodeInfo.getApiKey());
            indexerApiEndpoint.setText(existingNodeInfo.getIndexerAPIUrl());
            genesisHashTf.setText(existingNodeInfo.getGenesisHash());
            genesisIdTf.setText(existingNodeInfo.getGenesisId());
        }

        fetchNetworkInfoBtn.addActionListener(e -> {
            NodeInfo nodeInfo = new NodeInfo();
            nodeInfo.setNodeAPIUrl(getNodeApiUrl());
            nodeInfo.setApiKey(getApiKey());
            nodeInfo.setName(getServerName());

            AlgoConsole algoConsole = AlgoConsole.getConsole(project);
            algoConsole.clearAndshow();

            NetworkService networkService = new NetworkService(nodeInfo, new LogListenerAdapter(algoConsole));
            try {
                TransactionParametersResponse transactionParams = networkService.getNetworkInfo();
                if(transactionParams != null) {
                    String genesisHash = transactionParams.genesisHash();
                    String genesisId = transactionParams.genesisId;

                    if(!StringUtil.isEmpty(genesisHash)) {
                        genesisHashTf.setText(genesisHash);
                    }

                    if(!StringUtil.isEmpty(genesisId)) {
                        genesisIdTf.setText(genesisId);
                    }

                    connectionStatusLabel.setForeground(Color.black);
                    connectionStatusLabel.setText("Successfully connected to the Algorand Node");
                } else {
                    algoConsole.showErrorMessage("Could not connect to the Algo node");

                    genesisHashTf.setText("");
                    genesisIdTf.setText("");
                    connectionStatusLabel.setForeground(Color.red);
                    connectionStatusLabel.setText("Unable to get response from the Algorand Node.");
                }
            } catch (Exception ex) {
                genesisHashTf.setText("");
                genesisIdTf.setText("");
                algoConsole.showErrorMessage("Connection test failed", ex);
                connectionStatusLabel.setForeground(Color.red);
                connectionStatusLabel.setText("Unable to connect to the Algorand node, Reason: " + ex.getMessage());
            }
        });
    }

    public String getServerName() {
        return StringUtil.trim(serverName.getText());
    }

    public String getNodeApiUrl() {
        return StringUtil.trim(nodeApiEndpoint.getText());
    }

    public String getApiKey() {
        return StringUtil.trim(apiKey.getText());
    }

    public String getIndexerApiUrl() {
        return StringUtil.trim(indexerApiEndpoint.getText());
    }

    public String getGenesisHash() {
        return StringUtil.trim(genesisHashTf.getText());
    }

    public String getGenesisId() {
        return StringUtil.trim(genesisIdTf.getText());
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

        if(StringUtil.isEmpty(apiKey.getText())) {
            return new ValidationInfo("Api key cannot be empty", apiKey);
        }

        if(!StringUtil.isEmpty(nodeApiEndpoint.getText())
                && nodeApiEndpoint.getText().contains("purestake.io")
                && StringUtil.isEmpty(indexerApiEndpoint.getText())) {
            return new ValidationInfo("Indexer Api Endpoint is mandatory for Purestake.io node integration", indexerApiEndpoint);
        }

        if(!StringUtil.isEmpty(indexerApiEndpoint.getText())
                && !indexerApiEndpoint.getText().startsWith("http://")
                && !indexerApiEndpoint.getText().startsWith("https://")) {
            return new ValidationInfo("Invalid Indexer api endpoint", indexerApiEndpoint);
        }

        if(!ignoreConnectionCheckCB.isSelected()) {
            if (StringUtil.isEmpty(genesisHashTf.getText())) {
                return new ValidationInfo("Genesis hash is empty. Try to fetch it from network", genesisHashTf);
            }

            if (StringUtil.isEmpty(genesisIdTf.getText())) {
                return new ValidationInfo("Genesis Id is empty. Try to fetch it from network", genesisHashTf);
            }
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
