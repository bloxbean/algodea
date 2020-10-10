package com.bloxbean.algodea.idea.module.framework;

import com.bloxbean.algodea.idea.common.AlgoIcons;
import com.intellij.framework.FrameworkTypeEx;
import com.intellij.framework.addSupport.FrameworkSupportInModuleProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AlgoStatefulFrameworkType extends FrameworkTypeEx {
    public static final String ID = "com.bloxbean.algorand.framework.AlgorandStatefulSmartContract";

    protected AlgoStatefulFrameworkType() {
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
        return "Stateful Contract";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return AlgoIcons.ALGO_ICON;
    }
}
