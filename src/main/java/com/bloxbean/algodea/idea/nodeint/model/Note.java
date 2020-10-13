package com.bloxbean.algodea.idea.nodeint.model;

public class Note {
    private ArgType type;
    private String value;

    public Note(ArgType type, String value) {
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

    public String toString() {
        return type + ":" + value;
    }
}
