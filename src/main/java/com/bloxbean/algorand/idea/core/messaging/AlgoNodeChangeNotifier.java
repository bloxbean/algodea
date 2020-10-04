package com.bloxbean.algorand.idea.core.messaging;

import com.bloxbean.algorand.idea.configuration.model.NodeInfo;
import com.intellij.util.messages.Topic;

public interface AlgoNodeChangeNotifier  {
    Topic<AlgoNodeChangeNotifier> CHANGE_ALGO_REMOTE_NODES_TOPIC = Topic.create("AlgorandRemoteNodesTopic", AlgoNodeChangeNotifier.class);

    void nodeAdded(NodeInfo nodeInfo);
    void nodeUpdated(NodeInfo nodeInfo);
    void nodeDeleted(NodeInfo nodeInfo);
}
