package com.bloxbean.algodea.idea.toolwindow;

import com.bloxbean.algodea.idea.module.AlgorandModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class AlgoToolWindowFactory implements ToolWindowFactory {
    public final static String ALGO_WINDOW_ID = "Algorand";

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
