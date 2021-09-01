package com.bloxbean.algodea.idea.nodeint.service;

import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.BlockResponse;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class NetworkService extends AlgoBaseService {

    public NetworkService(Project project, LogListener logListener) throws DeploymentTargetNotConfigured {
        super(project, logListener);
    }

    public NetworkService(@NotNull NodeInfo nodeInfo, LogListener logListener) {
        super(nodeInfo, logListener);
    }

    public TransactionParametersResponse getNetworkInfo() throws Exception {
        Response<TransactionParametersResponse> transactionParametersResponse
                = client.TransactionParams().execute(getHeaders()._1(), getHeaders()._2());
        if(!transactionParametersResponse.isSuccessful()) {
            printErrorMessage("Unable to get Transaction Params from the node", transactionParametersResponse);
            return null;
        }

        return transactionParametersResponse.body();
    }

    public String getBlockTimeStamp(Long round) throws Exception {
        Response<BlockResponse> blockResponse = client.GetBlock(round).execute(getHeaders()._1(), getHeaders()._2());

        if(!blockResponse.isSuccessful()) {
            printErrorMessage("Unable to get block for round : " + round, blockResponse);
            return null;
        }

        Object value = blockResponse.body().block.get("ts");
        if(value == null)
            return null;
        else {
            return String.valueOf(value);
        }
    }
}
