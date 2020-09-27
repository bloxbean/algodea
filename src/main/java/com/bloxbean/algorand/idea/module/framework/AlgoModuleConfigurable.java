package com.bloxbean.algorand.idea.module.framework;

import com.bloxbean.algorand.idea.module.AlgoSdkPanel;
import com.intellij.framework.addSupport.FrameworkSupportInModuleConfigurable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModifiableModelsProvider;
import com.intellij.openapi.roots.ModifiableRootModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AlgoModuleConfigurable extends FrameworkSupportInModuleConfigurable {
    public static final String ALGORAND_SDK_KEY = "ALGORAND_SDK_NAME";
    private AlgoSdkPanel myAlgoSdkPanel = new AlgoSdkPanel();

    @Nullable
    @Override
    public JComponent createComponent() {
        return myAlgoSdkPanel;
    }

    @Override
    public void addSupport(@NotNull Module module, @NotNull ModifiableRootModel model, @NotNull ModifiableModelsProvider provider) {
        module.setOption(ALGORAND_SDK_KEY, myAlgoSdkPanel.getSdkName());
    }
}
