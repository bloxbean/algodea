package com.bloxbean.algorand.idea.configuration.service;

import com.bloxbean.algorand.idea.configuration.model.AlgoLocalSDK;
import com.bloxbean.algorand.idea.configuration.model.NodeInfo;
import com.bloxbean.algorand.idea.configuration.ui.LocalSDKDialog;
import com.bloxbean.algorand.idea.configuration.ui.RemoteNodeConfigDialog;
import com.intellij.openapi.project.Project;

import java.util.UUID;

public class ConfiguraionHelperService {
    public static NodeInfo createNewNodeConfiguration(Project project) {
        NodeConfigState stateService = NodeConfigState.getInstance();
        System.out.println(stateService.getNodes());

        RemoteNodeConfigDialog remoteNodeConfigDialog = new RemoteNodeConfigDialog(project);
        boolean ok = remoteNodeConfigDialog.showAndGet();
        if (ok) {
//            NodeConfigState = stateService.getState();

            NodeInfo nodeInfo = new NodeInfo();
            nodeInfo.setId(UUID.randomUUID().toString());
            nodeInfo.setName(remoteNodeConfigDialog.getServerName());
            nodeInfo.setNodeAPIUrl(remoteNodeConfigDialog.getNodeApiUrl());
            nodeInfo.setApiKey(remoteNodeConfigDialog.getApiKey());
            nodeInfo.setIndexerAPIUrl(remoteNodeConfigDialog.getIndexerApiUrl());

            stateService.addNode(nodeInfo);
            return nodeInfo;
        } else {
            return null;
        }
    }

    public static AlgoLocalSDK createNewLocalSDKConfiguration(Project project) {
        AlgoLocalSDKState stateService = AlgoLocalSDKState.getInstance();
        LocalSDKDialog localSDKDialog = new LocalSDKDialog(project);
        boolean ok = localSDKDialog.showAndGet();
        if(ok) {
            //save and return
            AlgoLocalSDK localSDK = new AlgoLocalSDK();
            localSDK.setId(UUID.randomUUID().toString());
            localSDK.setHome(localSDKDialog.getHome());
            localSDK.setName(localSDKDialog.getName());
            localSDK.setVersion(localSDKDialog.getVersion());

            stateService.addLocalSdk(localSDK);

            return localSDK;
        } else {
            return null;
        }
    }
}
