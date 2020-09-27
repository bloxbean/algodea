package com.bloxbean.algorand.idea.module.framework;

import com.bloxbean.algorand.idea.common.AlgoIcons;
import com.intellij.framework.FrameworkTypeEx;
import com.intellij.framework.addSupport.FrameworkSupportInModuleProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AlgoFrameworkType extends FrameworkTypeEx {
    public static final String ID = "Algorand Framework";

    protected AlgoFrameworkType() {
        super(ID);
    }

    @NotNull
    @Override
    public FrameworkSupportInModuleProvider createProvider() {
        return new AlgoModuleProvider();
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "Algorand Framework";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return AlgoIcons.ALGO_ICON;
    }
}
