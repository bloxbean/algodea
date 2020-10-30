package com.bloxbean.algodea.idea.configuration.ui;

import com.bloxbean.algodea.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algodea.idea.common.Tuple;
import com.bloxbean.algodea.idea.core.messaging.AlgoProjectNodeConfigChangeNotifier;
import com.bloxbean.algodea.idea.core.messaging.AlgoSDKChangeNotifier;
import com.bloxbean.algodea.idea.pkg.AlgoPkgJsonService;
import com.bloxbean.algodea.idea.pkg.model.AlgoPackageJson;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

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

            if(algoProjectConfiguration.isConfigChanged()) {
                AlgoProjectNodeConfigChangeNotifier algoProjectNodeConfigChangeNotifier
                        = ApplicationManager.getApplication().getMessageBus().syncPublisher(AlgoProjectNodeConfigChangeNotifier.CHANGE_ALGO_PROJECT_NODES_CONFIG_TOPIC);
                algoProjectNodeConfigChangeNotifier.configUpdated(project);
            }
        } else {
            IdeaUtil.showNotification(project, "Algorand node configuration",
                    "Unable to save Algorand configuration for the project !!!", NotificationType.ERROR, null);
        }

    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        return algoProjectConfiguration.doValidate();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return algoProjectConfiguration.getMainPanel();
    }

}
