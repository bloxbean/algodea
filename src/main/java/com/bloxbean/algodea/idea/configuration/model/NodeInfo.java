package com.bloxbean.algodea.idea.configuration.model;

import java.util.Objects;

public class NodeInfo {
    private String id;
    private String name;
    private String nodeAPIUrl;
    private String indexerAPIUrl;

    private String apiKey;
    private String encryptedApiKey;

    public NodeInfo() {
        this.id = "";
        this.name = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNodeAPIUrl() {
        return nodeAPIUrl;
    }

    public void setNodeAPIUrl(String nodeAPIUrl) {
        this.nodeAPIUrl = nodeAPIUrl;
    }

    public String getIndexerAPIUrl() {
        return indexerAPIUrl;
    }

    public void setIndexerAPIUrl(String indexerAPIUrl) {
        this.indexerAPIUrl = indexerAPIUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getEncryptedApiKey() {
        return encryptedApiKey;
    }

    public void setEncryptedApiKey(String encryptedApiKey) {
        this.encryptedApiKey = encryptedApiKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeInfo nodeInfo = (NodeInfo) o;
        return id.equals(nodeInfo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }

    public String print() {
        return "NodeInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", nodeAPIUrl='" + nodeAPIUrl + '\'' +
                ", indexerAPIUrl='" + indexerAPIUrl + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", encryptedApiKey='" + encryptedApiKey + '\'' +
                '}';
    }

    public void updateValues(NodeInfo updatedInfo) {
        if(updatedInfo == null) return;

        this.setName(updatedInfo.getName());
        this.setNodeAPIUrl(updatedInfo.getNodeAPIUrl());
        this.setIndexerAPIUrl(updatedInfo.getIndexerAPIUrl());
        this.setApiKey(updatedInfo.getApiKey());
    }
}

