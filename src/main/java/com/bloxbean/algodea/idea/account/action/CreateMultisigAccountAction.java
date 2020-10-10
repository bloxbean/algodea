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
package com.bloxbean.algodea.idea.account.action;

import com.bloxbean.algodea.idea.account.exception.InvalidMnemonicException;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algodea.idea.account.service.AccountService;
import com.bloxbean.algodea.idea.account.ui.MultiSignAccountCreateDialog;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.account.exception.AccountException;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CreateMultisigAccountAction extends AnAction {
    private final static Logger LOG = Logger.getInstance(CreateMultisigAccountAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        AccountService accountService = AccountService.getAccountService(project);
        AlgoConsole console = AlgoConsole.getConsole(project);

        MultiSignAccountCreateDialog multiSignAccountCreateDialog = new MultiSignAccountCreateDialog(project);
        boolean ok = multiSignAccountCreateDialog.showAndGet();
        try {
            if (!ok) {
                IdeaUtil.showNotification(project, "Multisig Account Create",
                        "Multisig account creation was cancelled", NotificationType.WARNING, null);
                return;
            } else {
                int threshold = multiSignAccountCreateDialog.getThreshold();
                List<AlgoAccount> accounts = multiSignAccountCreateDialog.getAccounts();
                try {
                    AlgoMultisigAccount algoMultisigAccount = accountService.createNewMultisigAccount(threshold, accounts);

                    console.clearAndshow();
                    console.showSuccessMessage("Multisig account created successfully with the following details :-");
                    console.showInfoMessage(String.format("Multisig account address : %s", algoMultisigAccount.getAddress()));
                    console.showInfoMessage(String.format("Threshold                : %s", algoMultisigAccount.getThreshold()));

                    for (String account : algoMultisigAccount.getAccounts()) {
                        console.showInfoMessage( String.format("Account                  : %s", account));
                    }

                    IdeaUtil.showNotification(project, "Multisig Account Create",
                            "Multisig account created successfully", NotificationType.INFORMATION, IdeaUtil.MULTISIG_ACCOUNT_LIST_ACTION);

                } catch (AccountException accountException) {
                    LOG.error(accountException);
                    IdeaUtil.showNotification(project, "Multisig Account Create",
                            "Multisig account creation failed", NotificationType.WARNING, null);
                } catch (InvalidMnemonicException invalidMnemonicException) {
                    LOG.error(invalidMnemonicException);
                    IdeaUtil.showNotification(project, "Multisig Account Create",
                            "Multisig account creation failed", NotificationType.WARNING, null);
                }
            }
        } finally {
            multiSignAccountCreateDialog.disposeIfNeeded();
        }
    }
}
