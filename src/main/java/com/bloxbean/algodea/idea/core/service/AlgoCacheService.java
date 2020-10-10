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
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@State(name="algorand-idea-cache", reloadable = true, storages = @com.intellij.openapi.components.Storage("algorand-idea-cache.xml"))
public class AlgoCacheService implements PersistentStateComponent<AlgoCacheService.State> {
    private static int MAX_CACHE_ENTRY = 40;

    public static AlgoCacheService getInstance(Project project) {
        if(project == null) return null;
        return project.getComponent(AlgoCacheService.class);
    }

    public static class State {
        public Map<String, Map<String, String>> vars = new HashMap<>(); //VAR_TMPL_* values

        public String sfCreatorAccount;
        public int sfGlobalByteslices;
        public int sfGlobalInts;
        public int sfLocalByteslices;
        public int sfLocalInts;
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

    public void updateSfGlobalBytesInts(int globalByteslices, int globalInts, int localByteslices, int localInts) {
        initializeStateIfRequired();

        state.sfGlobalByteslices = globalByteslices;
        state.sfGlobalInts = globalInts;
        state.sfLocalByteslices = localByteslices;
        state.sfLocalInts = localInts;
    }

    public int getSfGlobalByteslices() {
        initializeStateIfRequired();

        return state.sfGlobalByteslices;
    }

    public int getSfGlobalInts() {
        initializeStateIfRequired();

        return state.sfGlobalInts;
    }

    public int getSfLocalByteslices() {
        initializeStateIfRequired();

        return state.sfLocalByteslices;
    }

    public int getSfLocalInts() {
        initializeStateIfRequired();

        return state.sfLocalInts;
    }

    public void setSfCreatorAccount(String creatorAccount) {
        initializeStateIfRequired();
        state.sfCreatorAccount = creatorAccount;
    }

    public String getSfCreatorAccount() {
        initializeStateIfRequired();
        return state.sfCreatorAccount;
    }

    public void loadState(State state) {
        this.state = state;
    }

}
