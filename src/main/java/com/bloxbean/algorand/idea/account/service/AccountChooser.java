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

package com.bloxbean.algorand.idea.account.service;

import com.bloxbean.algorand.idea.account.model.AlgoAccount;
import com.bloxbean.algorand.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algorand.idea.account.ui.ListAccountDialog;
import com.bloxbean.algorand.idea.account.ui.ListMultisigAccountDialog;
import com.intellij.openapi.project.Project;

import java.util.List;

public class AccountChooser {

    public static AlgoAccount getSelectedAccount(Project project, boolean showBalance) {
        AccountService accountCacheService = AccountService.getAccountService(project);
        List<AlgoAccount> accounts = accountCacheService.getAccounts();

        ListAccountDialog listAccountDialog = new ListAccountDialog(project, true, showBalance);
        try {
            boolean result = listAccountDialog.showAndGet();

            if (!result) {
                return null;
            }

            AlgoAccount selectedAccount = listAccountDialog.getSelectAccount();
            return selectedAccount;
        } finally {
            listAccountDialog.disposeIfNeeded();
        }
    }

    public static AlgoMultisigAccount getSelectedMultisigAccount(Project project, boolean showBalance) {
        ListMultisigAccountDialog listAccountDialog = new ListMultisigAccountDialog(project, showBalance);
        try {
            boolean result = listAccountDialog.showAndGet();

            if (!result) {
                return null;
            }

            AlgoMultisigAccount selectedAccount = listAccountDialog.getSelectAccount();
            return selectedAccount;
        } finally {
            listAccountDialog.disposeIfNeeded();
        }
    }

//    public static Account getLocalAvmSelectedAccount(Project project) {
//        return getLocalAvmSelectedAccount(project, null, false);
//    }

//    public static Account getLocalAvmSelectedAccount(Project project, String moduleDir, boolean showBalance) {
//        AccountListFetcher accountListFetcher = new AccountListFetcher(project);
//        List<Account> accounts = accountListFetcher.getAccounts();
//
//        ListAccountDialog listAccountDialog = new ListAccountDialog(project, accounts, false, showBalance);
//        listAccountDialog.setModuleWorkingDir(moduleDir);
//
//        boolean result = listAccountDialog.showAndGet();
//
//        if(!result) {
//            return null;
//        }
//
//        Account selectedAccount = listAccountDialog.getSelectAccount();
//        return selectedAccount;
//    }
}
