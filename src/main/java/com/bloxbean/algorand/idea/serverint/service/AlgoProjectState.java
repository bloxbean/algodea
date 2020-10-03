package com.bloxbean.algorand.idea.serverint.service;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

@State(
        name = "com.bloxbean.algorand.AlgoProjectState",
        reloadable = true,
        storages = {@Storage("algorand-project.xml")}
)
public class AlgoProjectState implements PersistentStateComponent<AlgoProjectState.State> {
    private static final Logger LOG = Logger.getInstance(AlgoProjectState.class);

    public enum ConfigType { local_sdk, remote_node}

    public static AlgoProjectState getInstance(Project project) {
        return ServiceManager.getService(project, AlgoProjectState.class);
    }

    public static class State {
        private ConfigType compilerType;
        private String compilerId;
        private String deploymentServerId;

        //Stateful contracts
        private boolean supportStatefulContract;
        private String approvalProgramName;
        private String clearStateProgramName;

        public ConfigType getCompilerType() {
            return compilerType;
        }

        public void setCompilerType(ConfigType compilerType) {
            this.compilerType = compilerType;
        }

        public String getCompilerId() {
            return compilerId;
        }

        public void setCompilerId(String compilerId) {
            this.compilerId = compilerId;
        }

        public String getDeploymentServerId() {
            return deploymentServerId;
        }

        public void setDeploymentServerId(String deploymentServerId) {
            this.deploymentServerId = deploymentServerId;
        }

        public boolean isSupportStatefulContract() {
            return supportStatefulContract;
        }

        public void setSupportStatefulContract(boolean supportStatefulContract) {
            this.supportStatefulContract = supportStatefulContract;
        }

        public String getApprovalProgramName() {
            return approvalProgramName;
        }

        public void setApprovalProgramName(String approvalProgramName) {
            this.approvalProgramName = approvalProgramName;
        }

        public String getClearStateProgramName() {
            return clearStateProgramName;
        }

        public void setClearStateProgramName(String clearStateProgramName) {
            this.clearStateProgramName = clearStateProgramName;
        }

    }

    private State state = new State();

    public State getState() {
        return state;
    }

    public void loadState(State state) {
        this.state = state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
