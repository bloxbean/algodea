package com.bloxbean.algorand.idea.toolwindow;

import com.bloxbean.algorand.idea.configuration.model.AlgoLocalSDK;
import com.bloxbean.algorand.idea.configuration.model.NodeInfo;
import com.bloxbean.algorand.idea.configuration.service.AlgoLocalSDKState;
import com.bloxbean.algorand.idea.configuration.service.NodeConfigState;
import com.bloxbean.algorand.idea.toolwindow.model.AlgoLocalSDKRoot;
import com.bloxbean.algorand.idea.toolwindow.model.AlgoNodesRoot;
import com.bloxbean.algorand.idea.toolwindow.model.AlgoRoot;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.treeView.AbstractTreeStructure;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class AlgoExplorerTreeStructure extends AbstractTreeStructure {

    private final Project project;
    private AlgoRoot root;
    private AlgoNodesRoot nodesRoot;
    private AlgoLocalSDKRoot sdkRoot;

    public AlgoExplorerTreeStructure(Project project) {
        this.project = project;
    }
    @Override
    public @NotNull Object getRootElement() {
        if(root == null)
            root = new AlgoRoot();

        if(nodesRoot == null)
            nodesRoot = new AlgoNodesRoot();
        if(sdkRoot == null)
            sdkRoot = new AlgoLocalSDKRoot();

        return root;
    }

    @Override
    public @NotNull Object[] getChildElements(@NotNull Object element) {
        if(element instanceof AlgoRoot) {
            return new Object[] {sdkRoot, nodesRoot};
        } else if(element instanceof AlgoNodesRoot) { //Show Available Algorand nodes
            List<NodeInfo> nodes = NodeConfigState.getInstance().getNodes();
            if(nodes != null && nodes.size() > 0)
                return nodes.toArray();
        } else if(element instanceof AlgoLocalSDKRoot) { //Show Available Algorand local sdks
            List<AlgoLocalSDK> sdks = AlgoLocalSDKState.getInstance().getLocalSDKs();
            if(sdks != null && sdks.size() > 0)
                return sdks.toArray();
        }
        return new Object[0];
    }

    @Override
    public @Nullable Object getParentElement(@NotNull Object element) {
        if (element instanceof AlgoNodesRoot) {
            return null;
        } else if(element instanceof AlgoLocalSDKRoot || element instanceof AlgoNodesRoot) {
            return root;
        } else if (element instanceof NodeInfo) {
            return nodesRoot;
        } else if (element instanceof AlgoLocalSDK) {
            return sdkRoot;
        } else
            return null;
    }

    @Override
    public @NotNull NodeDescriptor createDescriptor(@NotNull Object element, @Nullable NodeDescriptor parentDescriptor) {
        if(element == root) {
            return new RootNodeDescriptor(project, parentDescriptor);
        }

        if(element == sdkRoot) {
            return new AlgoSDKNodeDescriptor(project, parentDescriptor);
        }

        if(element == nodesRoot) {
            return new AlgoNodesNodeDescriptor(project, parentDescriptor);
        }

        if(element instanceof NodeInfo) {
            return new AlgoServerNodeDescriptor(project, parentDescriptor, (NodeInfo)element);
        }

        if(element instanceof AlgoLocalSDK) {
            return new AlgoSDKDescriptor(project, parentDescriptor, (AlgoLocalSDK) element);
        }

        return null;
    }

    @Override
    public void commit() {

    }

    @Override
    public boolean hasSomethingToCommit() {
        return false;
    }

    public AlgoRoot getRootNode() {
        return root;
    }

    public AlgoNodesRoot getNodesRoot() {
        return nodesRoot;
    }

    public AlgoLocalSDKRoot getLocalSDKRoot() {
        return sdkRoot;
    }

    public final class RootNodeDescriptor extends NodeDescriptor {

        public RootNodeDescriptor(@Nullable Project project, @Nullable NodeDescriptor parentDescriptor) {
            super(project, parentDescriptor);
            myName = "Algorand";
            myClosedIcon = AllIcons.Nodes.Folder;
        }

        @Override
        public boolean update() {
            return false;
        }

        @Override
        public Object getElement() {
            return root;
        }
    }

    public final class AlgoNodesNodeDescriptor extends NodeDescriptor {

        public AlgoNodesNodeDescriptor(@Nullable Project project, @Nullable NodeDescriptor parentDescriptor) {
            super(project, parentDescriptor);
            myName = "Nodes";
            myClosedIcon = AllIcons.Nodes.Folder;
            myColor = Color.blue;
        }

        @Override
        public boolean update() {
            return false;
        }

        @Override
        public Object getElement() {
            return nodesRoot;
        }
    }

    public final class AlgoSDKNodeDescriptor extends NodeDescriptor {

        public AlgoSDKNodeDescriptor(@Nullable Project project, @Nullable NodeDescriptor parentDescriptor) {
            super(project, parentDescriptor);
            myName = "Local SDKs";
            myClosedIcon = AllIcons.Nodes.Folder;
            myColor = Color.blue;
        }

        @Override
        public boolean update() {
            return false;
        }

        @Override
        public Object getElement() {
            return sdkRoot;
        }
    }
}
