package com.bloxbean.algodea.idea.debugger.action;

import com.bloxbean.algodea.idea.debugger.service.DebugService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class StopDebuggerAction extends AnAction {

    public StopDebuggerAction() {
        super("Stop Tealdbg", "Stop TEAL Debugger", AllIcons.Actions.Suspend);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        if (DebugService.isDebuggerRunning()) {
            e.getPresentation().setEnabled(true);
        } else {
            e.getPresentation().setVisible(false);
            e.getPresentation().setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DebugService.stopDebugger();
    }
}
