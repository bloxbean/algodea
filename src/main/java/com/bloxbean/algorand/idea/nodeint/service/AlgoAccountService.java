package com.bloxbean.algorand.idea.nodeint.service;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.algod.AccountInformation;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.Account;
import com.bloxbean.algorand.idea.configuration.model.NodeInfo;
import com.bloxbean.algorand.idea.nodeint.AlgoConnectionFactory;
import com.bloxbean.algorand.idea.nodeint.AlgoServerConfigurationHelper;
import com.bloxbean.algorand.idea.nodeint.exception.ApiCallException;
import com.bloxbean.algorand.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algorand.idea.nodeint.purestake.BloxBeanAlgodClient;
import com.intellij.openapi.project.Project;

public class AlgoAccountService {
    AlgoConnectionFactory algoConnectionFactory;

    public AlgoAccountService(Project project) throws DeploymentTargetNotConfigured {
        NodeInfo nodeInfo = AlgoServerConfigurationHelper.getDeploymentNodeInfo(project);
        if(nodeInfo == null)
            throw new DeploymentTargetNotConfigured("No deployment node found");
        algoConnectionFactory
                = new AlgoConnectionFactory(nodeInfo.getNodeAPIUrl(), nodeInfo.getApiKey());
    }

    public Long getBalance(String address) throws Exception {
        BloxBeanAlgodClient algodClient = algoConnectionFactory.connect();
        AccountInformation accountInformation = algodClient.AccountInformation(new Address(address));
        Response<Account> accountResponse = accountInformation.execute();
        if(accountResponse.isSuccessful()) {
            Account account = accountResponse.body();
            if(account != null)
                return account.amount;
            else
                throw new ApiCallException("Unable to get the accoung balance: Response " + accountResponse);
        } else {
            throw new ApiCallException("Unable to get the accoung balance: Response " + accountResponse);
        }

    }
}
