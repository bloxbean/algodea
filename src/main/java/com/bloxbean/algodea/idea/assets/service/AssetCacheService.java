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
package com.bloxbean.algodea.idea.assets.service;

import com.bloxbean.algodea.idea.assets.model.AssetMeta;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.util.text.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

@State(name="algorand-assets-cache", reloadable = true, storages = @com.intellij.openapi.components.Storage("algorand-assets-cache.xml"))
public class AssetCacheService implements PersistentStateComponent<AssetCacheService.State> {
    private static int MAX_ASSETS_TO_STORE = 10;

    public static AssetCacheService getInstance() {
        return ServiceManager.getService(AssetCacheService.class);
    }

    public static class State {
        public Map<String, List<String>> genesisHashToAssetIdMap = new HashMap<>();
        public Map<String, String> assetIdMap = new HashMap<>();
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

    public List<String> getAssetIds(String genesisHash) {
        initializeStateIfRequired();

        List<String>  assetIds = state.genesisHashToAssetIdMap.get(genesisHash);
        if(assetIds == null) {
            return Collections.EMPTY_LIST;
        }

        List<String> appIdsList = assetIds.parallelStream().collect(Collectors.toList());
        Collections.reverse(appIdsList);
        return appIdsList;
    }

    public List<AssetMeta> getAssets(String genesisHash) {
        initializeStateIfRequired();

        List<String> assetIds = getAssetIds(genesisHash);
        if(assetIds == null) return Collections.EMPTY_LIST;

        return assetIds.stream().map(aId -> {
            String name = getAssetNameForAssetId(aId);
            return new AssetMeta(aId, name);
        }).collect(Collectors.toList());
    }

    public String getAssetNameForAssetId(String assetId) {
        initializeStateIfRequired();

        if(assetId == null) return null;

        return state.assetIdMap.get(assetId);
    }

    public void addAssetId(String genesisHash, String assetName, String assetId) {

        initializeStateIfRequired();
        if(StringUtil.isEmpty(genesisHash)) {
            return;
        }

        List<String> assetIds = state.genesisHashToAssetIdMap.get(genesisHash);
        if(assetIds == null)
            assetIds = new ArrayList(MAX_ASSETS_TO_STORE);

        if(assetIds.size() > MAX_ASSETS_TO_STORE) {
            String oldestAppId = assetIds.get(0);
            assetIds.remove(0);

            if(!StringUtil.isEmpty(oldestAppId)) //cleanup contractMap also
                state.assetIdMap.get(oldestAppId);
        }

        if(!assetIds.contains(assetId))
            assetIds.add(assetId);

        state.genesisHashToAssetIdMap.put(genesisHash, assetIds);

        if(!StringUtil.isEmpty(assetName)) {
            state.assetIdMap.put(assetId, assetName);
        }
    }

    public void loadState(State state) {
        this.state = state;
    }

}
