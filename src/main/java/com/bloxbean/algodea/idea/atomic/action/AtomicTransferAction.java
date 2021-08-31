package com.bloxbean.algodea.idea.atomic.action;

import com.algorand.algosdk.transaction.SignedTransaction;
import com.bloxbean.algodea.idea.atomic.ui.AtomicTransferDialog;
import com.bloxbean.algodea.idea.common.AlgoIcons;
import com.bloxbean.algodea.idea.core.action.BaseTxnAction;
import com.bloxbean.algodea.idea.nodeint.common.RequestMode;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.DryRunContext;
import com.bloxbean.algodea.idea.nodeint.model.Result;
import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.service.TransactionService;
import com.bloxbean.algodea.idea.debugger.service.DebugHandler;
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
import java.util.Collections;
import java.util.List;

public class AtomicTransferAction extends BaseTxnAction {
    private final static Logger LOG = Logger.getInstance(AtomicTransferAction.class);

    public AtomicTransferAction(){
        super("Atomic Transfer", "Atomic Transfer", AlgoIcons.ATOMIC_TRANSFER_ICON);
    }

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
            if(RequestMode.DRY_RUN.equals(requestMode) || RequestMode.DRYRUN_DUMP.equals(requestMode)
                    ||RequestMode.DEBUG.equals(requestMode)) { //If dry run or debug, get dryrun context
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

                DryRunContext dryRunContext = captureDryRunContext(project, appIds, Collections.EMPTY_LIST, Collections.EMPTY_LIST, !appIds.isEmpty(), true, false);
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

                        RequestMode originalReqMode = requestMode;
                        RequestMode callRequestMode = originalReqMode;
                        if(callRequestMode.equals(RequestMode.DEBUG)) {
                            callRequestMode = RequestMode.DRYRUN_DUMP;
                        }

                        if(callRequestMode == null || callRequestMode.equals(RequestMode.TRANSACTION)) {
                            result = transactionService.atomicTransfer(groupId, groupTxnBytes);
                        } else if(callRequestMode.equals(RequestMode.DRY_RUN)) {
                            result = transactionService.atomicTransferDryRun(groupId, dialog.getSignedTransactions());
                        } else if(callRequestMode.equals(RequestMode.DRYRUN_DUMP)) {
                            result = transactionService.processDryRunDump(dialog.getSignedTransactions());
                        }

                        if(callRequestMode == null || callRequestMode.equals(RequestMode.TRANSACTION)) {
                            if (result.isSuccessful()) {
                                console.showInfoMessage("Atomic transfer completed successfully");
                                IdeaUtil.showNotification(project, getTitle(), "Atomic transfer completed successfully",
                                        NotificationType.INFORMATION, null);
                            } else {
                                console.showErrorMessage(String.format("%s failed: %s", getTxnCommand(), result.getResponse()));
                                IdeaUtil.showNotification(project, getTitle(), "Atomic Transfer failed : " + result.getResponse(), NotificationType.ERROR, null);
                            }
                        } else {
                            if(originalReqMode.equals(RequestMode.DEBUG)) {//Debug call
                                DebugHandler debugHandler = new DebugHandler();
                                DryRunContext dryRunContext = transactionService.getDryRunContext();

                                String[] sourceFiles = null;
                                if(dryRunContext != null && dryRunContext.sources != null && dryRunContext.sources.size() > 0) {
                                    sourceFiles = new String[dryRunContext.sources.size()];
                                    for(int i=0; i < dryRunContext.sources.size(); i++) {
                                        sourceFiles[i] = dryRunContext.sources.get(i).code;
                                    }
                                }

                                debugHandler.startStatefulCallDebugger(project, sourceFiles, console, result.getResponse());
                            } else {
                                processResult(project, module, result, requestMode, logListener);
                            }
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
