package com.bloxbean.algodea.idea.core.action;

import com.bloxbean.algodea.idea.common.AlgoIcons;
import com.bloxbean.algodea.idea.module.AlgorandModuleType;
import com.bloxbean.algodea.idea.pkg.AlgoPkgJsonService;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

public class AlgoActionGroup extends DefaultActionGroup {
    @Override
    public void update(AnActionEvent event) {
        Project project = event.getProject();

        DataContext dataContext = event.getDataContext();
        final Module module = LangDataKeys.MODULE.getData(dataContext);

        final ModuleType moduleType = module == null ? null : ModuleType.get(module);
        boolean isAlgorandModule = moduleType instanceof AlgorandModuleType;

        //Try to check if algo-package.json file available.
        //For non-Algorand modules
        if(!isAlgorandModule) {
            AlgoPkgJsonService pkgJsonService = AlgoPkgJsonService.getInstance(project);
            if (pkgJsonService != null)
                isAlgorandModule = pkgJsonService.isAlgoProject();
        }

        if(isAlgorandModule) {
            event.getPresentation().setVisible(true);
            event.getPresentation().setIcon(AlgoIcons.ALGO_ICON);
        } else {
            event.getPresentation().setVisible(false);
            event.getPresentation().setIcon(AlgoIcons.ALGO_ICON);
        }
    }
}
