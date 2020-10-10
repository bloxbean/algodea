/*
 * Copyright (c) 2020 BloxBean Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bloxbean.algodea.idea.nodeint.service;

import com.algorand.algosdk.v2.client.common.Response;
import com.bloxbean.algodea.idea.nodeint.purestake.CustomAlgodClient;
import com.bloxbean.algodea.idea.configuration.model.NodeInfo;
import com.bloxbean.algodea.idea.nodeint.AlgoConnectionFactory;
import com.bloxbean.algodea.idea.nodeint.AlgoServerConfigurationHelper;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

public class AlgoBaseService {
    private final static Logger LOG = Logger.getInstance(AlgoBaseService.class);

    protected Project project;
    protected AlgoConnectionFactory algoConnectionFactory;
    protected LogListener logListener;
    protected CustomAlgodClient client;

    public AlgoBaseService(Project project) throws DeploymentTargetNotConfigured {
        this(project, new LogListener() {
            @Override
            public void info(String msg) {
                LOG.info(msg);
            }

            @Override
            public void error(String msg) {
                LOG.error(msg);
            }

            @Override
            public void warn(String msg) {
                LOG.warn(msg);
            }
        });
    }

    public AlgoBaseService(Project project, LogListener logListener) throws DeploymentTargetNotConfigured {
        NodeInfo nodeInfo = AlgoServerConfigurationHelper.getDeploymentNodeInfo(project);
        if(nodeInfo == null)
            throw new DeploymentTargetNotConfigured("No deployment node found");
        algoConnectionFactory
                = new AlgoConnectionFactory(nodeInfo.getNodeAPIUrl(), nodeInfo.getApiKey());
        this.logListener = logListener;

        this.client = algoConnectionFactory.connect();
    }

    public CustomAlgodClient getAlgodClient() {
        return algoConnectionFactory.connect();
    }

    public void printErrorMessage(String message, Response response) {
        if(!response.isSuccessful()) {
            logListener.error(message);
            logListener.error("Failure code    : " + response.code());
            logListener.error("Failure message : " + response.message());
        }
    }
}