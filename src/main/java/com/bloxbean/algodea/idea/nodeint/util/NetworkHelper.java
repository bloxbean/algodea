package com.bloxbean.algodea.idea.nodeint.util;

import com.intellij.openapi.util.text.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class NetworkHelper {

    private static NetworkHelper instance;
    private Map<String, String> genesisHashTOExplorerUrlMap;

    private NetworkHelper() {
        genesisHashTOExplorerUrlMap = new HashMap<>();
        genesisHashTOExplorerUrlMap.put("wGHE2Pwdvd7S12BL5FaOP20EGYesN73ktiC1qzkkit8=", "https://algoexplorer.io");
        genesisHashTOExplorerUrlMap.put("SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=", "https://testnet.algoexplorer.io");
        genesisHashTOExplorerUrlMap.put("mFgazF+2uRS1tMiL9dsj01hJGySEmPN28B/TjjvpVW0=", "https://betanet.algoexplorer.io");
    }

    public static NetworkHelper getInstance() {
        if(instance == null) {
            instance = new NetworkHelper();
        }

        return instance;
    }

    public String getExplorerBaseUrl(String genesisHash) {
        if(StringUtil.isEmpty(genesisHash))
            return null;
        return genesisHashTOExplorerUrlMap.get(genesisHash);
    }

    public String getTxnHashUrl(String genesisHash, String txnHash) {
        String url = getExplorerBaseUrl(genesisHash);
        if(StringUtil.isEmpty(url)) return null;

        return url + "/tx/" + txnHash;
    }

    public String getAssetUrl(String genesisHash, String assetId) {
        String url = getExplorerBaseUrl(genesisHash);
        if(StringUtil.isEmpty(url)) return null;

        return url + "/asset/" + assetId;
    }

}
