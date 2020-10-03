package com.bloxbean.algorand.idea.configuration.service;

import com.bloxbean.algorand.idea.configuration.model.AlgoLocalSDK;
import com.bloxbean.algorand.idea.configuration.model.NodeInfo;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(
        name = "com.bloxbean.algorand.NodeConfigState",
        storages = {@Storage("algorand-node-config.xml")}
)
final public class NodeConfigState implements PersistentStateComponent<Element> {
    private static final Logger LOG = Logger.getInstance(NodeConfigState.class);

    public static NodeConfigState getInstance() {
        return ServiceManager.getService(NodeConfigState.class);
    }

    List<NodeInfo> nodes;

    public NodeConfigState() {
        this.nodes = new ArrayList<>();
    }

    @Nullable
    @Override
    public Element getState() {
        Element state = new Element("nodes");

        for(NodeInfo node: nodes) {
            Element entry = new Element("node");
            entry.setAttribute("id", node.getId());
            entry.setAttribute("name", StringUtil.notNullize(node.getName()));
            entry.setAttribute("nodeApiUrl", StringUtil.notNullize(node.getNodeAPIUrl()));
            entry.setAttribute("indexerApiUrl", StringUtil.notNullize(node.getIndexerAPIUrl()));
            entry.setAttribute("apiKey", StringUtil.notNullize(node.getApiKey()));
            entry.setAttribute("encryptedApiKey", StringUtil.notNullize(node.getEncryptedApiKey()));
            
            state.addContent(entry);
        }
        
        return state;
    }

    @Override
    public void loadState(@NotNull Element elm) {
        List<NodeInfo> list = new ArrayList<>();

        for (Element child : elm.getChildren("node")) {
            String id = child.getAttributeValue("id");
            String name = child.getAttributeValue("name");
            String nodeApiUrl = child.getAttributeValue("nodeApiUrl");
            String indexerApiUrl = child.getAttributeValue("indexerApiUrl");
            String apiKey = child.getAttributeValue("apiKey");
            String encryptedApiKey = child.getAttributeValue("encryptedApiKey");

            NodeInfo nodeInfo = new NodeInfo();
            nodeInfo.setId(id);
            nodeInfo.setName(name);
            nodeInfo.setNodeAPIUrl(nodeApiUrl);
            nodeInfo.setIndexerAPIUrl(indexerApiUrl);
            nodeInfo.setApiKey(apiKey);
            nodeInfo.setEncryptedApiKey(encryptedApiKey);

            list.add(nodeInfo);
        }

        setNodes(list);
    }

    public List<NodeInfo> getNodes() {
        return nodes;
    }

    public void addNode(NodeInfo nodeInfo) {
        nodes.add(nodeInfo);
    }

    private void setNodes(List<NodeInfo> list) {
        nodes = list;
    }

    public void updateNodeInfo(NodeInfo updatedInfo) {
        for(NodeInfo nd: nodes) {
            if(nd.getId() != null && nd.getId().equals(updatedInfo.getId())) {
                nd.updateValues(updatedInfo);
                break;
            }
        }
    }

}
