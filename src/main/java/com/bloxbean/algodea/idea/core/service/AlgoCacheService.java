/*
 * Copyright (c) 2020 BloxBean Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bloxbean.algodea.idea.core.service;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@State(name="algorand-idea-cache", reloadable = true, storages = @com.intellij.openapi.components.Storage("algorand-idea-cache.xml"))
public class AlgoCacheService implements PersistentStateComponent<AlgoCacheService.State> {
    private static int MAX_CACHE_ENTRY = 40;
    private static int MAX_APP_IDS_TO_STORE = 10;

    public static AlgoCacheService getInstance(Project project) {
        if(project == null) return null;
        return project.getComponent(AlgoCacheService.class);
    }

    public static class State {
        public Map<String, Map<String, String>> vars = new HashMap<>(); //VAR_TMPL_* values

        public Map<String, String> optInArgs = new HashMap<>();
        public Map<String, String> createArgs = new HashMap<>();
        public Map<String, String> callArgs = new HashMap<>();

        public String sfCreatorAccount;
        public String contract;

        public Map<String, List<String>> appIdMap = new HashMap<>();
        public Map<String, String> appIdContractMap = new HashMap<>();
//        public List<String> appIds = new ArrayList<>();
    }

    public State state;

    public State getState() {
        if(state == null)
            return new State();
        else
            return state;
    }

    private void initializeStateIfRequired() {
        if (state == null)
            state = this.getState();
    }

    public Map<String, String> getVarsFromCache(@NotNull String tealFile) {
        if(tealFile == null)
            return Collections.EMPTY_MAP;

        return getState().vars.get(tealFile);
    }

    public void updateVarsToCache(@NotNull String tealFile, Map<String, String> vars) {
        if(tealFile == null)
            return;

        initializeStateIfRequired();

        if(state.vars.size() >= MAX_CACHE_ENTRY) {
            state.vars.clear();
        }

        state.vars.put(tealFile, vars);
    }

    public String getContract() {
        initializeStateIfRequired();
        return state.contract;
    }

    public void setLastContract(String contractName) {
        initializeStateIfRequired();
        state.contract = contractName;
    }

    public void setSfCreatorAccount(String creatorAccount) {
        initializeStateIfRequired();
        state.sfCreatorAccount = creatorAccount;
    }

    public String getSfCreatorAccount() {
        initializeStateIfRequired();
        return state.sfCreatorAccount;
    }

    public List<String> getAppIds(String deployServerId) {
        initializeStateIfRequired();

        List<String>  appIds = state.appIdMap.get(deployServerId);
        if(appIds == null) {
            return Collections.EMPTY_LIST;
        }

        List<String> appIdsList = appIds.parallelStream().collect(Collectors.toList());
        Collections.reverse(appIdsList);
        return appIdsList;
    }

    public String getContractNameForAppId(String appId) {
        initializeStateIfRequired();

        if(appId == null) return null;

        return state.appIdContractMap.get(appId);
    }

    public void addAppId(String deployServerId, String contractName, String appId) {

        initializeStateIfRequired();
        if(StringUtil.isEmpty(deployServerId)) {
            return;
        }

        List<String> appIds = state.appIdMap.get(deployServerId);
        if(appIds == null)
            appIds = new ArrayList(MAX_APP_IDS_TO_STORE);

        if(appIds.size() > MAX_APP_IDS_TO_STORE) {
            String oldestAppId = appIds.get(0);
            appIds.remove(0);

            if(!StringUtil.isEmpty(oldestAppId)) //cleanup contractMap also
                state.appIdContractMap.get(oldestAppId);
        }

        if(!appIds.contains(appId))
            appIds.add(appId);

        state.appIdMap.put(deployServerId, appIds);

        if(!StringUtil.isEmpty(contractName)) {
            state.appIdContractMap.put(appId, contractName);
        }
    }

    public void loadState(State state) {
        this.state = state;
    }

}
