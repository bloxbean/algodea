package com.bloxbean.algorand.idea.module.framework;

import com.bloxbean.algorand.idea.module.AlgorandModuleType;
import com.intellij.framework.FrameworkTypeEx;
import com.intellij.framework.addSupport.FrameworkSupportInModuleConfigurable;
import com.intellij.framework.addSupport.FrameworkSupportInModuleProvider;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportModel;

import com.intellij.openapi.module.ModuleType;
import org.jetbrains.annotations.NotNull;

public class AlgoModuleProvider extends FrameworkSupportInModuleProvider {
    public AlgoModuleProvider() {

    }

    @NotNull
    @Override
    public FrameworkTypeEx getFrameworkType() {
        return FrameworkTypeEx.EP_NAME.findExtension(AlgoStatefulFrameworkType.class);
    }

    @NotNull
    @Override
    public FrameworkSupportInModuleConfigurable createConfigurable(@NotNull FrameworkSupportModel model) {
        return new AlgoModuleConfigurable();
    }

    @Override
    public boolean isEnabledForModuleType(@NotNull ModuleType moduleType) {
        return moduleType instanceof AlgorandModuleType;
    }

}
