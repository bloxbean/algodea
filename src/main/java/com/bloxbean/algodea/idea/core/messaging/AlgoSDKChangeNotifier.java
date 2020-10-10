package com.bloxbean.algodea.idea.core.messaging;

import com.bloxbean.algodea.idea.configuration.model.AlgoLocalSDK;
import com.intellij.util.messages.Topic;

public interface AlgoSDKChangeNotifier {
    Topic<AlgoSDKChangeNotifier> CHANGE_ALGO_LOCAL_SDK_TOPIC = Topic.create("AlgorandLocalSDKTopic", AlgoSDKChangeNotifier.class);

    void sdkAdded(AlgoLocalSDK sdk);
    void sdkUpdated(AlgoLocalSDK sdk);
    void sdkDeleted(AlgoLocalSDK sdk);
}
