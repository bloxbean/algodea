package com.bloxbean.algodea.idea.assets.action;

import com.bloxbean.algodea.idea.assets.ui.TransferAssetsDialog;
import com.bloxbean.algodea.idea.core.action.AlgoBaseAction;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class TransferAssetAction extends AlgoBaseAction {

    public TransferAssetAction() {
        super();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(project == null)
            return;

        AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();

        TransferAssetsDialog dialog = new TransferAssetsDialog(project);
        boolean ok = dialog.showAndGet();

    }
}
