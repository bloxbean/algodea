package com.bloxbean.algodea.idea.contracts.action;

import com.bloxbean.algodea.idea.contracts.ui.NewStatefulContractDialog;
import com.bloxbean.algodea.idea.core.action.AlgoBaseAction;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class NewStatefulContractAction extends AlgoBaseAction {

    public NewStatefulContractAction() {
        super(AllIcons.General.Settings);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        if(isAlgoProject(e)) {
            e.getPresentation().setEnabled(true);
        } else {
            e.getPresentation().setVisible(false);
            e.getPresentation().setEnabled(false);
        }

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        if(project == null)
            return;

        NewStatefulContractDialog dialog = new NewStatefulContractDialog(project);
        boolean ok = dialog.showAndGet();
        if(!ok)
            return;

        dialog.save();
    }
}
