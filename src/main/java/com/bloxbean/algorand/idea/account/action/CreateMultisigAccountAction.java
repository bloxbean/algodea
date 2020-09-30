package com.bloxbean.algorand.idea.account.action;

import com.bloxbean.algorand.idea.account.model.AlgoAccount;
import com.bloxbean.algorand.idea.account.model.AlgoMultisigAccount;
import com.bloxbean.algorand.idea.account.exception.AccountException;
import com.bloxbean.algorand.idea.account.service.AccountService;
import com.bloxbean.algorand.idea.account.exception.InvalidMnemonicException;
import com.bloxbean.algorand.idea.account.ui.MultiSignAccountCreateDialog;
import com.bloxbean.algorand.idea.util.IdeaUtil;
import com.bloxbean.algorand.idea.toolwindow.AlgoConsoleMessages;
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

                    AlgoConsoleMessages.clearAndshow(project);
                    AlgoConsoleMessages.showSuccessMessage(project, "Multisig account created successfully with the following details :-");
                    AlgoConsoleMessages.showInfoMessage(project, String.format("Multisig account address : %s", algoMultisigAccount.getAddress()));
                    AlgoConsoleMessages.showInfoMessage(project, String.format("Threshold                : %s", algoMultisigAccount.getThreshold()));

                    for (String account : algoMultisigAccount.getAccounts()) {
                        AlgoConsoleMessages.showInfoMessage(project, String.format("Account                  : %s", account));
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
