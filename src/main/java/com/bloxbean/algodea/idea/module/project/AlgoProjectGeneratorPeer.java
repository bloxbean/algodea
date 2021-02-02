package com.bloxbean.algodea.idea.module.project;

import com.bloxbean.algodea.idea.module.ui.StatefulContractPanel;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.platform.GeneratorPeerImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AlgoProjectGeneratorPeer extends GeneratorPeerImpl<ProjectCreateSettings> {

    StatefulContractPanel statefulContractPanel;
    SettingsListener settingsListener;

    public AlgoProjectGeneratorPeer() {
        statefulContractPanel = new StatefulContractPanel(true);
        statefulContractPanel.addSettingChangeListener(new StatefulContractPanel.SettingChangeListener() {
            @Override
            public void settingsChanged() {
                settingsListener.stateChanged(true);
            }
        });
    }

    @Override
    public @NotNull JComponent getComponent(@NotNull TextFieldWithBrowseButton myLocationField, @NotNull Runnable checkValid) {
        return super.getComponent(myLocationField, checkValid);
    }

    @Override
    public @NotNull JComponent getComponent() {
        return statefulContractPanel.getMainPanel();
    }

    @NotNull
    @Override
    public ProjectCreateSettings getSettings() {
        if(statefulContractPanel.isCreateStatefulContract()) {
            return new ProjectCreateSettings(statefulContractPanel.getStatefulContractName(), statefulContractPanel.getApprovalProgram(),
                    statefulContractPanel.getClearStateProgram());
        } else {
            return new ProjectCreateSettings(null, null, null);
        }
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        return statefulContractPanel.doValidate();
    }

    @Override
    public void addSettingsListener(@NotNull SettingsListener listener) {
        super.addSettingsListener(listener);
        this.settingsListener = listener;
    }
}
