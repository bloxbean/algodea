package com.bloxbean.algodea.idea.account;

import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountCacheService;
import com.bloxbean.algodea.idea.account.service.AccountService;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class AccountServiceTest {

    @Test
    public void testGenerateAddress() throws NoSuchAlgorithmException {
        AccountCacheService accountCacheService = new AccountCacheService() {
            @Override
            protected String getAccountCacheFolder() {
                File tempFolder = new File(System.getProperty("java.io.tmpdir"));
                File testFolder = new File(tempFolder + File.separator + "testFolder" + new Random().nextInt());

                System.out.println("path:: " + testFolder.getAbsolutePath());
                testFolder.mkdirs();

                testFolder.deleteOnExit();
                return testFolder.getAbsolutePath();
            }
        };

        AccountService accountService = new AccountService(accountCacheService);
        AlgoAccount algoAccount = accountService.createNewAccount();

        System.out.println(algoAccount.getMnemonic());

        Assert.assertTrue(algoAccount.getAddress() != null);
        Assert.assertTrue(algoAccount.getAddress().length() > 0);
        Assert.assertTrue(algoAccount.getMnemonic() != null && algoAccount.getMnemonic().length() > 0);
    }
}