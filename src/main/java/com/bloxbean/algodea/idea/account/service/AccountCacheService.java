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

package com.bloxbean.algodea.idea.account.service;

import com.bloxbean.algodea.idea.account.cache.AccountCache;
import com.bloxbean.algodea.idea.account.cache.GlobalCache;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.intellij.openapi.diagnostic.Logger;

import java.io.File;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

public class AccountCacheService {
    private final static Logger log = Logger.getInstance(AccountCacheService.class);
    //private Project project;

    public AccountCacheService() {
        //this.project = project;
    }

    public List<AlgoAccount> getAccounts() {
//        if(!isContinue())
//            return Collections.EMPTY_LIST;

        try {
            GlobalCache globalCache = new GlobalCache(getAccountCacheFolder());
            AccountCache accountCache = globalCache.getAccountCache();

            if(accountCache != null) {
                List<AlgoAccount> accounts = accountCache.getAccounts();
                return accounts;
            } else {
                return Collections.EMPTY_LIST;
            }
        } catch (Exception e) {
            return Collections.EMPTY_LIST;
        }
    }

    public List<AlgoMultisigAccount> getMultisigAccounts() {
        try {
            GlobalCache globalCache = new GlobalCache(getAccountCacheFolder());
            AccountCache accountCache = globalCache.getAccountCache();

            if(accountCache != null) {
                List<AlgoMultisigAccount> accounts = accountCache.getMultisigAccounts();
                return accounts;
            } else {
                return Collections.EMPTY_LIST;
            }
        } catch (Exception e) {
            return Collections.EMPTY_LIST;
        }
    }

    public void storeAccount(AlgoAccount account) {

        GlobalCache globalCache = new GlobalCache(getAccountCacheFolder());
        AccountCache accountCache = globalCache.getAccountCache();

        accountCache.addAccount(account);
        globalCache.setAccountCache(accountCache);
    }

    public boolean deleteAccount(AlgoAccount account) {
        GlobalCache globalCache = new GlobalCache(getAccountCacheFolder());
        AccountCache accountCache = globalCache.getAccountCache();

        if(accountCache.deleteAccount(account)) {
            globalCache.setAccountCache(accountCache);
            return true;
        }

        return false;
    }

    public void storeMultisigAccount(AlgoMultisigAccount multisigAccount) {

        GlobalCache globalCache = new GlobalCache(getAccountCacheFolder());
        AccountCache accountCache = globalCache.getAccountCache();

        accountCache.addMultisigAccount(multisigAccount);
        globalCache.setAccountCache(accountCache);
    }

    public boolean deleteMultisigAccount(AlgoMultisigAccount multisigAccount) {

        GlobalCache globalCache = new GlobalCache(getAccountCacheFolder());
        AccountCache accountCache = globalCache.getAccountCache();

        if(accountCache.deleteMultisigAccount(multisigAccount)) {
            globalCache.setAccountCache(accountCache);
            return true;
        }

        return false;
    }

    public void clearCache() {
        GlobalCache globalCache = new GlobalCache(getAccountCacheFolder());
        globalCache.clearAccountCache();
    }

//    public RemoteAVMNodeAdapter getRemoteAvmAdapter(Project project) {
//        AvmConfigStateService.State state = KernelConfigurationHelper.getRemoteKernelInfo(project);
//
//        if(state == null || StringUtil.isEmpty(state.web3RpcUrl))
//            return null;
//
//        String web3RpcUrl = state.web3RpcUrl.trim();
//        RemoteAVMNodeAdapter remoteAVMNodeAdapter = new RemoteAVMNodeAdapter(web3RpcUrl);
//        return remoteAVMNodeAdapter;
//    }
//
//    public BigInteger getBalance(Account account, boolean isRemote) {
//        RemoteAVMNodeAdapter remoteAVMNodeAdapter = null;
//        if(isRemote) {
//            remoteAVMNodeAdapter = getRemoteAvmAdapter(project);
//        }
//
//        if (isRemote) {
//            if(remoteAVMNodeAdapter != null) {
//                return fetchRemoteBalance(remoteAVMNodeAdapter, account);
//            }
//        }
//
//        return null;
//    }
//
//    private BigInteger fetchRemoteBalance(RemoteAVMNodeAdapter remoteAVMNodeAdapter, Account account) {
//        return remoteAVMNodeAdapter.getBalance(account.getAddress());
//    }

//    private boolean isContinue() {
//        AvmService avmService = ServiceManager.getService(project, AvmService.class);
//
//        if(avmService != null && avmService.isAvmProject()) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    protected String getAccountCacheFolder() {
        String home = System.getProperty("user.home");

        File cacheFolder = new File(home);

        if(!cacheFolder.canWrite()) {
            //Let's find temp folder
            String temp = System.getProperty("java.io.tmpdir");
            File tempFolder = new File(temp);

            if(!tempFolder.canWrite()) {
                log.warn("Unable to find a writable folder to keep account list cache");
                return null;
            } else {
                cacheFolder = tempFolder;
            }
        }

        File algoFolder = new File(cacheFolder, ".algodea");
        if(!algoFolder.exists()) {
            algoFolder.mkdirs();
        }

        return algoFolder.getAbsolutePath();
    }

    public BigInteger getBalance(AlgoAccount account, boolean isRemote) {
        return BigInteger.ZERO;
    }

    public boolean updateAccountName(String address, String accountName) {
        GlobalCache globalCache = new GlobalCache(getAccountCacheFolder());
        AccountCache accountCache = globalCache.getAccountCache();

        boolean successful = accountCache.updateAccountName(address, accountName);
        globalCache.setAccountCache(accountCache);

        return successful;
    }
}
