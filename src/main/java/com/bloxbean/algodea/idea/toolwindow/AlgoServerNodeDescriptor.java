package com.bloxbean.algodea.idea.toolwindow;

import com.bloxbean.algodea.idea.common.AlgoIcons;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.JBColor;

public class AlgoServerNodeDescriptor extends NodeDescriptor {

    private NodeInfo node;
    private boolean isCompilerTarget;
    private boolean isDeploymentTarget;

    public AlgoServerNodeDescriptor(final Project project, final NodeDescriptor parentDescriptor, NodeInfo node, String compilerNodeId, String deploymentNodeId) {
        super(project, parentDescriptor);
        this.node = node;

        myName = node.getName() + " [" + StringUtil.trimLog(node.getNodeAPIUrl(), 40) + "]";
        myColor = JBColor.blue;

        if(node.getId() != null) {
            if (!node.getId().equals(compilerNodeId) && !node.getId().equals(deploymentNodeId)){
                myClosedIcon = AlgoIcons.NODE;
            } else if (node.getId().equals(compilerNodeId) && node.getId().equals(deploymentNodeId)){
                myClosedIcon = AlgoIcons.NODE_COMPILE_DEPLOY;
                isCompilerTarget = true;
                isDeploymentTarget = true;
            } else if(node.getId().equals(compilerNodeId)) {
                myClosedIcon = AlgoIcons.NODE_COMPILE;
                isCompilerTarget = true;
            } else if(node.getId().equals(deploymentNodeId)) {
                myClosedIcon = AlgoIcons.NODE_DEPLOY;
                isDeploymentTarget = true;
            }
        } else {
            myClosedIcon = AlgoIcons.NODE;
        }
    }

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public Object getElement() {
        return node;
    }

    public NodeInfo getNode() {
        return node;
    }

    public boolean isCompilerTarget() {
        return isCompilerTarget;
    }

    public void setCompilerTarget(boolean compilerTarget) {
        isCompilerTarget = compilerTarget;
    }

    public boolean isDeploymentTarget() {
        return isDeploymentTarget;
    }

    public void setDeploymentTarget(boolean deploymentTarget) {
        isDeploymentTarget = deploymentTarget;
    }
}
