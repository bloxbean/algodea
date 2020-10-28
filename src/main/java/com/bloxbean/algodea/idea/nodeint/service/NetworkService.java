package com.bloxbean.algodea.idea.nodeint.service;

import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;
import com.bloxbean.algodea.idea.common.Tuple;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import org.jetbrains.annotations.NotNull;

public class NetworkService extends AlgoBaseService {

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
}
