package com.bloxbean.algorand.idea.nodeint;

import com.bloxbean.algorand.idea.nodeint.purestake.BloxBeanAlgodClient;

public class AlgoConnectionFactory {

    private String apiUrl;
    private String apiKey;

    public AlgoConnectionFactory(String apiUrl, String apiKey) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    public BloxBeanAlgodClient connect() {
        BloxBeanAlgodClient algodClient = new BloxBeanAlgodClient(apiUrl, 443, apiKey);

//        client.addDefaultHeader("x-api-key", apiKey);
        return algodClient;
    }

}
