package com.bloxbean.algodea.idea.debugger.service;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

@State(
        name = "com.bloxbean.algorand.DebugConfigState",
        storages = {@Storage("algorand-debug-config.xml")}
)
public class DebugConfigState implements PersistentStateComponent<DebugConfigState.State> {

    public static DebugConfigState getInstance() {
        return ServiceManager.getService(DebugConfigState.class);
    }

    public static class State {
        public String chromeExecPath;
        public boolean autoDetectChromePath = true;
        public String debugPort;
    }

    public DebugConfigState.State state;

    @Override
    public DebugConfigState.State getState() {
        if(state == null)
            return new DebugConfigState.State();
        else
            return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    public String getChromeExecPath() {
        initializeStateIfRequired();
        return state.chromeExecPath;
    }

    public boolean isAutoDetectChromePath() {
        initializeStateIfRequired();
        return state.autoDetectChromePath;
    }

    public String getDebugPort() {
        initializeStateIfRequired();
        return state.debugPort;
    }

    public void setChromeExecPath(String path) {
        initializeStateIfRequired();
        if(path != null)
            state.chromeExecPath = path;
    }

    public void setAutoDetectChromePath(boolean flag) {
        initializeStateIfRequired();
        state.autoDetectChromePath = flag;
    }

    public void setDebugPort(String port) {
        initializeStateIfRequired();
        state.debugPort = port;
    }

    private void initializeStateIfRequired() {
        if (state == null)
            state = this.getState();
    }

}
