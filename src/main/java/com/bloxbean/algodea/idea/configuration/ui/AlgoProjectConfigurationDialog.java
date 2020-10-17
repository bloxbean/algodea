package com.bloxbean.algodea.idea.configuration.ui;

import com.bloxbean.algodea.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algodea.idea.common.Tuple;
import com.bloxbean.algodea.idea.pkg.AlgoPkgJsonService;
import com.bloxbean.algodea.idea.pkg.model.AlgoPackageJson;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
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

    public void save(Project project) {
        AlgoProjectState algoProjectState = AlgoProjectState.getInstance(project);
        if (algoProjectState == null) {
            IdeaUtil.showNotification(project, "Algorand node configuration",
                    "Unable to save Algorand configuration for the project", NotificationType.ERROR, null);
            return;
        }

        AlgoProjectState.State state = algoProjectState.getState();
        AlgoPkgJsonService algoPkgJsonService = AlgoPkgJsonService.getInstance(project);
        if (state != null) {
            algoProjectConfiguration.updateDataToState(state, algoPkgJsonService);
            algoProjectState.setState(state);
        } else {
            IdeaUtil.showNotification(project, "Algorand node configuration",
                    "Unable to save Algorand configuration for the project !!!", NotificationType.ERROR, null);
        }

    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return algoProjectConfiguration.getMainPanel();
    }

}
