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
package com.bloxbean.algorand.idea.contracts.action;

import com.algorand.algosdk.account.Account;
import com.bloxbean.algorand.idea.account.model.AlgoAccount;
import com.bloxbean.algorand.idea.account.service.AccountService;
import com.bloxbean.algorand.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algorand.idea.contracts.ui.CreateAppDiaglog;
import com.bloxbean.algorand.idea.core.action.util.AlgoContractModuleHelper;
import com.bloxbean.algorand.idea.core.service.AlgoCacheService;
import com.bloxbean.algorand.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algorand.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algorand.idea.nodeint.service.StatefulContractService;
import com.bloxbean.algorand.idea.toolwindow.AlgoConsole;
import com.bloxbean.algorand.idea.util.AlgoModuleUtils;
import com.bloxbean.algorand.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.twelvemonkeys.lang.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class CreateStatefulAppAction extends AnAction {
    private final static Logger LOG = Logger.getInstance(CreateStatefulAppAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(project == null)
            return;

        final Module module = LangDataKeys.MODULE.getData(e.getDataContext());
        if(module == null)
            return;

        FileDocumentManager.getInstance().saveAllDocuments();

        AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();
        try {
            StatefulContractService sfService
                    = new StatefulContractService(project, new LogListenerAdapter(console));

            AlgoProjectState projectState = AlgoProjectState.getInstance(project);
            if(projectState == null) {
                LOG.error("Project state is null");
                IdeaUtil.showNotificationWithAction(project, "Create App",
                        "Project data could not be found. Something is wrong", NotificationType.ERROR, null);
                return;
            }

            String approvalProgramName = projectState.getState().getApprovalProgramName();
            String clearStateProgramName = projectState.getState().getClearStateProgramName();

            AlgoCacheService cacheService = AlgoCacheService.getInstance(project);
            AccountService accountService = AccountService.getAccountService(project);

            String cacheCreatorAccount = cacheService.getSfCreatorAccount();
            AlgoAccount cacheAlgoAccount = null;
            if(!StringUtil.isEmpty(cacheCreatorAccount)) {
                cacheAlgoAccount = accountService.getAccountByAddress(cacheCreatorAccount);
            }

            CreateAppDiaglog createDialog = new CreateAppDiaglog(project, cacheAlgoAccount, approvalProgramName, clearStateProgramName, cacheService.getSfGlobalByteslices(),
                    cacheService.getSfGlobalInts(), cacheService.getSfLocalByteslices(), cacheService.getSfLocalInts());
            boolean ok = createDialog.showAndGet();
            if(!ok) {
                IdeaUtil.showNotification(project, "Create App", "Create App operation was cancelled", NotificationType.INFORMATION, null);
                return;
            }

            Account account = createDialog.getAccount();
            if(account == null) {
                console.showErrorMessage("Invalid or null creator account");
                console.showErrorMessage("Create App Failed");
                return;
            }

            int globalByteslices = createDialog.getGlobalByteslices();
            int globalInts = createDialog.getGlobalInts();
            int localByteslices = createDialog.getLocalByteslices();
            int localInts = createDialog.getLocalInts();

            //update cache
            cacheService.updateSfGlobalBytesInts(globalByteslices, globalInts, localByteslices, localInts);
            cacheService.setSfCreatorAccount(account.getAddress().toString());

            VirtualFile sourceRoot = AlgoModuleUtils.getFirstSourceRoot(project);
            LOG.info("Source root : " + sourceRoot);

            VirtualFile appProgVF = VfsUtil.findRelativeFile(sourceRoot, approvalProgramName);
            VirtualFile clearProgVF = VfsUtil.findRelativeFile(sourceRoot, clearStateProgramName);

            VirtualFile moduleOutFolder = AlgoContractModuleHelper.getModuleOutputFolder(console, module);

            //Merge Approval Program if there is any variable template available
            File mergedAppProgSource  = AlgoContractModuleHelper.generateMergeSourceWithVariables(project, console, moduleOutFolder, appProgVF);

            //Merge Clear Program if there is any variable template available
            File mergeClearProgSource  = AlgoContractModuleHelper.generateMergeSourceWithVariables(project, console, moduleOutFolder, clearProgVF);

            String appProgSource = null;
            String clearProgSource = null;

            if(mergedAppProgSource == null) { //No VAR_TMPL_
                appProgSource = VfsUtil.loadText(appProgVF);
            } else {
                console.showInfoMessage("Variables found. Generated merged file for Approval Program can be found at : " + mergeClearProgSource.getAbsolutePath());
                appProgSource = FileUtil.loadFile(mergedAppProgSource, "UTF-8");
            }

            if(mergeClearProgSource == null) { //No VAR_TMPL
                clearProgSource = VfsUtil.loadText(clearProgVF);;
            } else {
                console.showInfoMessage("Variables found. Generated merged file for Clear State Program can be found at : " + mergeClearProgSource.getAbsolutePath());
                clearProgSource = FileUtil.loadFile(mergeClearProgSource, "UTF-8");
            }

            LOG.info(appProgSource);
            LOG.info(clearProgSource);

            //Needed for nested class
            final String appProgText = appProgSource;
            final String clearProgText = clearProgSource;

            Task.Backgroundable task = new Task.Backgroundable(project, "Creating Stateful Smart Contract app") {

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    console.showInfoMessage("Creating stateful smart contract ...");
                    Long appId = null;
                    try {
                        appId = sfService.createApp(appProgText, clearProgText, account,
                                globalByteslices, globalInts, localByteslices, localInts);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    if(appId != null) {
                        LOG.info(appId + "");

                        console.showInfoMessage("Stateful smart contract app created with app Id : " + appId);
                        IdeaUtil.showNotification(project, "Create App", "App Created Successfully with appId: " + appId, NotificationType.INFORMATION, null);
                    } else {
                        console.showErrorMessage("Create App failed");
                        IdeaUtil.showNotification(project, "Create App", "Create App failed", NotificationType.ERROR, null);
                    }
                }
            };


            ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));


        } catch (DeploymentTargetNotConfigured | Exception deploymentTargetNotConfigured) {
            deploymentTargetNotConfigured.printStackTrace();
        }
    }
}
