package com.bloxbean.algorand.idea;

import com.bloxbean.algorand.idea.module.AlgorandModuleType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import org.jetbrains.annotations.NotNull;

public abstract class BaseTEALAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        final Module module = LangDataKeys.MODULE.getData(e.getDataContext());
        final ModuleType moduleType = module == null ? null : ModuleType.get(module);
        final boolean isAlgorandModule = moduleType instanceof AlgorandModuleType;
    }
}
