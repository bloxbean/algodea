package com.bloxbean.algorand.idea.serverint.model;

public class AlgoLocalSDK {
    private String id;
    private String name;
    private String home;
    private String version;

    public AlgoLocalSDK() {
        id = "";
        name = "";
    }

    public AlgoLocalSDK(String id, String name, String home, String version) {
        this.id = id;
        this.name = name;
        this.home = home;
        this.version = version;
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

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String toString() {
        return name;
    }

    public String print() {
        return "AlgoLocalSDK{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", home='" + home + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
