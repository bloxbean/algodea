package com.bloxbean.algorand.idea.configuration.service;

import com.bloxbean.algorand.idea.configuration.model.AlgoLocalSDK;
import com.bloxbean.algorand.idea.configuration.model.NodeInfo;
import com.bloxbean.algorand.idea.configuration.ui.LocalSDKDialog;
import com.bloxbean.algorand.idea.configuration.ui.RemoteNodeConfigDialog;
import com.intellij.openapi.project.Project;

import java.util.UUID;

public class ConfiguraionHelperService {
    public static NodeInfo createOrUpdateNewNodeConfiguration(Project project, NodeInfo existingNodeInfo) {
        NodeConfigState stateService = NodeConfigState.getInstance();
        System.out.println(stateService.getNodes());

        RemoteNodeConfigDialog remoteNodeConfigDialog = new RemoteNodeConfigDialog(project, existingNodeInfo);
        boolean ok = remoteNodeConfigDialog.showAndGet();
        if (ok) {
//            NodeConfigState = stateService.getState();

            NodeInfo nodeInfo = new NodeInfo();

            if(existingNodeInfo == null) {
                nodeInfo.setId(UUID.randomUUID().toString());
            } else {
                nodeInfo.setId(existingNodeInfo.getId());
            }
            nodeInfo.setName(remoteNodeConfigDialog.getServerName());
            nodeInfo.setNodeAPIUrl(remoteNodeConfigDialog.getNodeApiUrl());
            nodeInfo.setApiKey(remoteNodeConfigDialog.getApiKey());
            nodeInfo.setIndexerAPIUrl(remoteNodeConfigDialog.getIndexerApiUrl());

            if(existingNodeInfo == null) {
                stateService.addNode(nodeInfo);
            } else {
                stateService.updateNodeInfo(existingNodeInfo);
            }
            return nodeInfo;
        } else {
            return null;
        }
    }

    public static AlgoLocalSDK createOrUpdateLocalSDKConfiguration(Project project, AlgoLocalSDK existingLocalSdk) {
        AlgoLocalSDKState stateService = AlgoLocalSDKState.getInstance();
        LocalSDKDialog localSDKDialog = new LocalSDKDialog(project, existingLocalSdk);
        boolean ok = localSDKDialog.showAndGet();
        if(ok) {
            //save and return
            AlgoLocalSDK localSDK = new AlgoLocalSDK();

            if(existingLocalSdk == null) {
                localSDK.setId(UUID.randomUUID().toString());
            } else {
                localSDK.setId(existingLocalSdk.getId());
            }

            localSDK.setHome(localSDKDialog.getHome());
            localSDK.setName(localSDKDialog.getName());
            localSDK.setVersion(localSDKDialog.getVersion());

            if(existingLocalSdk == null) {
                stateService.addLocalSdk(localSDK);
            } else {
                stateService.updateLocalSdk(existingLocalSdk);
            }

            return localSDK;
        } else {
            return null;
        }
    }
}
