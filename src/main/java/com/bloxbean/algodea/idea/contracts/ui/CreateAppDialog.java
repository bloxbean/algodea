package com.bloxbean.algodea.idea.contracts.ui;

import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.core.action.ui.TxnDialogWrapper;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.transaction.ui.TransactionDtlsEntryForm;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CreateAppDialog extends TxnDialogWrapper {
    private CreateMainPanel createMainPanel;

    public CreateAppDialog(Project project,
                              AlgoAccount creatorAccount, String contractName) throws DeploymentTargetNotConfigured {
        super(project);
        createMainPanel = new CreateMainPanel(project, creatorAccount, contractName);
        init();
        setTitle("Create Stateful Smart Contract App");
    }

    @Override
    protected ValidationInfo doTransactionInputValidation() {
        return createMainPanel.doValidate();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return createMainPanel.getMainPanel();
    }

    public CreateAppEntryForm getCreateForm() {
        return createMainPanel.getCreateAppEntryForm();
    }

    public AppTxnDetailsEntryForm getAppTxnDetailsEntryForm() {
        return createMainPanel.getAppTxnDetailsEntryForm();
    }

    public TransactionDtlsEntryForm getTxnDetailsEntryForm() {
        return createMainPanel.getTxnDetailsEntryForm();
    }

    @Override
    protected void doOKAction() {
        if(getCreateForm().isContractSettingsUpdate()) {
            int yesNo = Messages.showYesNoDialog("Contract settings have been updated. " +
                            "\nDo you want to save these changes in algo-package.json ?", "Contract Settings Update",
                    AllIcons.General.QuestionDialog);
            if(yesNo == Messages.YES) {
                getCreateForm().saveUpdatedContractSettings();
            }
        }
        super.doOKAction();
    }

}
