package com.bloxbean.algodea.idea.core.action;

import com.bloxbean.algodea.idea.core.action.util.TransactionExporterUtil;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;

import javax.swing.*;
import java.util.function.Consumer;

public abstract class BaseTxnAction extends AlgoBaseAction {

    public BaseTxnAction() {
        super();
    }

    public BaseTxnAction(Icon icon) {
        super(icon);
    }

    public void exportTransaction(Project project, Module module, RequestMode requestMode, Result result, LogListener logListener) {
        if(result == null) {
            logListener.error("Export failed. Result : null");
            return;
        }
        if (result.isSuccessful()) {
            Result finalResult = result;

            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        String txnOutputFileName = Messages.showInputDialog("Enter txn file name (Without extension) : ",
                                "Export transaction", AllIcons.General.QuestionDialog, "NewTransaction", new InputValidator() {
                                    @Override
                                    public boolean checkInput(String inputString) {
                                        if(inputString != null && inputString.contains("."))
                                            return false;
                                        else
                                            return true;
                                    }

                                    @Override
                                    public boolean canClose(String inputString) {
                                        if(inputString != null && inputString.contains("."))
                                            return false;
                                        else
                                            return true;
                                    }
                                });
                        if(StringUtil.isEmpty(txnOutputFileName)) {
                            logListener.warn("Export transaction was cancelled");
                            return;
                        }

                        logListener.info(finalResult.getResponse());
                        boolean status = TransactionExporterUtil.exportTransaction(module, finalResult.getResponse(), txnOutputFileName, logListener);
                        if(status) {
                            IdeaUtil.showNotification(project, "Export Transaction", String.format("Export transaction has been completed"),
                                    NotificationType.INFORMATION, null);
                        }
                    } catch (Exception exception) {
                        IdeaUtil.showNotification(project, "Export Transaction", String.format("Export transaction was not successful"),
                                NotificationType.INFORMATION, null);
                    }
                }
            });

        } else {
            logListener.error("Export transaction was not successful");
            IdeaUtil.showNotification(project, "Export Transaction", "Export transaction was not successful", NotificationType.ERROR, null);
        }
    }

    protected void processResult(Project project, Module module, Result result, RequestMode requestMode, LogListener logListener) {

        if(requestMode == null || requestMode.equals(RequestMode.TRANSACTION)) {
            if (result != null && result.isSuccessful()) {
                logListener.info(String.format("%s transaction executed successfully", getTxnCommand()));
                IdeaUtil.showNotification(project, getTitle(), String.format("%s was successful", getTxnCommand()), NotificationType.INFORMATION, null);
            } else {
                logListener.info(String.format("%s failed", getTxnCommand()));
                IdeaUtil.showNotification(project, getTitle(), String.format("%s failed", getTxnCommand()), NotificationType.ERROR, null);
            }
        } else if(requestMode.equals(RequestMode.EXPORT_SIGNED) || requestMode.equals(RequestMode.EXPORT_UNSIGNED)) {
            exportTransaction(project, module, requestMode, result, logListener);
        }
    }

    protected abstract String getTitle();
    protected abstract String getTxnCommand();
}
