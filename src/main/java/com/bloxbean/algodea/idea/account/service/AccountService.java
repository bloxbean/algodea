package com.bloxbean.algodea.idea.account.service;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.crypto.Ed25519PublicKey;
import com.algorand.algosdk.crypto.MultisigAddress;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algodea.idea.account.exception.AccountException;
import com.bloxbean.algodea.idea.account.exception.InvalidMnemonicException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountService {
    private final static Logger LOG = Logger.getInstance(AccountService.class);

    private AccountCacheService accountCacheService;

    public static AccountService getAccountService(Project project) {
        return new AccountService();
    }

    public AccountService() {
        this.accountCacheService = new AccountCacheService();
    }

    public AlgoAccount createNewAccount() throws NoSuchAlgorithmException {
        Account account = new Account();
        Address address = account.getAddress();

        AlgoAccount algoAccount = new AlgoAccount(address.toString(), account.toMnemonic());

        accountCacheService.storeAccount(algoAccount);
        return algoAccount;
    }

    public AlgoMultisigAccount createNewMultisigAccount(int threshold, List<AlgoAccount> accounts)
            throws AccountException, InvalidMnemonicException {
        if(accounts == null || accounts.size() < threshold)
            throw new AccountException("Minimum criteria doesn't match. " +
                    "No of accounts in a multisig account should be equal or more than threshold");

        List<Ed25519PublicKey> accountPks = new ArrayList<>();
        for(AlgoAccount acc: accounts) {
            try {
                accountPks.add(new Account(acc.getMnemonic()).getEd25519PublicKey());
            } catch (GeneralSecurityException e) {
                throw new InvalidMnemonicException("Account cannot be generated from mnemonic for address: " + acc.getAddress());
            }
        }

        MultisigAddress multisigAddress = new MultisigAddress(1, threshold, accountPks);

        try {
            AlgoMultisigAccount algoMultisigAccount
                    = new AlgoMultisigAccount(multisigAddress.toAddress().toString(), threshold);
            algoMultisigAccount.setAccounts(accounts.stream()
                    .map(acc -> acc.getAddress())
                    .collect(Collectors.toList()));

            accountCacheService.storeMultisigAccount(algoMultisigAccount);
            return algoMultisigAccount;
        } catch (NoSuchAlgorithmException e) {
            throw new AccountException("Unable to generate address from multisig account");
        }
    }

    public List<AlgoAccount> getAccounts() {
        return accountCacheService.getAccounts();
    }

    public AlgoAccount getAccountByAddress(String address) {
        List<AlgoAccount> accounts = accountCacheService.getAccounts();
        for(AlgoAccount acc: accounts) {
            if(acc.getAddress().equals(address))
                return acc;
        }

        return null;
    }

    public List<AlgoMultisigAccount> getMultisigAccounts() {
        return accountCacheService.getMultisigAccounts();
    }

    public AlgoAccount getAccountFromMnemonic(String mnemonic) {
        try {
            Account account = new Account(mnemonic);
            return new AlgoAccount(account.getAddress().toString(), mnemonic);
        } catch (GeneralSecurityException e) {
            LOG.warn("Account derivation failed from mnemonic");
            return null;
        }
    }

    public void clearCache() {
        accountCacheService.clearCache();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        new AccountService().createNewAccount();
    }

}
