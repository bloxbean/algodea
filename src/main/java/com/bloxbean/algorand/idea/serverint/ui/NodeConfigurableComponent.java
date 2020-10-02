package com.bloxbean.algorand.idea.serverint.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.BalloonImpl;
import org.jdesktop.swingx.action.ActionManager;

import javax.swing.*;

public class NodeConfigurableComponent {
    private JPanel panel;
    private JButton addButton;
    private JButton deleteButton;
    private JTextField nodeName;
    private JTextField nodeApiUrl;
    private JTextField apiKey;
    private JTextField indexerApiUrl;
    private JList list1;
    private String nodeId;

    public JPanel getPanel() {
        return panel;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getServerName() {
        return nodeName.getText();
    }

    public String getNodeApiUrl() {
        return nodeApiUrl.getText();
    }

    public String getApiKey() {
        return apiKey.getText();
    }

    public String getIndexerApiUrl() {
        return indexerApiUrl.getText();
    }

    public void setNodeId(String id) {
        nodeId = id;
    }

    public void setNodeApiUrl(String nodeApiUrl) {
        this.nodeApiUrl.setText(nodeApiUrl);
    }

    public void setApiKey(String apiKey) {
        this.apiKey.setText(apiKey);
    }

    public void setIndexerApiUrl(String indexerApiUrl) {
        this.indexerApiUrl.setText(indexerApiUrl);
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
//        addButton = new BalloonImpl.ActionButton(AllIcons.General.Add, AllIcons.General.Add, "Add new node", mouseEvent -> {})
    }
}
