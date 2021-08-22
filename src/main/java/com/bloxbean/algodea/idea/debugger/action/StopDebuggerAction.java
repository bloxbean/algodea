package com.bloxbean.algodea.idea.debugger.action;

import com.bloxbean.algodea.idea.debugger.service.DebugService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StopDebuggerAction extends AnAction {

    public StopDebuggerAction() {
        super("Stop Tealdbg", "Stop TEAL Debugger", AllIcons.Actions.Suspend);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        DebugService debugService = getDebugService(e);

        if (debugService != null && debugService.isDebuggerRunning()) {
            e.getPresentation().setEnabled(true);
        } else {
            e.getPresentation().setVisible(false);
            e.getPresentation().setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DebugService debugService = getDebugService(e);
        if(debugService != null) {
            debugService.stopDebugger();
        }
    }

    @Nullable
    private DebugService getDebugService(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        DebugService debugService = null;
        if(project != null) {
            debugService = ServiceManager.getService(project, DebugService.class);
        }
        return debugService;
    }
}
