package com.bloxbean.algodea.idea.nodeint.util;

import com.bloxbean.algodea.idea.nodeint.model.Network;

public class NetworkHelper {

    private static NetworkHelper instance;

    private NetworkHelper() {

    }

    public NetworkHelper getInstance() {
        if(instance == null) {
            instance = new NetworkHelper();
        }

        return instance;
    }

}
