/*
 * Copyright (c) 2020 BloxBean Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bloxbean.algorand.idea.toolwindow;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;

public class AlgoConsole {
    private final static Logger LOG = Logger.getInstance(AlgoConsole.class);
    public final static String CONSOLE_VIEW = "Console";

    private static ConsoleView view;

    private Project project;

    public static AlgoConsole getConsole(Project project) {
        if(project == null)
            return new AlgoConsole(project);

        AlgoConsole console = project.getService(AlgoConsole.class);
        if(console == null)
            return new AlgoConsole(project);
        else
            return console;
    }

    public AlgoConsole(Project project) {
        this.project = project;
    }

    private ConsoleView createAlgorandConsoleView(String title) {

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

    public void clear() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                if(view != null)
                    view.clear();

            }
        });
    }

    public  ConsoleView getView() {
        if(view == null) {
            view = createAlgorandConsoleView(CONSOLE_VIEW);
        }
        return view;
    }

    public  void clearAndshow() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                if(view != null)
                    view.clear();

                Project project = ProjectManager.getInstance().getDefaultProject();
                if(project == null) return;
                ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(AlgoToolWindowFactory.ALGO_WINDOW_ID);

                if(toolWindow != null && !toolWindow.isAvailable()) {
                    toolWindow.setAvailable(true);
                }

                if(toolWindow != null && !toolWindow.isVisible()) {
                    toolWindow.show(null);
                }
            }
        });

    }

    public void showInfoMessage(String message) {
        showMessage(message , ConsoleViewContentType.NORMAL_OUTPUT);
    }

    public void showSuccessMessage(String message) {
        showMessage(message , ConsoleViewContentType.LOG_INFO_OUTPUT);
    }

    public void showErrorMessage(String message) {
        showMessage(message, ConsoleViewContentType.ERROR_OUTPUT);
    }

    public void showWarningMessage(String message) {
        showMessage(message, ConsoleViewContentType.LOG_WARNING_OUTPUT);
    }

    private void showMessage(String message, ConsoleViewContentType type) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                if(view == null) {
                    view = createAlgorandConsoleView(CONSOLE_VIEW);
                }

                if(view == null) {
                    LOG.error("Console view could not be created.");
                    return;
                }

                view.print(message + "\n", type);
            }
        });

    }


}
