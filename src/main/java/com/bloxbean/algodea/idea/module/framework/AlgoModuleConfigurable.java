package com.bloxbean.algodea.idea.module.framework;

import com.bloxbean.algodea.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algodea.idea.module.framework.ui.StatefulContractPanel;
import com.bloxbean.algodea.idea.pkg.model.AlgoPackageJson;
import com.bloxbean.algodea.idea.pkg.exception.PackageJsonException;
import com.bloxbean.algodea.idea.pkg.AlgoPkgJsonService;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.framework.addSupport.FrameworkSupportInModuleConfigurable;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableModelsProvider;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AlgoModuleConfigurable extends FrameworkSupportInModuleConfigurable {
    private StatefulContractPanel statefulContractPanel = new StatefulContractPanel();

    @Nullable
    @Override
    public JComponent createComponent() {
        return statefulContractPanel.getMainPanel();
    }

    @Override
    public void addSupport(@NotNull Module module, @NotNull ModifiableRootModel model, @NotNull ModifiableModelsProvider provider) {
        String approvalProgramName = statefulContractPanel.getApprovalProgram();
        String clearStateProgramName = statefulContractPanel.getClearStateProgram();

        Project project = module.getProject();
        AlgoProjectState projectState = AlgoProjectState.getInstance(project);
        if (projectState != null) {
            AlgoProjectState.State state = projectState.getState();
            state.setSupportStatefulContract(true);
            state.setApprovalProgramName(approvalProgramName);
            state.setClearStateProgramName(clearStateProgramName);

            VirtualFile[] srcRoots = model.getSourceRoots();
            if (srcRoots != null && srcRoots.length > 0) {
                if (srcRoots[0].exists()) {
                    createStatefulContractFiles(project, srcRoots[0], approvalProgramName, clearStateProgramName);
                }
            }
        }
    }

    private void createStatefulContractFiles(Project project, VirtualFile srcRoot, String approvalProgramName, String clearStateProgramName) {
        if (srcRoot == null)
            return;

        if (srcRoot.getName().equals("teal")) {
            VirtualFile main = srcRoot.getParent();
            if (main != null && "main".equals(main.getName())) {
                final VirtualFile src = main.getParent();
                if (src != null) {
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                createFile(approvalProgramName, srcRoot, "_Algo.ApprovalProgram");
                                createFile(clearStateProgramName, srcRoot, "_Algo.ClearStateProgram");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }

        //Update package json
        WriteCommandAction.runWriteCommandAction(project, ()-> {
            try {
                AlgoPackageJson packageJson = AlgoPkgJsonService.getInstance(project).loadPackageJson();

                AlgoPackageJson.StatefulContract statefulContract = new AlgoPackageJson.StatefulContract();
                statefulContract.setName("StatefulContract");
                statefulContract.setApprovalProgram(approvalProgramName);
                statefulContract.setClearStateProgram(clearStateProgramName);

                packageJson.addStatefulContractList(statefulContract);

                AlgoPkgJsonService.getInstance(project).writeToPackageJson(packageJson);
            } catch (PackageJsonException e) {
                IdeaUtil.showNotification(project, "Project create",
                        "algo-package.json could not be created or found", NotificationType.ERROR, null);
            }
        });
    }

    private boolean createFile(String file, VirtualFile folder, final String templateName)
            throws Exception {
        final FileTemplate template = FileTemplateManager.getDefaultInstance().getInternalTemplate(templateName);

        VirtualFile srcFile = folder.createChildData(this, file);
        VfsUtil.saveText(srcFile, template.getText());

        return true;
    }
}
