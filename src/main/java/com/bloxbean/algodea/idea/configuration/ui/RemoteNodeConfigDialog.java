package com.bloxbean.algodea.idea.configuration.ui;

import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.service.NetworkService;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.TextFieldWithAutoCompletion;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class RemoteNodeConfigDialog extends DialogWrapper{
    private JPanel contentPane;
    private JTextField serverName;
    private JTextField apiKey;
    private TextFieldWithAutoCompletion indexerApiEndpoint;
    private JButton fetchNetworkInfoBtn;
    private JLabel connectionStatusLabel;
    private JTextField genesisHashTf;
    private JTextField genesisIdTf;
    private JLabel fetchNetworkInfoHelpLabel;
    private JCheckBox ignoreConnectionCheckCB;
    private TextFieldWithAutoCompletion nodeApiEndpoint;

    private Project project;

    private static java.util.List<NodeConfig> defaultNodeConfigs;

    static {
        defaultNodeConfigs = new ArrayList<>();

        defaultNodeConfigs.add(new NodeConfig("http://localhost:4001",
                "http://localhost:8980", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));

        defaultNodeConfigs.add(new NodeConfig("https://node.testnet.algoexplorerapi.io",
                "https://algoindexer.testnet.algoexplorerapi.io", "algoexplorer-dummykey"));
        defaultNodeConfigs.add(new NodeConfig("https://node.algoexplorerapi.io",
                "https://algoindexer.algoexplorerapi.io", "algoexplorer-dummykey"));

        defaultNodeConfigs.add(new NodeConfig("https://testnet-algorand.api.purestake.io/ps2",
                "https://testnet-algorand.api.purestake.io/idx2", ""));
    }

    public RemoteNodeConfigDialog(Project project) {
        this(project, null);
    }

    public RemoteNodeConfigDialog(Project project, NodeInfo existingNodeInfo) {
        super(project);
        this.project = project;
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

            ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
                @Override
                public void run() {
                    ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
                    connectionTest(nodeInfo, algoConsole);
                    progressIndicator.setFraction(1.0);
                }
            } , "Connecting to Algorand Node to fetch network info...", true, project);
        });
    }

    private void connectionTest(NodeInfo nodeInfo, AlgoConsole algoConsole) {
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
                connectionStatusLabel.setText("Successfully connected to Algorand Node");
            } else {
                algoConsole.showErrorMessage("Could not connect to Algorand node");

                genesisHashTf.setText("");
                genesisIdTf.setText("");
                connectionStatusLabel.setForeground(Color.red);
                connectionStatusLabel.setText("Unable to get response from Algorand Node.");
            }
        } catch (Exception ex) {
            genesisHashTf.setText("");
            genesisIdTf.setText("");
            algoConsole.showErrorMessage("Connection test failed", ex);
            connectionStatusLabel.setForeground(Color.red);
            connectionStatusLabel.setText("Unable to connect to the Algorand node, Reason: " + ex.getMessage());
        }
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

        if(StringUtil.isEmpty(apiKey.getText()) &&
                (!StringUtil.isEmpty(nodeApiEndpoint.getText()) && !nodeApiEndpoint.getText().contains("algoexplorer.io"))) { //Not required for algoexplorer
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
        Collection<String> availableNodeOptions = new ArrayList<String>();
        availableNodeOptions.add("");

        availableNodeOptions.addAll(defaultNodeConfigs.stream().map(nodeConfig -> nodeConfig.apiEndpoint).collect(Collectors.toList()));
        nodeApiEndpoint =  TextFieldWithAutoCompletion.create(project, availableNodeOptions, true, "" );

        nodeApiEndpoint.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if(nodeApiEndpoint != null && !nodeApiEndpoint.getText().isEmpty()) {
                    NodeConfig defNodeConfig = getDefaultNodeConfigByApiEndpoint(nodeApiEndpoint.getText());
                    if (defNodeConfig != null) {
                        if (StringUtil.isEmpty(apiKey.getText())) {
                            apiKey.setText(defNodeConfig.getApiKey());
                        }

                        if (StringUtil.isEmpty(indexerApiEndpoint.getText())) {
                            indexerApiEndpoint.setText(defNodeConfig.getIndexerApiEndpoint());
                        }
                    }
                }
            }
        });

        Collection<String> availableIndexerEndpoints = new ArrayList<String>();
        availableIndexerEndpoints.add("");
        availableIndexerEndpoints.addAll(defaultNodeConfigs.stream().map(nodeConfig -> nodeConfig.indexerApiEndpoint).collect(Collectors.toList()));
        indexerApiEndpoint = TextFieldWithAutoCompletion.create(project, availableIndexerEndpoints, true, "" );
    }

    private NodeConfig getDefaultNodeConfigByApiEndpoint(String apiEndpoint) {
        return defaultNodeConfigs.stream().filter(nodeConfig -> nodeConfig.apiEndpoint.equals(apiEndpoint)).findFirst()
                .get();
    }

    static class NodeConfig {
        String apiEndpoint;
        String indexerApiEndpoint;
        String apiKey;

        public NodeConfig(String apiEndpoint, String indexerApiEndpoint, String apiKey) {
            this.apiEndpoint = apiEndpoint;
            this.apiKey = apiKey;
            this.indexerApiEndpoint = indexerApiEndpoint;
        }

        public String getApiEndpoint() {
            return apiEndpoint;
        }

        public String getIndexerApiEndpoint() {
            return indexerApiEndpoint;
        }

        public String getApiKey() {
            return apiKey;
        }
    }
}
