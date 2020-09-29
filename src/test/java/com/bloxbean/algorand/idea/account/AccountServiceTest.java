package com.bloxbean.algorand.idea.account;

import com.bloxbean.algorand.idea.action.account.service.AccountService;
import com.bloxbean.algorand.idea.action.account.model.AlgoAccount;
import org.junit.Assert;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;

public class AccountServiceTest {

    @Test
    public void testGenerateAddress() throws NoSuchAlgorithmException {
        AccountService accountService = new AccountService();
        AlgoAccount algoAccount = accountService.generateAccount();

        Assert.assertTrue(algoAccount.getAddress() != null);
        Assert.assertTrue(algoAccount.getAddress().length() > 0);
        Assert.assertTrue(algoAccount.getMnemonic() != null && algoAccount.getMnemonic().length() > 0);
    }
}