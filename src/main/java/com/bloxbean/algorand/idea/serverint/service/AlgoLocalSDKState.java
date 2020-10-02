package com.bloxbean.algorand.idea.serverint.service;

import com.bloxbean.algorand.idea.serverint.model.AlgoLocalSDK;
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
        name = "com.bloxbean.algorand.AlgoLocalSDKState",
        storages = {@Storage("algorand-local-sdks.xml")}
)
public class AlgoLocalSDKState implements PersistentStateComponent<Element> {
    private static final Logger LOG = Logger.getInstance(NodeConfigState.class);

    public static AlgoLocalSDKState getInstance() {
        return ServiceManager.getService(AlgoLocalSDKState.class);
    }

    private List<AlgoLocalSDK> localSDKs;

    public AlgoLocalSDKState() {
        this.localSDKs = new ArrayList<>();
    }

    @Nullable
    @Override
    public Element getState() {
        Element state = new Element("localSdks");

        for(AlgoLocalSDK sdk: localSDKs) {
            Element entry = new Element("localSdk");
            entry.setAttribute("id", sdk.getId());
            entry.setAttribute("name", sdk.getName());
            entry.setAttribute("home", StringUtil.notNullize(sdk.getHome()));
            entry.setAttribute("version", StringUtil.notNullize(sdk.getVersion()));

            state.addContent(entry);
        }

        return state;
    }

    @Override
    public void loadState(@NotNull Element elm) {
        List<AlgoLocalSDK> list = new ArrayList<>();

        for (Element child : elm.getChildren("localSdk")) {
            String id = child.getAttributeValue("id");
            String name = child.getAttributeValue("name");
            String home = child.getAttributeValue("home");
            String version = child.getAttributeValue("version");

            AlgoLocalSDK sdk = new AlgoLocalSDK(id, name, home,version);

            list.add(sdk);
        }

        setLocalSDKs(list);
    }

    public List<AlgoLocalSDK> getLocalSDKs() {
        return localSDKs;
    }

    public void addLocalSdk(AlgoLocalSDK sdk) {
        localSDKs.add(sdk);
    }

    private void setLocalSDKs(List<AlgoLocalSDK> list) {
        localSDKs = list;
    }
}
