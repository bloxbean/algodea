package com.bloxbean.algodea.idea.configuration.service;

import com.bloxbean.algodea.idea.configuration.model.AlgoLocalSDK;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.configuration.ui.LocalSDKDialog;
import com.bloxbean.algodea.idea.configuration.ui.RemoteNodeConfigDialog;
import com.bloxbean.algodea.idea.core.messaging.AlgoNodeChangeNotifier;
import com.bloxbean.algodea.idea.core.messaging.AlgoSDKChangeNotifier;
import com.intellij.openapi.application.ApplicationManager;
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

            if (existingNodeInfo == null) {
                nodeInfo.setId(UUID.randomUUID().toString());
            } else {
                nodeInfo.setId(existingNodeInfo.getId());
            }
            nodeInfo.setName(remoteNodeConfigDialog.getServerName());
            nodeInfo.setNodeAPIUrl(remoteNodeConfigDialog.getNodeApiUrl());
            nodeInfo.setApiKey(remoteNodeConfigDialog.getApiKey());
            nodeInfo.setIndexerAPIUrl(remoteNodeConfigDialog.getIndexerApiUrl());

            if (existingNodeInfo == null) {
                stateService.addNode(nodeInfo);
                AlgoNodeChangeNotifier algoNodeChangeNotifier = ApplicationManager.getApplication().getMessageBus().syncPublisher(AlgoNodeChangeNotifier.CHANGE_ALGO_REMOTE_NODES_TOPIC);
                algoNodeChangeNotifier.nodeAdded(nodeInfo);
            } else {
                NodeInfo updatedNodeInfo = stateService.updateNodeInfo(nodeInfo);
                AlgoNodeChangeNotifier algoNodeChangeNotifier = ApplicationManager.getApplication().getMessageBus().syncPublisher(AlgoNodeChangeNotifier.CHANGE_ALGO_REMOTE_NODES_TOPIC);
                algoNodeChangeNotifier.nodeUpdated(updatedNodeInfo);
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
        if (ok) {
            //save and return
            AlgoLocalSDK localSDK = new AlgoLocalSDK();

            if (existingLocalSdk == null) {
                localSDK.setId(UUID.randomUUID().toString());
            } else {
                localSDK.setId(existingLocalSdk.getId());
            }

            localSDK.setHome(localSDKDialog.getHome());
            localSDK.setName(localSDKDialog.getName());
            localSDK.setVersion(localSDKDialog.getVersion());

            if (existingLocalSdk == null) {
                stateService.addLocalSdk(localSDK);

                AlgoSDKChangeNotifier algoSDKChangeNotifier = ApplicationManager.getApplication().getMessageBus().syncPublisher(AlgoSDKChangeNotifier.CHANGE_ALGO_LOCAL_SDK_TOPIC);
                algoSDKChangeNotifier.sdkAdded(localSDK);
            } else {
                stateService.updateLocalSdk(localSDK);

                AlgoSDKChangeNotifier algoSDKChangeNotifier = ApplicationManager.getApplication().getMessageBus().syncPublisher(AlgoSDKChangeNotifier.CHANGE_ALGO_LOCAL_SDK_TOPIC);
                algoSDKChangeNotifier.sdkUpdated(localSDK);
            }

            return localSDK;
        } else {
            return null;
        }
    }

    public static boolean deleteAlgoNodeConfiguration(NodeInfo node) {
        NodeConfigState nodeStateService = NodeConfigState.getInstance();

        if (nodeStateService == null)
            return false;

        nodeStateService.removeNode(node);
        AlgoNodeChangeNotifier algoNodeChangeNotifier = ApplicationManager.getApplication().getMessageBus().syncPublisher(AlgoNodeChangeNotifier.CHANGE_ALGO_REMOTE_NODES_TOPIC);
        algoNodeChangeNotifier.nodeDeleted(node);

        return true;
    }

    public static void deleteAlgoLocalSDKConfiguration(AlgoLocalSDK sdk) {
        AlgoLocalSDKState stateService = AlgoLocalSDKState.getInstance();

        if(stateService == null)
            return;

        stateService.removeSdk(sdk);

        AlgoSDKChangeNotifier algoSDKChangeNotifier = ApplicationManager.getApplication().getMessageBus().syncPublisher(AlgoSDKChangeNotifier.CHANGE_ALGO_LOCAL_SDK_TOPIC);
        algoSDKChangeNotifier.sdkDeleted(sdk);

    }
}
