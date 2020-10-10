package com.bloxbean.algodea.idea.toolwindow;

import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.JBColor;

public class AlgoServerNodeDescriptor extends NodeDescriptor {

    private NodeInfo node;

    public AlgoServerNodeDescriptor(final Project project, final NodeDescriptor parentDescriptor, NodeInfo node) {
        super(project, parentDescriptor);
        this.node = node;
        myName = node.getName() + " [" + StringUtil.trimLog(node.getNodeAPIUrl(), 40) + "]";
        myColor = JBColor.blue;
        myClosedIcon = AllIcons.Nodes.Services;
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
