package com.bloxbean.algodea.idea.assets.model;

import com.intellij.openapi.util.text.StringUtil;

public class AssetMeta {
    private String id;
    private String name;

    public AssetMeta(String id, String name) {
        this.id = id;
        this.name = name;
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

    public String toString() {
        if(StringUtil.isEmpty(id) && StringUtil.isEmpty(name))
            return "";

        return id + " ( " + (name != null? name: "") + " )";
    }
}
