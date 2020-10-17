package com.bloxbean.algodea.idea.contracts.action;

import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algodea.idea.contracts.ui.AppTxnBaseParamEntryForm;
import com.bloxbean.algodea.idea.contracts.ui.TxnDetailsEntryForm;
import com.bloxbean.algodea.idea.contracts.ui.UpdateAppDialog;
import com.bloxbean.algodea.idea.contracts.ui.UpdateAppEntryForm;
import com.bloxbean.algodea.idea.core.action.AlgoBaseAction;
import com.bloxbean.algodea.idea.core.action.util.AlgoContractModuleHelper;
import com.bloxbean.algodea.idea.core.service.AlgoCacheService;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.TxnDetailsParameters;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.service.StatefulContractService;
import com.bloxbean.algodea.idea.pkg.AlgoPkgJsonService;
import com.bloxbean.algodea.idea.pkg.model.AlgoPackageJson;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.bloxbean.algodea.idea.util.AlgoModuleUtils;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationType;
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
import java.util.List;

public class UpdateStatefulAppAction extends AlgoBaseAction {
    private final static Logger LOG = Logger.getInstance(UpdateStatefulAppAction.class);

    public UpdateStatefulAppAction() {
        super(AllIcons.Actions.EditSource);
    }

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
                IdeaUtil.showNotificationWithAction(project, "UpdateApplication",
                        "Project data could not be found. Something is wrong", NotificationType.ERROR, null);
                return;
            }

            AlgoPkgJsonService pkgJsonService = AlgoPkgJsonService.getInstance(project);
            AlgoPackageJson packageJson = pkgJsonService.getPackageJson();
            if (packageJson == null || packageJson.getStatefulContractList().size() == 0) {
                IdeaUtil.showNotification(project, "Create App",
                        "No stateful contract defined in algo-package.json", NotificationType.WARNING, null);
                return;
            }

            String deploymentServerId = projectState.getState().getDeploymentServerId();

            AlgoCacheService cacheService = AlgoCacheService.getInstance(project);

            UpdateAppDialog dialog = new UpdateAppDialog(project, cacheService.getContract());
            boolean ok = dialog.showAndGet();
            if(!ok) {
                IdeaUtil.showNotification(project, "UpdateApplication", "UpdateApplication operation was cancelled", NotificationType.WARNING, null);
                return;
            }

            UpdateAppEntryForm updateForm = dialog.getUpdateAppEntryForm();
            AppTxnBaseParamEntryForm appTxnBaseForm = dialog.getAppTxnBaseEntryForm();
            TxnDetailsEntryForm txnDetailsEntryForm = dialog.getTxnDetailsEntryForm();

            Long appId = appTxnBaseForm.getAppId();
            if(appId == null) {
                console.showErrorMessage("Invalid or null App Id");
                console.showErrorMessage("UpdateApplication Failed");
                return;
            }

            Account account = appTxnBaseForm.getFromAccount();
            if(account == null) {
                console.showErrorMessage("Invalid or null from account");
                console.showErrorMessage("UpdateApplication Failed");
                return;
            }

            String contractName = updateForm.getContractName();
            String approvalProgramName = updateForm.getApprovalProgram();
            String clearStateProgramName = updateForm.getClearStateProgram();
            if(StringUtil.isEmpty(approvalProgramName)) {
                console.showErrorMessage("Approval program name cannot be empty");
                console.showErrorMessage("UpdateApplication Failed");
                return;
            }

            if(StringUtil.isEmpty(clearStateProgramName)) {
                console.showErrorMessage("Clear state program name cannot be empty");
                console.showErrorMessage("UpdateApplication Failed");
                return;
            }

            List<byte[]> appArgs = txnDetailsEntryForm.getArgsAsBytes();
            byte[] note = txnDetailsEntryForm.getNoteBytes();
            byte[] lease = txnDetailsEntryForm.getLeaseBytes();
            List<Address> accounts = txnDetailsEntryForm.getAccounts();
            List<Long> foreignApps = txnDetailsEntryForm.getForeignApps();
            List<Long> foreignAssets = txnDetailsEntryForm.getForeignAssets();

            TxnDetailsParameters txnDetailsParameters = new TxnDetailsParameters();
            txnDetailsParameters.setAppArgs(appArgs);
            txnDetailsParameters.setNote(note);
            txnDetailsParameters.setLease(lease);
            txnDetailsParameters.setAccounts(accounts);
            txnDetailsParameters.setForeignApps(foreignApps);
            txnDetailsParameters.setForeignAssets(foreignAssets);

            //update cache.. For update from account, save it inside creator account for now.
            cacheService.setSfCreatorAccount(account.getAddress().toString());
            if(!StringUtil.isEmpty(contractName))
                cacheService.setLastContract(contractName);

            //Update latest approval program name & clear state program name //TODO

            VirtualFile appProgVF = AlgoModuleUtils.getSourceVirtualFileByRelativePath(project, approvalProgramName);//VfsUtil.findRelativeFile(approvalProgramName, sourceRoot);//VfsUtil.findRelativeFile(sourceRoot, approvalProgramName);
            VirtualFile clearProgVF = AlgoModuleUtils.getSourceVirtualFileByRelativePath(project, clearStateProgramName);//VfsUtil.findRelativeFile(clearStateProgramName, sourceRoot);

            if(appProgVF == null || !appProgVF.exists()) {
                console.showErrorMessage(String.format("Approval Program doesn't exist: %s", appProgVF != null ? appProgVF.getCanonicalPath(): approvalProgramName));
                return;
            }

            if(clearProgVF == null || !clearProgVF.exists()) {
                console.showErrorMessage(String.format("Clear State Program doesn't exist: %s", clearProgVF != null? clearProgVF.getCanonicalPath(): clearStateProgramName));
                return;
            }

            //Find relative path for source which is required to create merged source
            String relAppProgPath = AlgoModuleUtils.getRelativePathFromSourceRoot(project, appProgVF);
            String relClearStatePath = AlgoModuleUtils.getRelativePathFromSourceRoot(project, clearProgVF);

            if(StringUtil.isEmpty(relAppProgPath))
                relAppProgPath = appProgVF.getName();
            if(StringUtil.isEmpty(relClearStatePath))
                relClearStatePath = clearProgVF.getName();
            //ends

            VirtualFile moduleOutFolder = AlgoContractModuleHelper.getModuleOutputFolder(console, module);

            //Merge Approval Program if there is any variable template available
            File mergedAppProgSource  = AlgoContractModuleHelper.generateMergeSourceWithVariables(project, console, moduleOutFolder, appProgVF, relAppProgPath);

            //Merge Clear Program if there is any variable template available
            File mergeClearProgSource  = AlgoContractModuleHelper.generateMergeSourceWithVariables(project, console, moduleOutFolder, clearProgVF, relClearStatePath);

            String appProgSource = null;
            String clearProgSource = null;

            if(mergedAppProgSource == null) { //No VAR_TMPL_
                appProgSource = VfsUtil.loadText(appProgVF);
            } else {
                console.showInfoMessage("Variables found. Generated merged file for Approval Program can be found at : " + mergedAppProgSource.getAbsolutePath());
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

            Task.Backgroundable task = new Task.Backgroundable(project, "Updating Stateful Smart Contract app") {

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    console.showInfoMessage("Updating stateful smart contract ...");
                    Long appId = appTxnBaseForm.getAppId();
                    boolean success = false;
                    try {
                       success = sfService.updateApp(appId, account, appProgText, clearProgText, txnDetailsParameters);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    if(success) {
                        LOG.info(appId + "");

                        cacheService.addAppId(deploymentServerId, contractName, String.valueOf(appId));

                        console.showInfoMessage("Stateful smart contract app updated with app Id : " + appId);
                        IdeaUtil.showNotification(project, "Update App", String.format("%s App Created Successfully with appId: %s", contractName, appId), NotificationType.INFORMATION, null);
                    } else {
                        console.showErrorMessage("Update App failed");
                        IdeaUtil.showNotification(project, "Update App", "Update App failed", NotificationType.ERROR, null);
                    }
                }
            };


            ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));


        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            deploymentTargetNotConfigured.printStackTrace();
            warnDeploymentTargetNotConfigured(project, "UpdateApplication");
        } catch (Exception ex) {
            LOG.error(ex);
            IdeaUtil.showNotification(project, "UpdateApplication", "Update App failed : " + ex.getMessage(), NotificationType.ERROR, null);
        }
    }
}
