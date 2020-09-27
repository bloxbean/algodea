package com.bloxbean.algorand.idea.module.toolwindow;

import com.bloxbean.algorand.idea.module.AlgorandModuleType;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class AlgoToolWindowFactory implements ToolWindowFactory {
    public final static String ALGO_TOOL_WINDOW_ID = "Algorand_Tool_Window";

    @Override
    public boolean isApplicable(@NotNull Project project) {
        return isAlgorandModuleAvailable(project);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
//        TextConsoleBuilderFactory factory = TextConsoleBuilderFactory.getInstance();
//        TextConsoleBuilder builder = factory.createBuilder(project);
//        ConsoleView view = builder.getConsole();
//
//        final ContentManager contentManager = toolWindow.getContentManager();
//        Content content = contentManager
//                .getFactory()
//                .createContent(view.getComponent(), "Teal Compile", false);
//        contentManager.addContent(content);
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
