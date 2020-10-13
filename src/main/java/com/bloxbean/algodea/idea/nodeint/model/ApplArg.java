package com.bloxbean.algodea.idea.nodeint.model;

public class ApplArg {
    private ArgType type;
    private String value;

    public ApplArg(ArgType type, String value) {
        this.type = type;
        this.value = value;
    }

    public ArgType getType() {
        return type;
    }

    public void setType(ArgType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String convertToBase64() {
        return "text-base64";
    }

    public String toString() {
        return type + ":" + value;
    }
}
