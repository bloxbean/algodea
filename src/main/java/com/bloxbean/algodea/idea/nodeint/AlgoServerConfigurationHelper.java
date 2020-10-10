package com.bloxbean.algodea.idea.nodeint;

import com.bloxbean.algodea.idea.configuration.model.AlgoLocalSDK;
import com.bloxbean.algodea.idea.configuration.service.AlgoLocalSDKState;
import com.bloxbean.algodea.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algodea.idea.configuration.service.NodeConfigState;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;

import java.util.List;

public class AlgoServerConfigurationHelper {

    public static NodeInfo getDeploymentNodeInfo(Project project) {
        AlgoProjectState projectState = AlgoProjectState.getInstance(project);
        String deploymentNodeId = projectState.getState().getDeploymentServerId();

        if(!StringUtil.isEmpty(deploymentNodeId)) {
            List<NodeInfo> nodes = NodeConfigState.getInstance().getNodes();
            for(NodeInfo node: nodes) {
                if(deploymentNodeId.equals(node.getId())) {
                    return node;
                }
            }
        }

        return null;
    }

    public static AlgoLocalSDK getCompilerLocalSDK(Project project) {
        AlgoProjectState projectState = AlgoProjectState.getInstance(project);
        AlgoProjectState.ConfigType compilerType = projectState.getState().getCompilerType();
        String compilerId = projectState.getState().getCompilerId();

        if(AlgoProjectState.ConfigType.local_sdk == compilerType
                &&!StringUtil.isEmpty(compilerId)) {
            List<AlgoLocalSDK> sdks = AlgoLocalSDKState.getInstance().getLocalSDKs();
            for(AlgoLocalSDK sdk: sdks) {
                if(compilerId.equals(sdk.getId())) {
                    return sdk;
                }
            }
        }

        return null;
    }

    public static NodeInfo getCompilerNodeInfo(Project project) {
        AlgoProjectState projectState = AlgoProjectState.getInstance(project);
        AlgoProjectState.ConfigType compilerType = projectState.getState().getCompilerType();
        String compilerId = projectState.getState().getCompilerId();

        if(AlgoProjectState.ConfigType.remote_node == compilerType
                &&!StringUtil.isEmpty(compilerId)) {
            List<NodeInfo> nodes = NodeConfigState.getInstance().getNodes();
            for(NodeInfo node: nodes) {
                if(compilerId.equals(node.getId())) {
                    return node;
                }
            }
        }

        return null;
    }
}
