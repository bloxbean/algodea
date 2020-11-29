package com.bloxbean.algodea.idea.configuration.ui;

import com.bloxbean.algodea.idea.configuration.model.AlgoLocalSDK;
import com.bloxbean.algodea.idea.configuration.service.AlgoLocalSDKState;
import com.bloxbean.algodea.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algodea.idea.configuration.service.NodeConfigState;
import com.bloxbean.algodea.idea.common.Tuple;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.configuration.service.ConfiguraionHelperService;
import com.bloxbean.algodea.idea.pkg.AlgoPkgJsonService;
import com.bloxbean.algodea.idea.util.AlgoModuleUtils;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
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
    private JButton localSDKDetailBtn;
    private JButton algorandNodeDetailBtn;
    private JButton deployNodeDetailBtn;
    private JPanel contractSettingsTab;
    private ButtonGroup compileRadioBtnGroup;

    private ContractSettingsConfigurationPanel contractSettingsPanel;

    private NodeInfo emptyNodeInfo = new NodeInfo();
    private AlgoLocalSDK emptyLocalSDK = new AlgoLocalSDK();

    private boolean configChanged;

    public AlgoProjectConfiguration(Project project) {
        initializeData(project);
        attachHandlers(project);
        setCurrentSelection(project);

        //Don't change this call sequence. This is needed for change notifier
        //Keep it at the end to ignore initial selection
        listenSelectionChange();
    }

    private void initializeData(Project project) {
        populateAvilableNodes();
        populateAvailableLocalSDKs();
        populateContractSettingsPanel(project);
    }

    private void setCurrentSelection(Project project) {
        AlgoProjectState algoProjectState = AlgoProjectState.getInstance(project);
        AlgoProjectState.State state = algoProjectState.getState();

        //set compiler setting
        if(AlgoProjectState.ConfigType.local_sdk == state.getCompilerType()){
            localAlgorandSDKRB.setSelected(true);
            setSelectedLocalSDK(localSDKCB, state.getCompilerId());
        } else {
            algorandNodeRB.setSelected(true);
            setSelectedNode(algorandNodeCB, state.getCompilerId());
        }

        if (!StringUtil.isEmpty(state.getDeploymentServerId())) {
            setSelectedNode(deployNodeCB, state.getDeploymentServerId());
        }

        if (AlgoProjectState.ConfigType.local_sdk.equals(state.getCompilerType())) {
            localAlgorandSDKRB.setSelected(true);
        } else {
            algorandNodeRB.setSelected(true);
        }

        enableDisableCompilationType();
    }

    private void setSelectedLocalSDK(JComboBox cb, String id) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            AlgoLocalSDK algoLocalSDK = (AlgoLocalSDK) cb.getItemAt(i);
            if (algoLocalSDK == null) continue;

            if (algoLocalSDK.getId() != null && algoLocalSDK.getId().equals(id)) {
                cb.setSelectedIndex(i);
                break;
            }
        }
    }

    private void setSelectedNode(JComboBox cb, String id) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            NodeInfo nodeI = (NodeInfo) cb.getItemAt(i);
            if (nodeI == null) continue;

            if (nodeI.getId() != null && nodeI.getId().equals(id)) {
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

        if (nodes != null) {
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
        if (localSdks != null) {
            localSDKCB.addItem(emptyLocalSDK);
            for (AlgoLocalSDK localSDK : localSdks) {
                localSDKCB.addItem(localSDK);
            }
        }
    }

    private void populateContractSettingsPanel(Project project) {
        AlgoProjectState.State state = AlgoProjectState.getInstance(project).getState();
        AlgoPkgJsonService pkgJsonService = AlgoPkgJsonService.getInstance(project);
        contractSettingsPanel.poulateData(state, pkgJsonService);

        String projectFolder = project.getBasePath();

        String sourcePath = AlgoModuleUtils.getFirstSourceRootPath(project);//getFirstTEALSourceRootPath(project);

        if(sourcePath == null) {
            sourcePath = AlgoModuleUtils.getModuleDirPath(project);
        }

        if(!StringUtil.isEmpty(sourcePath)) {
            contractSettingsPanel.setSourceFolder(sourcePath);
        }

        if(!StringUtil.isEmpty(projectFolder)) {
            contractSettingsPanel.setProjectFolder(projectFolder);
        }
    }

    private void attachHandlers(Project project) {
        newCompileNodeBtn.addActionListener(e -> {
            NodeInfo nodeInfo = ConfiguraionHelperService.createOrUpdateNewNodeConfiguration(project, null);
            if (nodeInfo != null) {
                //do something. Refresh
                populateAvilableNodes();
                setSelectedNode(algorandNodeCB, nodeInfo.getId());
                setSelectedNode(deployNodeCB, nodeInfo.getId());
            }
        });

        newLocalSDKBtn.addActionListener(e -> {
            AlgoLocalSDK localSDK = ConfiguraionHelperService.createOrUpdateLocalSDKConfiguration(project, null);
            if (localSDK != null) {
                populateAvailableLocalSDKs();
                setSelectedLocalSDK(localSDKCB, localSDK.getId());
            }
        });

        //New deploy target
        newDeployNodeBtn.addActionListener(e -> {
            NodeInfo nodeInfo = ConfiguraionHelperService.createOrUpdateNewNodeConfiguration(project, null);
            if (nodeInfo != null) {
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

        localSDKDetailBtn.addActionListener(e -> { //Update if required.
            AlgoLocalSDK localSDK = (AlgoLocalSDK) localSDKCB.getSelectedItem();
            if (localSDK == null || StringUtil.isEmpty(localSDK.getId())) {
                Messages.showWarningDialog("Please select a Algorand Local SDK first to see the details", "");
                return;
            }

            AlgoLocalSDK updatedSDK = ConfiguraionHelperService.createOrUpdateLocalSDKConfiguration(project, localSDK);
            if (updatedSDK != null) {
                updateLocalSDKInComboBox(localSDKCB, updatedSDK);
            }
        });

        algorandNodeDetailBtn.addActionListener(e -> {
            NodeInfo nodeInfo = (NodeInfo) algorandNodeCB.getSelectedItem();
            if (nodeInfo == null || StringUtil.isEmpty(nodeInfo.getId())) {
                Messages.showWarningDialog("Please select a Algorand node first to see the details", "");
                return;
            }

            NodeInfo updatedNodeInfo = ConfiguraionHelperService.createOrUpdateNewNodeConfiguration(project, nodeInfo);
            if (updatedNodeInfo != null) {
                updateNodeInfoInComboBox(algorandNodeCB, updatedNodeInfo);
            }
        });

        deployNodeDetailBtn.addActionListener(e -> {
            NodeInfo nodeInfo = (NodeInfo) deployNodeCB.getSelectedItem();
            if (nodeInfo == null || StringUtil.isEmpty(nodeInfo.getId())) {
                Messages.showWarningDialog("Please select a Algorand node first to see the details", "");
                return;
            }

            NodeInfo updatedNodeInfo = ConfiguraionHelperService.createOrUpdateNewNodeConfiguration(project, nodeInfo);
            if (updatedNodeInfo != null) {
                updateNodeInfoInComboBox(deployNodeCB, updatedNodeInfo);
            }
        });
    }

    //This is required for change notifier
    private void listenSelectionChange() {
        localSDKCB.addActionListener(e -> {
            configChanged = true;
        });

        algorandNodeCB.addActionListener(e -> {
            configChanged = true;
        });

        deployNodeCB.addActionListener(e -> {
            configChanged = true;
        });
    }

    public boolean isConfigChanged() {
        return configChanged;
    }

    //For update operation
    private void updateNodeInfoInComboBox(JComboBox cb, NodeInfo updatedNodeInfo) {
        int count = cb.getItemCount();
        if (count == 0) return;

        for (int i = 0; i < count; i++) {
            NodeInfo nd = (NodeInfo) cb.getItemAt(i);
            if (nd == null || StringUtil.isEmpty(nd.getId()))
                continue;
            if (nd.getId().equals(updatedNodeInfo.getId())) {
                nd.updateValues(updatedNodeInfo);
                break;
            }
        }

    }

    private void updateLocalSDKInComboBox(JComboBox cb, AlgoLocalSDK updatedLocalSDK) {
        int count = cb.getItemCount();
        if (count == 0) return;

        for (int i = 0; i < count; i++) {
            AlgoLocalSDK lsdk = (AlgoLocalSDK) cb.getItemAt(i);
            if (lsdk == null || StringUtil.isEmpty(lsdk.getId()))
                continue;
            if (lsdk.getId().equals(updatedLocalSDK.getId())) {
                lsdk.updateValues(updatedLocalSDK);
                break;
            }
        }

    }

    private void enableDisableCompilationType() {
        if (localAlgorandSDKRB.isSelected()) {
            //clean remote node
            if (algorandNodeCB.getItemCount() >= 1)
                algorandNodeCB.setSelectedIndex(0);
            algorandNodeCB.setEnabled(false);
            newCompileNodeBtn.setEnabled(false);
            algorandNodeDetailBtn.setEnabled(false);

            localSDKCB.setEnabled(true);
            newLocalSDKBtn.setEnabled(true);
            localSDKDetailBtn.setEnabled(true);
        } else if (algorandNodeRB.isSelected()) {
            if (localSDKCB.getItemCount() >= 1)
                localSDKCB.setSelectedIndex(0);
            localSDKCB.setEnabled(false);
            newLocalSDKBtn.setEnabled(false);
            localSDKDetailBtn.setEnabled(false);

            algorandNodeCB.setEnabled(true);
            newCompileNodeBtn.setEnabled(true);
            algorandNodeDetailBtn.setEnabled(true);
        }
    }

    public Tuple<AlgoProjectState.ConfigType, String> getCompilerSdkId() {
        if (localAlgorandSDKRB.isSelected()) {
            AlgoLocalSDK algoLocalSDK = (AlgoLocalSDK) localSDKCB.getSelectedItem();
            if (algoLocalSDK != null)
                return new Tuple(AlgoProjectState.ConfigType.local_sdk, algoLocalSDK.getId());
            else
                return null;
        } else if (algorandNodeRB.isSelected()) {
            NodeInfo nodeInfo = (NodeInfo) algorandNodeCB.getSelectedItem();
            if (nodeInfo != null)
                return new Tuple<>(AlgoProjectState.ConfigType.remote_node, nodeInfo.getId());
            else
                return null;
        }
        return null;
    }

    public String getDeployementNodeId() {
        NodeInfo nodeInfo = (NodeInfo) deployNodeCB.getSelectedItem();
        if (nodeInfo != null)
            return nodeInfo.getId();
        else
            return null;
    }

    public void updateDataToState(AlgoProjectState.State state, AlgoPkgJsonService algoPkgJsonService) {

        //Save Compile / Build settings

        Tuple<AlgoProjectState.ConfigType, String> compilerSetting = getCompilerSdkId();

        if (compilerSetting != null) {
            state.setCompilerType(compilerSetting._1());
            state.setCompilerId(compilerSetting._2());
        }
        state.setDeploymentServerId(getDeployementNodeId());

        //Save Contract Settings panel
        contractSettingsPanel.updateDataToState(state, algoPkgJsonService);

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

        contractSettingsPanel = new ContractSettingsConfigurationPanel();

    }

    public ValidationInfo doValidate() {
        return contractSettingsPanel.doValidate();
    }
}
