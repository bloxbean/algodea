/*
 * Copyright (c) 2022 BloxBean Project
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

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.common.Response;
import com.algorand.algosdk.v2.client.indexer.LookupAccountByID;
import com.algorand.algosdk.v2.client.model.*;
import com.bloxbean.algodea.idea.nodeint.exception.ApiCallException;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.AccountAsset;
import com.bloxbean.algodea.idea.util.JsonUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.twelvemonkeys.lang.StringUtil;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlgoAccountService extends AlgoBaseService {
    private final static Logger LOG = Logger.getInstance(AlgoAccountService.class);

    public AlgoAccountService(Project project, LogListener logListener) throws DeploymentTargetNotConfigured {
        super(project, logListener);
    }

    public Long getBalance(String address) throws Exception {
        Account account = getAccount(address);
        if (account != null)
            return account.amount;
        else
            throw new ApiCallException("Unable to get the account balance");
    }

    public Account getAccount(String address) throws ApiCallException {
        try {
            if(indexerClient == null) {
                Response<Account> accountResponse = client.AccountInformation(new Address(address)).execute(getHeaders()._1(), getHeaders()._2());

                if (!accountResponse.isSuccessful()) {
                    printErrorMessage("Unable to fetch account information for address : " + address, accountResponse);
                    return null;
                }

                Account account = accountResponse.body();
                if (account != null) {
                    return account;
                } else
                    return null;
            } else {
                logListener.info("Fetching balance from Indexer Url ...");
                LookupAccountByID lookupAccountByID = indexerClient.lookupAccountByID(new Address(address));
                Response<AccountResponse> accountResponseResponse =
                        lookupAccountByID.execute(getHeaders()._1(), getHeaders()._2());

                if (accountResponseResponse.isSuccessful() && accountResponseResponse.body() != null) {
                    Account account = accountResponseResponse.body().account;
                    return account;
                } else {
                    printErrorMessage("Unable to get the account balance: Response ", accountResponseResponse);
                    return null;
                }
            }
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

    public List<Account> getAccounts(List<String> addresses) throws ApiCallException {

        if(addresses == null || addresses.size() == 0)
            return Collections.EMPTY_LIST;

        List<Account> accounts = new ArrayList<>();
        for(String address: addresses) {
            Account account = getAccount(address);
            accounts.add(account);
        }


        return accounts;
    }

    public List<AccountAsset> getAccountAssets(String address) throws ApiCallException {
        if(StringUtil.isEmpty(address)) {
            logListener.error("Account can not be fetched for empty address");
            return null;
        }

        Account account = getAccount(address);
        if(account == null) {
            logListener.error("Unable to get account information for address : " + address);
            return Collections.EMPTY_LIST;
        }

        List<AssetHolding> assetHoldings = account.assets;
        if(assetHoldings == null || assetHoldings.size() == 0)
            return Collections.EMPTY_LIST;

        List<AccountAsset> accountAssets = new ArrayList<>();
        for(AssetHolding assetHolding: assetHoldings) {
            AccountAsset accountAsset = new AccountAsset();
            accountAsset.setAssetId(assetHolding.assetId);
            accountAsset.setAmount(assetHolding.amount);

            try {
                Asset asset = getAsset(assetHolding.assetId);
                accountAsset.setAssetName(asset.params.name);
                accountAsset.setAssetUnit(asset.params.unitName);
                accountAsset.setDecimals(asset.params.decimals);
            } catch (Exception e) {
                if(LOG.isDebugEnabled()) {
                    LOG.warn(e);
                }
            }

            accountAssets.add(accountAsset);
        }

        return accountAssets;
    }

    public List<AccountAsset> getAccountAssets(Account account) throws ApiCallException {
        if(account == null) {
            logListener.error("Account can not null");
            return Collections.EMPTY_LIST;
        }

        List<AssetHolding> assetHoldings = account.assets;
        if(assetHoldings == null || assetHoldings.size() == 0)
            return Collections.EMPTY_LIST;

        List<AccountAsset> accountAssets = new ArrayList<>();
        for(AssetHolding assetHolding: assetHoldings) {
            AccountAsset accountAsset = new AccountAsset();
            accountAsset.setAssetId(assetHolding.assetId);
            accountAsset.setAmount(assetHolding.amount);

            try {
                Asset asset = getAsset(assetHolding.assetId);
                accountAsset.setAssetName(asset.params.name);
                accountAsset.setAssetUnit(asset.params.unitName);
                accountAsset.setDecimals(asset.params.decimals);
            } catch (Exception e) {
                if(LOG.isDebugEnabled()) {
                    LOG.warn(e);
                }
            }

            accountAssets.add(accountAsset);
        }

        return accountAssets;
    }


    public Asset getAsset(Long assetId) throws Exception {
        if(assetId == null) {
            logListener.error("Asset id cannot be null");
            return null;
        }

        Asset asset = null;
        if(indexerClient == null) {
            Response<Asset> assetResponse = client.GetAssetByID(assetId).execute(getHeaders()._1(), getHeaders()._2());
            if (!assetResponse.isSuccessful()) {
                printErrorMessage("Reading asset info failed", assetResponse);
                return null;
            }

            asset = assetResponse.body();

            logListener.info(JsonUtil.getPrettyJson(asset));
            logListener.info("\n");
        } else {
            logListener.info("Fetching asset detail from Indexer Url ...");
            Response<AssetResponse> response = indexerClient.lookupAssetByID(assetId).execute(getHeaders()._1(), getHeaders()._2());
            if(!response.isSuccessful()) {
                printErrorMessage("Reading asset info failed", response);
                return null;
            }

            AssetResponse assetResponse = response.body();
            asset = assetResponse.asset;

            logListener.info(JsonUtil.getPrettyJson(asset));
            logListener.info("\n");
        }

        return asset;
    }
}
