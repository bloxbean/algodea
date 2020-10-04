package com.bloxbean.algorand.idea.core.messaging;

import com.bloxbean.algorand.idea.configuration.model.AlgoLocalSDK;
import com.bloxbean.algorand.idea.configuration.model.NodeInfo;
import com.intellij.util.messages.Topic;

public interface AlgoSDKChangeNotifier {
    Topic<AlgoSDKChangeNotifier> CHANGE_ALGO_LOCAL_SDK_TOPIC = Topic.create("AlgorandLocalSDKTopic", AlgoSDKChangeNotifier.class);

    void sdkAdded(AlgoLocalSDK sdk);
    void sdkUpdated(AlgoLocalSDK sdk);
    void sdkDeleted(AlgoLocalSDK sdk);
}
