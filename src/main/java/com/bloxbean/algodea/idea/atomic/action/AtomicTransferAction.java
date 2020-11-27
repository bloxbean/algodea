package com.bloxbean.algodea.idea.atomic.action;

import com.algorand.algosdk.transaction.SignedTransaction;
import com.bloxbean.algodea.idea.atomic.ui.AtomicTransferDialog;
import com.bloxbean.algodea.idea.core.action.BaseTxnAction;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.DryRunContext;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.service.TransactionService;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AtomicTransferAction extends BaseTxnAction {
    private final static Logger LOG = Logger.getInstance(AtomicTransferAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null)
            return;

        final Module module = LangDataKeys.MODULE.getData(e.getDataContext());

        AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();

        try {

            AtomicTransferDialog dialog = new AtomicTransferDialog(project, module);
            boolean ok = dialog.showAndGet();
            if(!ok) {
                IdeaUtil.showNotification(project, "Atomic Transfer", "Atomic Transfer cancelled", NotificationType.WARNING, null );
                return;
            }

            byte[] groupTxnBytes = dialog.getGroupTransactionBytes();
            String groupId = dialog.getGroupId();

            LogListener logListener = new LogListenerAdapter(console);
            TransactionService transactionService = new TransactionService(project, logListener);

            RequestMode requestMode = dialog.getRequestMode();
            List<SignedTransaction> signedTransactionList;
            if(RequestMode.DRY_RUN.equals(requestMode)) {
                signedTransactionList = dialog.getSignedTransactions();
                List<DryRunContext.Source> dryRunSources = dialog.getDryContextSources();

                List<Long> appIds = new ArrayList();

                if(signedTransactionList != null && signedTransactionList.size() > 0) { //Try to guess appId from the transactions
                    for(SignedTransaction stxn: signedTransactionList) {
                        if(stxn.tx.applicationId != null) {
                            appIds.add(stxn.tx.applicationId);
                        }
                    }
                }

                DryRunContext dryRunContext = captureDryRunContext(project, appIds,  !appIds.isEmpty(), true, false);
                if(dryRunSources != null)
                    dryRunContext.sources = dryRunSources;

                transactionService.setDryRunContext(dryRunContext);
            }

            Task.Backgroundable task = new Task.Backgroundable(project, getTxnCommand()) {

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    console.showInfoMessage(String.format("Starting %s ...\n", getTxnCommand()));
                    try {
                        Result result = null;

                        if(requestMode == null || requestMode.equals(RequestMode.TRANSACTION)) {
                            result = transactionService.atomicTransfer(groupId, groupTxnBytes);
                        } else if(requestMode.equals(RequestMode.DRY_RUN)) {
                            result = transactionService.atomicTransferDryRun(groupId, dialog.getSignedTransactions());
                        }

                        if (result.isSuccessful()) {
                                console.showInfoMessage("Atomic transfer completed successfully");
                                IdeaUtil.showNotification(project, getTitle(), "Atomic transfer completed successfully",
                                        NotificationType.INFORMATION, null);
                            } else {
                                console.showErrorMessage(String.format("%s failed: %s", getTxnCommand(), result.getResponse()));
                                IdeaUtil.showNotification(project, getTitle(), "Atomic Transfer failed : " + result.getResponse(), NotificationType.ERROR, null);
                            }
                    } catch (Exception exception) {
                        if (LOG.isDebugEnabled()) {
                            LOG.warn(exception);
                        }
                        console.showErrorMessage("Error", exception);
                        console.showErrorMessage(String.format("%s failed", getTxnCommand()));
                        IdeaUtil.showNotification(project, getTitle(), String.format("%s failed, Reason: %s", getTxnCommand(), exception.getMessage()), NotificationType.ERROR, null);
                    }
                }
            };

            ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            //deploymentTargetNotConfigured.printStackTrace();
            warnDeploymentTargetNotConfigured(project, getTitle());
        } catch (Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.error(ex);
            }
            console.showErrorMessage(ex.getMessage());
            IdeaUtil.showNotification(project, getTitle(), String.format("Atomic Transfer failed, reason: %s", ex.getMessage()), NotificationType.ERROR, null);
        }
    }

    public String getTitle() {
        return "Atomic Transfer";
    }

    public String getTxnCommand() {
        return "Atomic Transfer";
    }
}
