package com.bloxbean.algorand.idea.core.service;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@State(name="algorand-idea-cache", reloadable = true, storages = @com.intellij.openapi.components.Storage("algorand-idea-cache.xml"))
public class AlgoCacheService implements PersistentStateComponent<AlgoCacheService.State> {
    private static int MAX_CACHE_ENTRY = 10;

    public static class State {
        public Map<String, Map<String, String>> vars = new HashMap<>(); //VAR_TMPL_* values
    }

    public State state;

    public State getState() {
        if(state == null)
            return new State();
        else
            return state;
    }

    public Map<String, String> getVarsFromCache(@NotNull String tealFile) {
        if(tealFile == null)
            return Collections.EMPTY_MAP;

        return getState().vars.get(tealFile);
    }

    public void updateVarsToCache(@NotNull String tealFile, Map<String, String> vars) {
        if(tealFile == null)
            return;

        if(state == null)
            state = this.getState();

        if(state.vars.size() >= MAX_CACHE_ENTRY) {
            state.vars.clear();
        }

        state.vars.put(tealFile, vars);
    }

    public void loadState(State state) {
        this.state = state;
    }

}
