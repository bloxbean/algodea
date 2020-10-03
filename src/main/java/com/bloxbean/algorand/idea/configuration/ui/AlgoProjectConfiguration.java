package com.bloxbean.algorand.idea.configuration.ui;

import com.bloxbean.algorand.idea.common.Tuple;
import com.bloxbean.algorand.idea.configuration.model.AlgoLocalSDK;
import com.bloxbean.algorand.idea.configuration.model.NodeInfo;
import com.bloxbean.algorand.idea.configuration.service.AlgoLocalSDKState;
import com.bloxbean.algorand.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algorand.idea.configuration.service.ConfiguraionHelperService;
import com.bloxbean.algorand.idea.configuration.service.NodeConfigState;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBRadioButton;
import com.twelvemonkeys.lang.StringUtil;

import javax.swing.*;
import java.util.List;

public class AlgoProjectConfiguration {
    private JTabbedPane tabbedPane1;
    private JPanel mainPanel;
    private JRadioButton localAlgorandSDKRB;
    private JRadioButton algorandNodeRB;
    private JComboBox localSDKCB;
    private JButton newLocalSDKBtn;
    private JComboBox algorandNodeCB;
    private JComboBox deployNodeCB;
    private JButton newDeployNodeBtn;
    private JButton newCompileNodeBtn;
    private ButtonGroup compileRadioBtnGroup;

    private NodeInfo emptyNodeInfo = new NodeInfo();
    private AlgoLocalSDK emptyLocalSDK = new AlgoLocalSDK();

    public AlgoProjectConfiguration(Project project) {
        initializeData(project);
        attachHandlers(project);
        setCurrentSelection(project);
    }

    private void initializeData(Project project) {
        populateAvilableNodes();
        populateAvailableLocalSDKs();
    }

    private void setCurrentSelection(Project project) {
        AlgoProjectState algoProjectState = AlgoProjectState.getInstance(project);
        AlgoProjectState.State state = algoProjectState.getState();

        //set compiler setting
        if(AlgoProjectState.ConfigType.remote_node == state.getCompilerType()) {
            algorandNodeRB.setSelected(true);

            setSelectedNode(algorandNodeCB, state.getCompilerId());
        } else {
            localAlgorandSDKRB.setSelected(true);
            setSelectedLocalSDK(localSDKCB, state.getCompilerId());
        }

        if(!StringUtil.isEmpty(state.getDeploymentServerId())) {
            setSelectedNode(deployNodeCB, state.getDeploymentServerId());
        }

        if(AlgoProjectState.ConfigType.remote_node.equals(state.getCompilerType())) {
            algorandNodeRB.setSelected(true);
        } else {
            localAlgorandSDKRB.setSelected(true);
        }

        enableDisableCompilationType();
    }

    private void setSelectedLocalSDK(JComboBox cb, String id) {
        for(int i=0; i < cb.getItemCount(); i++) {
            AlgoLocalSDK algoLocalSDK = (AlgoLocalSDK)cb.getItemAt(i);
            if(algoLocalSDK == null) continue;

            if(algoLocalSDK.getId() != null && algoLocalSDK.getId().equals(id)) {
                cb.setSelectedIndex(i);
                break;
            }
        }
    }

    private void setSelectedNode(JComboBox cb, String id) {
        for(int i=0; i < cb.getItemCount(); i++) {
            NodeInfo nodeI = (NodeInfo) cb.getItemAt(i);
            if(nodeI == null) continue;

            if(nodeI.getId() != null && nodeI.getId().equals(id)) {
                cb.setSelectedIndex(i);
                break;
            }
        }
    }

    private void populateAvilableNodes() {
        NodeConfigState nodeConfigState = ServiceManager.getService(NodeConfigState.class);
        List<NodeInfo> nodes = nodeConfigState.getNodes();

        algorandNodeCB.removeAllItems();
        deployNodeCB.removeAllItems();

        if(nodes != null) {
            algorandNodeCB.addItem(emptyNodeInfo);
            deployNodeCB.addItem(emptyNodeInfo);
            for (NodeInfo node : nodes) {
                algorandNodeCB.addItem(node);
                deployNodeCB.addItem(node);
            }
        }
    }

    private void populateAvailableLocalSDKs() {
        AlgoLocalSDKState localSDKState = ServiceManager.getService(AlgoLocalSDKState.class);
        List<AlgoLocalSDK> localSdks = localSDKState.getLocalSDKs();

        localSDKCB.removeAllItems();
        if(localSdks != null) {
            localSDKCB.addItem(emptyLocalSDK);
            for(AlgoLocalSDK localSDK: localSdks) {
                localSDKCB.addItem(localSDK);
            }
        }
    }

    private void attachHandlers(Project project) {
        newCompileNodeBtn.addActionListener(e -> {
            NodeInfo nodeInfo = ConfiguraionHelperService.createNewNodeConfiguration(project);
            if(nodeInfo != null) {
                //do something. Refresh
                populateAvilableNodes();
                setSelectedNode(algorandNodeCB, nodeInfo.getId());
                setSelectedNode(deployNodeCB, nodeInfo.getId());
            }
        });

        newLocalSDKBtn.addActionListener(e -> {
            AlgoLocalSDK localSDK = ConfiguraionHelperService.createNewLocalSDKConfiguration(project);
            if(localSDK != null) {
                populateAvailableLocalSDKs();
                setSelectedLocalSDK(localSDKCB, localSDK.getId());
            }
        });

        //New deploy target
        newDeployNodeBtn.addActionListener(e -> {
            NodeInfo nodeInfo = ConfiguraionHelperService.createNewNodeConfiguration(project);
            if(nodeInfo != null) {
                //do something. Refresh
                populateAvilableNodes();

                setSelectedNode(deployNodeCB, nodeInfo.getId());
                setSelectedNode(algorandNodeCB, nodeInfo.getId());
            }
        });

        localAlgorandSDKRB.addActionListener(e -> {
            enableDisableCompilationType();
        });

        algorandNodeRB.addActionListener(e -> {
            enableDisableCompilationType();
        });
    }

    private void enableDisableCompilationType() {
        if(localAlgorandSDKRB.isSelected()) {
            //clean remote node
            if(algorandNodeCB.getItemCount() >= 1)
                algorandNodeCB.setSelectedIndex(0);
            algorandNodeCB.setEnabled(false);
            newCompileNodeBtn.setEnabled(false);

            localSDKCB.setEnabled(true);
            newLocalSDKBtn.setEnabled(true);
        } else if(algorandNodeRB.isSelected()) {
            if(localSDKCB.getItemCount() >= 1)
                localSDKCB.setSelectedIndex(0);
            localSDKCB.setEnabled(false);
            newLocalSDKBtn.setEnabled(false);

            algorandNodeCB.setEnabled(true);
            newCompileNodeBtn.setEnabled(true);
        }
    }

    public Tuple<AlgoProjectState.ConfigType, String> getCompilerSdkId() {
        if(localAlgorandSDKRB.isSelected()) {
            AlgoLocalSDK algoLocalSDK = (AlgoLocalSDK) localSDKCB.getSelectedItem();
            if(algoLocalSDK != null)
                return new Tuple(AlgoProjectState.ConfigType.local_sdk, algoLocalSDK.getId());
            else
                return null;
        } else if(algorandNodeRB.isSelected()) {
            NodeInfo nodeInfo = (NodeInfo) algorandNodeCB.getSelectedItem();
            if(nodeInfo != null)
                return new Tuple<>(AlgoProjectState.ConfigType.remote_node, nodeInfo.getId());
            else
                return null;
        }
        return null;
    }

    public String getDeployementNodeId() {
        NodeInfo nodeInfo = (NodeInfo) deployNodeCB.getSelectedItem();
        if(nodeInfo != null)
            return nodeInfo.getId();
        else
            return null;
    }



    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        localAlgorandSDKRB = new JBRadioButton();
        algorandNodeRB = new JBRadioButton();

        compileRadioBtnGroup = new ButtonGroup();
        compileRadioBtnGroup.add(localAlgorandSDKRB);
        compileRadioBtnGroup.add(algorandNodeRB);
    }
}
