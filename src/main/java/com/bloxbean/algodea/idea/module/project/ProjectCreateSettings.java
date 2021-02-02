package com.bloxbean.algodea.idea.module.project;

public class ProjectCreateSettings {
    public String contractName;
    public String approvalProgram;
    public String clearStateProgram;

    public ProjectCreateSettings(String contractName, String approvalProgram, String clearStateProgram) {
        this.contractName = contractName;
        this.approvalProgram = approvalProgram;
        this.clearStateProgram = clearStateProgram;
    }
}