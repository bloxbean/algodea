package com.bloxbean.algodea.idea.core.messaging;

import com.intellij.openapi.project.Project;
import com.intellij.util.messages.Topic;

public interface AlgoProjectNodeConfigChangeNotifier {
    Topic<AlgoProjectNodeConfigChangeNotifier> CHANGE_ALGO_PROJECT_NODES_CONFIG_TOPIC
            = Topic.create("AlgorandProjectNodeConfigurationTopic", AlgoProjectNodeConfigChangeNotifier.class);

    void configUpdated(Project project);
}
