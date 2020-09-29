package com.bloxbean.algorand.idea.toolwindow;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;

public class AlgoConsoleMessages {
    private final static Logger LOG = Logger.getInstance(AlgoConsoleMessages.class);

    private static ConsoleView view;

    private static ConsoleView createAlgorandConsoleView(Project project, String title) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(AlgoToolWindowFactory.ALGO_WINDOW_ID);
        ConsoleView consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        Content content =
                toolWindow.getContentManager()
                        .getFactory()
                        .createContent(consoleView.getComponent(), title, false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.show();
        return consoleView;
    }

    public static void clear() {
        if(view != null)
            view.clear();
    }

    public static void clearAndshow(Project project) {
        if(view != null)
            view.clear();

        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(AlgoToolWindowFactory.ALGO_WINDOW_ID);
        if(toolWindow != null && !toolWindow.isVisible())
            toolWindow.show();;
    }

    public static void showInfoMessage(Project project, String message) {
        showMessage(project, message , ConsoleViewContentType.NORMAL_OUTPUT);
    }

    public static void showSuccessMessage(Project project, String message) {
        showMessage(project, message , ConsoleViewContentType.LOG_INFO_OUTPUT);
    }

    public static void showErrorMessage(Project project, String message) {
        showMessage(project, message, ConsoleViewContentType.ERROR_OUTPUT);
    }

    public static void showWarningMessage(Project project, String message) {
        showMessage(project, message, ConsoleViewContentType.LOG_WARNING_OUTPUT);
    }

    private static void showMessage(Project project, String message, ConsoleViewContentType type) {
        if(view == null) {
            view = createAlgorandConsoleView(project, "Console");
        }

        if(view == null) {
            LOG.error("Console view could not be created.");
            return;
        }

        view.print(message + "\n", type);
    }


}
