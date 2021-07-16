package com.bloxbean.algodea.idea.core.action;

import com.bloxbean.algodea.idea.configuration.action.ConfigurationAction;
import com.bloxbean.algodea.idea.module.AlgorandModuleType;
import com.bloxbean.algodea.idea.pkg.AlgoPkgJsonService;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;

import javax.swing.*;

public abstract class AlgoBaseAction extends AnAction {

    public AlgoBaseAction() {
        super();
    }

    public AlgoBaseAction(Icon icon) {
        super(icon);
    }

    public AlgoBaseAction(String text, String description, Icon icon) {
        super(text, description, icon);
    }

    public void warnDeploymentTargetNotConfigured(Project project, String actionTitle) {
        IdeaUtil.showNotification(project, actionTitle, "Algorand Node for deployment node is not configured. Click here to configure.",
                NotificationType.ERROR, ConfigurationAction.ACTION_ID);
    }

    protected boolean isAlgoProject(AnActionEvent e) {
        Project project = e.getProject();
        final Module module = LangDataKeys.MODULE.getData(e.getDataContext());
        if(project != null && module != null) {
            final ModuleType moduleType = module == null ? null : ModuleType.get(module);
            boolean isAlgorandModule = moduleType instanceof AlgorandModuleType;

            //Try to check if algo-package.json file available.
            //For non-Algorand modules
            if (!isAlgorandModule) {
                AlgoPkgJsonService pkgJsonService = AlgoPkgJsonService.getInstance(project);
                if (pkgJsonService != null)
                    isAlgorandModule = pkgJsonService.isAlgoProject();
            }

            return isAlgorandModule;
        } else {
            return false;
        }
    }
}
