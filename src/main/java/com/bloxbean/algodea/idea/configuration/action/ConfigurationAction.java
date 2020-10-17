package com.bloxbean.algodea.idea.configuration.action;

import com.bloxbean.algodea.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algodea.idea.configuration.ui.AlgoProjectConfigurationDialog;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ConfigurationAction extends AnAction {
    public static final String ACTION_ID = ConfigurationAction.class.getName();

    public ConfigurationAction() {
        super(AllIcons.Ide.Notification.Gear);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(project == null)
            return;

        AlgoProjectState algoProjectState = AlgoProjectState.getInstance(project);
        if(algoProjectState == null) {
            IdeaUtil.showNotification(project, "Algorand project configuration",
                    "Unable to configure Algorand project", NotificationType.ERROR, null);
            return;
        }

        AlgoProjectConfigurationDialog dialog = new AlgoProjectConfigurationDialog(project);

        boolean ok = dialog.showAndGet();
        if(ok) {
            dialog.save(project);
        } else {

        }

    }
}
