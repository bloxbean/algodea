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
package com.bloxbean.algorand.idea.nodeint.service;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.algod.AccountInformation;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.model.Account;
import com.bloxbean.algorand.idea.nodeint.exception.ApiCallException;
import com.bloxbean.algorand.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algorand.idea.nodeint.purestake.CustomAlgodClient;
import com.bloxbean.algorand.idea.util.JsonUtil;
import com.intellij.openapi.project.Project;
import com.twelvemonkeys.lang.StringUtil;

import java.security.NoSuchAlgorithmException;

public class AlgoAccountService extends AlgoBaseService {
    public AlgoAccountService(Project project, LogListener logListener) throws DeploymentTargetNotConfigured {
        super(project, logListener);
    }

    public Long getBalance(String address) throws Exception {
        CustomAlgodClient algodClient = getAlgodClient();
        AccountInformation accountInformation = algodClient.AccountInformation(new Address(address));
        Response<Account> accountResponse = accountInformation.execute();
        if(accountResponse.isSuccessful()) {
            Account account = accountResponse.body();
            if(account != null)
                return account.amount;
            else
                throw new ApiCallException("Unable to get the accoung balance: Response " + accountResponse);
        } else {
            logListener.error("Unable to get the accoung balance: Response " + accountResponse);
            throw new ApiCallException("Unable to get the accoung balance: Response " + accountResponse);
        }

    }

    public Account getAccount(String address) throws ApiCallException {
        try {
            Response<Account> accountResponse = client.AccountInformation(new Address(address)).execute();

            if(!accountResponse.isSuccessful()) {
                printErrorMessage("Unable to fetch account information for address : " + address, accountResponse);
                return null;
            }

            Account account = accountResponse.body();
            if(account != null) {
                return account;
            } else
                return null;
        } catch (NoSuchAlgorithmException e) {
            logListener.error("Invalid address : " + address);
            throw new ApiCallException("Invalid address : " + address);
        } catch (Exception e) {
            logListener.error("Unable to get account information for address : " + address, e);
            throw new ApiCallException("Unable to get account information for address : " + address, e);
        }
    }

    public String getAccountDump(String address) throws ApiCallException {
        if(StringUtil.isEmpty(address)) {
            logListener.error("Account can not be fetched for empty address");
            return null;
        }

        Account account = getAccount(address);
        if(account == null) {
            logListener.error("Unable to get account information for address : " + address);
            return null;
        }

        return JsonUtil.getPrettyJson(account);
    }

}
