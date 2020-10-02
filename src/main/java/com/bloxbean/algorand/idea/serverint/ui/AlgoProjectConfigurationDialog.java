package com.bloxbean.algorand.idea.serverint.ui;

import com.bloxbean.algorand.idea.common.Tuple;
import com.bloxbean.algorand.idea.serverint.service.AlgoProjectState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AlgoProjectConfigurationDialog extends DialogWrapper {

    private AlgoProjectConfiguration algoProjectConfiguration;

    public AlgoProjectConfigurationDialog(@Nullable Project project) {
        super(project);
        algoProjectConfiguration = new AlgoProjectConfiguration(project);
        init();
        setTitle("Algorand Project - Build / Deploy Configuration");
    }

    public Tuple<AlgoProjectState.ConfigType, String> getCompilerSdkId() {
        return algoProjectConfiguration.getCompilerSdkId();
    }

    public String getDeployementNodeId() {
        return algoProjectConfiguration.getDeployementNodeId();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return algoProjectConfiguration.getMainPanel();
    }

}
