package com.bloxbean.algorand.idea.toolwindow;

import com.bloxbean.algorand.idea.module.AlgorandModuleType;
import com.bloxbean.algorand.idea.toolwindow.ui.AlgoExplorer;
import com.bloxbean.algorand.idea.toolwindow.ui.AlgoToolServicePanel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class AlgoExplorerToolWindowFactory implements ToolWindowFactory {
    public final static String ALGO_WINDOW_ID = "Algorand Service";

    @Override
    public boolean isApplicable(@NotNull Project project) {
        return isAlgorandModuleAvailable(project);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
//        AlgoToolServicePanel algoToolServicePanel = new AlgoToolServicePanel();
//
//        final ContentManager contentManager = toolWindow.getContentManager();
//                Content content = contentManager
//                .getFactory()
//                .createContent(algoToolServicePanel, "Nodes", false);
//        contentManager.addContent(content);
        AlgoExplorer explorer = new AlgoExplorer(project);
        ContentManager contentManager = toolWindow.getContentManager();
        Content content = contentManager.getFactory().createContent(explorer, null, false);
        contentManager.addContent(content);
        //toolWindow.setHelpId(HelpID.ANT);
        content.setDisposer(explorer);
    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {

    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return isAlgorandModuleAvailable(project);
    }

    private boolean isAlgorandModuleAvailable(@NotNull Project project) {
        if(project == null)
            return false;

        Collection<Module> modules = ModuleUtil.getModulesOfType(project, AlgorandModuleType.getInstance());
        if(modules != null || modules.size() > 0)
            return true;
        else
            return false;
    }
}
