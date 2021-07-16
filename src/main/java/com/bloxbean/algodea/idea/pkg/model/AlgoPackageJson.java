package com.bloxbean.algodea.idea.pkg.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellij.openapi.util.text.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
        if(statefulContractList == null)
            statefulContractList = new ArrayList<>();
        return statefulContractList;
    }

    public void setStatefulContractList(List<StatefulContract> statefulContractList) {
        this.statefulContractList = statefulContractList;
    }

    public void addStatefulContract(StatefulContract contract) {
        if(this.statefulContractList == null)
            this.statefulContractList = new ArrayList<>();

        if(StringUtil.isEmpty(contract.getName())) {
            contract.setName(StringUtil.trimLog(UUID.randomUUID().toString(), 8));
        }

        int index = this.statefulContractList.indexOf(contract);
        if(index == -1) {
            this.statefulContractList.add(contract);
        } else {
            this.statefulContractList.remove(index);
            this.statefulContractList.add(index, contract);
        }

    }

    @JsonIgnore
    public StatefulContract getStatefulContractByName(String name) {
        if(name == null || this.statefulContractList == null) return null;

        for(StatefulContract contract: this.statefulContractList) {
            if(name.equals(contract.getName())) {
                return contract;
            }
        }

        return null;
    }

    @JsonIgnore
    public StatefulContract getFirstStatefulContract() {
        if(this.statefulContractList == null || this.statefulContractList.size() == 0)
            return null;

        return this.statefulContractList.get(0);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatefulContract {
        @JsonProperty("name")
        private String name;

        @JsonProperty("approval_program")
        private String approvalProgram;

        @JsonProperty("clear_state_program")
        private String clearStateProgram;

        @JsonProperty("global-byteslices")
        private int globalByteSlices;

        @JsonProperty("global-ints")
        private int globalInts;

        @JsonProperty("local-byteslices")
        private int localByteSlices;

        @JsonProperty("local-ints")
        private int localInts;

        @JsonProperty("extra-pages")
        private int extraPages;

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
            if(!StringUtil.isEmpty(approvalProgram)) {
                approvalProgram = approvalProgram.replace('\\', '/');
            }

            this.approvalProgram = approvalProgram;
        }

        public String getClearStateProgram() {
            return clearStateProgram;
        }

        public void setClearStateProgram(String clearStateProgram) {
            if(!StringUtil.isEmpty(clearStateProgram)) {
                clearStateProgram = clearStateProgram.replace('\\', '/');
            }

            this.clearStateProgram = clearStateProgram;
        }

        public int getGlobalByteSlices() {
            return globalByteSlices;
        }

        public void setGlobalByteSlices(int globalByteSlices) {
            this.globalByteSlices = globalByteSlices;
        }

        public int getGlobalInts() {
            return globalInts;
        }

        public void setGlobalInts(int globalInts) {
            this.globalInts = globalInts;
        }

        public int getLocalByteSlices() {
            return localByteSlices;
        }

        public void setLocalByteSlices(int localByteSlices) {
            this.localByteSlices = localByteSlices;
        }

        public int getLocalInts() {
            return localInts;
        }

        public void setLocalInts(int localInts) {
            this.localInts = localInts;
        }

        public int getExtraPages() {
            return extraPages;
        }

        public void setExtraPages(int extraPages) {
            this.extraPages = extraPages;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StatefulContract that = (StatefulContract) o;
            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
}
