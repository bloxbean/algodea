package com.bloxbean.algodea.idea.debugger.action;

import com.bloxbean.algodea.idea.debugger.ui.DebugConfigDialog;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class DebugConfigAction extends AnAction {

    public DebugConfigAction() {
        super("Debugger Configuration", "Debugger Configuration", AllIcons.RunConfigurations.RemoteDebug);
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DebugConfigDialog dialog = new DebugConfigDialog();
        boolean ok = dialog.showAndGet();

        if(!ok) {
            return;
        } else {
            dialog.storeDebugConfig();
        }
    }

    @Override
    public boolean isDumbAware() {
        return true;
    }
}
