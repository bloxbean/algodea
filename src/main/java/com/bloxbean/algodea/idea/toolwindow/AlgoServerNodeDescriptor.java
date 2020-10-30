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
            } else if(node.getId().equals(compilerNodeId)) {
                myClosedIcon = AlgoIcons.NODE_COMPILE;
            } else if(node.getId().equals(deploymentNodeId)) {
                myClosedIcon = AlgoIcons.NODE_DEPLOY;
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

}
