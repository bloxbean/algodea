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

package com.bloxbean.algorand.idea.account.action;

import com.bloxbean.algorand.idea.account.model.AlgoAccount;
import com.bloxbean.algorand.idea.account.service.AccountService;
import com.bloxbean.algorand.idea.toolwindow.AlgoConsoleMessages;
import com.bloxbean.algorand.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Satya
 */

public class CreateAccountAction extends AnAction {
    private final static Logger LOG = Logger.getInstance(CreateAccountAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        AccountService accountService = new AccountService();

        try {
            AlgoAccount algoAccount = accountService.createNewAccount();

            AlgoConsoleMessages.clearAndshow(project);
            AlgoConsoleMessages.showInfoMessage(project, String.format("Address: %s", algoAccount.getAddress()));
            AlgoConsoleMessages.showInfoMessage(project, String.format("Mnemonic: %s", algoAccount.getMnemonic()));
            AlgoConsoleMessages.showSuccessMessage( project, "New account created successfully");

            IdeaUtil.showNotification(project, "Account Create",
                    "A new account created successfully", NotificationType.INFORMATION, IdeaUtil.ACCOUNT_LIST_ACTION);
        } catch (NoSuchAlgorithmException ex) {
            LOG.error("Unable to create new Algorand Account", ex);
            AlgoConsoleMessages.showErrorMessage( project, "Account creation failed");
        }
    }

}
