package com.bloxbean.algodea.idea.pkg.action;

import com.bloxbean.algodea.idea.module.AlgoModuleConstant;
import com.bloxbean.algodea.idea.pkg.AlgoPkgJsonService;
import com.bloxbean.algodea.idea.pkg.exception.PackageJsonException;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import org.jetbrains.annotations.NotNull;

public class PkgJsonReloadAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            return;

        AlgoPkgJsonService pkgJsonService = AlgoPkgJsonService.getInstance(project);
        if(pkgJsonService != null) {
            try {
                pkgJsonService.load();
                IdeaUtil.showNotification(project, "Loading " + AlgoModuleConstant.ALGO_PACKAGE_JSON, AlgoModuleConstant.ALGO_PACKAGE_JSON
                        + " re-loaded successfully", NotificationType.INFORMATION, null);
            } catch (PackageJsonException packageJsonException) {
                IdeaUtil.showNotification(project, "Loading " + AlgoModuleConstant.ALGO_PACKAGE_JSON,
                        AlgoModuleConstant.ALGO_PACKAGE_JSON + " file could not be loaded \n" + packageJsonException.getMessage()
                        , NotificationType.ERROR, null);

            }
        }
    }
}
