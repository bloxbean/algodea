package com.bloxbean.algodea.idea.pkg.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlgoPackageJson {
    @JsonProperty("name")
    private String name;

    @JsonProperty("version")
    private String version;

    @JsonProperty("stateful_contracts")
    private List<StatefulContract> statefulContractList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<StatefulContract> getStatefulContractList() {
        return statefulContractList;
    }

    public void setStatefulContractList(List<StatefulContract> statefulContractList) {
        this.statefulContractList = statefulContractList;
    }

    public void addStatefulContractList(StatefulContract contract) {
        if(this.statefulContractList == null)
            this.statefulContractList = new ArrayList<>();

        this.statefulContractList.add(contract);
    }

    public static class StatefulContract {
        @JsonProperty("name")
        private String name;

        @JsonProperty("approval_program")
        private String approvalProgram;

        @JsonProperty("clear_state_program")
        private String clearStateProgram;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getApprovalProgram() {
            return approvalProgram;
        }

        public void setApprovalProgram(String approvalProgram) {
            this.approvalProgram = approvalProgram;
        }

        public String getClearStateProgram() {
            return clearStateProgram;
        }

        public void setClearStateProgram(String clearStateProgram) {
            this.clearStateProgram = clearStateProgram;
        }
    }
}