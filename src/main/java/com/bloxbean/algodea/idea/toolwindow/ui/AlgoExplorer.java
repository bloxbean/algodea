package com.bloxbean.algodea.idea.toolwindow.ui;

import com.bloxbean.algodea.idea.account.action.AccountListAction;
import com.bloxbean.algodea.idea.account.action.MultisigAccountListAction;
import com.bloxbean.algodea.idea.atomic.action.AtomicTransferAction;
import com.bloxbean.algodea.idea.configuration.action.*;
import com.bloxbean.algodea.idea.configuration.model.AlgoLocalSDK;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algodea.idea.core.messaging.AlgoNodeChangeNotifier;
import com.bloxbean.algodea.idea.core.messaging.AlgoProjectNodeConfigChangeNotifier;
import com.bloxbean.algodea.idea.core.messaging.AlgoSDKChangeNotifier;
import com.bloxbean.algodea.idea.toolwindow.AlgoExplorerTreeStructure;
import com.bloxbean.algodea.idea.toolwindow.AlgoSDKDescriptor;
import com.bloxbean.algodea.idea.toolwindow.AlgoServerNodeDescriptor;
import com.bloxbean.algodea.idea.toolwindow.action.SetCompilerTargetAction;
import com.bloxbean.algodea.idea.toolwindow.action.SetDeploymentTargetAction;
import com.bloxbean.algodea.idea.transaction.action.TransferAction;
import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.tree.AsyncTreeModel;
import com.intellij.ui.tree.StructureTreeModel;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;

public class AlgoExplorer extends SimpleToolWindowPanel implements DataProvider, Disposable {
    private final static String ALGO_EXPLORER_POPUP = "AlgoExplorerPopup";
    private Project myProject;
    private Tree myTree;
    private StructureTreeModel myTreeModel;
    private AlgoExplorerTreeStructure myTreeStructure;

//    private final TreeExpander myTreeExpander = new DefaultTreeExpander(() -> myTree) {
//        @Override
//        protected boolean isEnabled(@NotNull JTree tree) {
//            return true;
//        }
//    };

    public AlgoExplorer(Project project) {
        super(true, true);

//        setTransferHandler(new MyTransferHandler());
        myProject = project;

        myTreeStructure = new AlgoExplorerTreeStructure(project);
//       // myTreeStructure.setFilteredTargets(AntConfigurationBase.getInstance(project).isFilterTargets());
        final StructureTreeModel treeModel = new StructureTreeModel<>(myTreeStructure, this);
        myTreeModel = treeModel;
        myTree = new Tree(new AsyncTreeModel(treeModel, this));
        myTree.setRootVisible(true);
        myTree.setShowsRootHandles(true);
        myTree.setCellRenderer(new NodeRenderer());

        setToolbar(createToolbarPanel());
        setContent(ScrollPaneFactory.createScrollPane(myTree));
        ToolTipManager.sharedInstance().registerComponent(myTree);

        attachListeners();
        attachHandlers();
    }

    private void attachHandlers() {
        myTree.addMouseListener(new PopupHandler() {
            @Override
            public void invokePopup(Component comp, int x, int y) {
                popupInvoked(comp, x, y);
            }
        });
    }

    private void attachListeners() {
        ApplicationManager.getApplication().getMessageBus().connect(this)
                .subscribe(AlgoNodeChangeNotifier.CHANGE_ALGO_REMOTE_NODES_TOPIC, new AlgoNodeChangeNotifier() {

                    @Override
                    public void nodeAdded(NodeInfo nodeInfo) {
                        myTreeModel.invalidate();
                    }

                    @Override
                    public void nodeUpdated(NodeInfo nodeInfo) {
                        myTreeModel.invalidate();
                    }

                    @Override
                    public void nodeDeleted(NodeInfo nodeInfo) {
                        myTreeModel.invalidate();
                    }
                });

        ApplicationManager.getApplication().getMessageBus().connect(this)
                .subscribe(AlgoSDKChangeNotifier.CHANGE_ALGO_LOCAL_SDK_TOPIC, new AlgoSDKChangeNotifier() {
                    @Override
                    public void sdkAdded(AlgoLocalSDK sdk) {
                        myTreeModel.invalidate();
                    }

                    @Override
                    public void sdkUpdated(AlgoLocalSDK sdk) {
                        myTreeModel.invalidate();
                    }

                    @Override
                    public void sdkDeleted(AlgoLocalSDK sdk) {
                        myTreeModel.invalidate();
                    }
                });

        ApplicationManager.getApplication().getMessageBus().connect(this)
                .subscribe(AlgoProjectNodeConfigChangeNotifier.CHANGE_ALGO_PROJECT_NODES_CONFIG_TOPIC, new AlgoProjectNodeConfigChangeNotifier() {
                    @Override
                    public void configUpdated(Project project) {
                        if(myProject != null && myProject.equals(project)) {
                            myTreeStructure.updateSelectedNodeInfos();
                            myTreeModel.invalidate();
                        }

                    }
                });

    }

    private void popupInvoked(Component comp, int x, int y) {
        Object userObject = null;
        final TreePath path = myTree.getSelectionPath();
        if (path != null) {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
            if (node != null) {
                userObject = node.getUserObject();
            }
        }

        final DefaultActionGroup group = new DefaultActionGroup();
        if(userObject instanceof AlgoServerNodeDescriptor) {
            AlgoServerNodeDescriptor algoServerNodeDescriptor = ((AlgoServerNodeDescriptor)userObject);
            NodeInfo nodeInfo = algoServerNodeDescriptor.getNode();

            group.add(new UpdateAlgoNodeAction(nodeInfo));
            if(!algoServerNodeDescriptor.isCompilerTarget()) {
                group.add(new SetCompilerTargetAction(AlgoProjectState.ConfigType.remote_node, nodeInfo.getId()));
            }

            if(!algoServerNodeDescriptor.isDeploymentTarget()) {
                group.add(new SetDeploymentTargetAction(nodeInfo.getId()));
            }
            group.add(new DeleteAlgoNodeAction(nodeInfo));

        } else if(userObject instanceof AlgoExplorerTreeStructure.AlgoNodesNodeDescriptor) {
            group.add(ActionManager.getInstance().getAction(CreateNewServerAction.ACTION_ID));
        } else if(userObject instanceof AlgoExplorerTreeStructure.AlgoSDKNodeDescriptor) {
            group.add(ActionManager.getInstance().getAction(CreateNewLocalSDKAction.ACTION_ID));
        } else if(userObject instanceof AlgoSDKDescriptor) {
            AlgoSDKDescriptor algoSDKDescriptor = ((AlgoSDKDescriptor)userObject);
            AlgoLocalSDK sdk = algoSDKDescriptor.getSdk();

            group.add(new UpdateAlgoSDKAction(sdk));

            if(!algoSDKDescriptor.isCompilerTarget()) {
                group.add(new SetCompilerTargetAction(AlgoProjectState.ConfigType.local_sdk, sdk.getId()));
            }
            group.add(new DeleteAlgoSDKAction(sdk));
        }

        final ActionPopupMenu popupMenu = ActionManager.getInstance().createActionPopupMenu(AlgoExplorer.ALGO_EXPLORER_POPUP, group);
        popupMenu.getComponent().show(comp, x, y);
    }

    @Override
    public @Nullable JComponent getToolbar() {
        return createToolbarPanel();
    }

    private JPanel createToolbarPanel() {
        final DefaultActionGroup group = new DefaultActionGroup();
        group.add(new CreateNewServerAction());
        group.add(new CreateNewLocalSDKAction());
        group.add(new Separator());
        group.add(new AccountListAction());
        group.add(new MultisigAccountListAction());
        group.add(new Separator());
        group.add(new TransferAction());
        group.add(new AtomicTransferAction());

        final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar(ActionPlaces.ANT_EXPLORER_TOOLBAR, group, true);
        return JBUI.Panels.simplePanel(actionToolBar.getComponent());
    }


    @Override
    public void dispose() {
        myTree = null;
        myProject = null;
    }
}
