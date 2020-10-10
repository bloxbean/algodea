package com.bloxbean.algodea.idea.account;

import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountService;

import org.junit.Assert;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;

public class AccountServiceTest {

    @Test
    public void testGenerateAddress() throws NoSuchAlgorithmException {
        AccountService accountService = new AccountService();
        AlgoAccount algoAccount = accountService.createNewAccount();

        System.out.println(algoAccount.getMnemonic());

        Assert.assertTrue(algoAccount.getAddress() != null);
        Assert.assertTrue(algoAccount.getAddress().length() > 0);
        Assert.assertTrue(algoAccount.getMnemonic() != null && algoAccount.getMnemonic().length() > 0);
    }
}