package com.bloxbean.algodea.idea.toolwindow.action;

import com.bloxbean.algodea.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algodea.idea.configuration.service.ConfiguraionHelperService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class SetCompilerTargetAction extends AnAction {
    private String compilerId;
    private AlgoProjectState.ConfigType compilerType;

    public SetCompilerTargetAction(AlgoProjectState.ConfigType compilerType, String compilerId) {
        super("Use for compilation", "Use this for compilation", AllIcons.Actions.Compile);
        this.compilerId = compilerId;
        this.compilerType = compilerType;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(project == null) return;

        ConfiguraionHelperService.setCompilerId(project, compilerType, compilerId);
    }
}
