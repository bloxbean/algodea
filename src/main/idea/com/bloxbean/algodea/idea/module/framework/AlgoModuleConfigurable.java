package com.bloxbean.algodea.idea.module.framework;

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
import com.intellij.openapi.diagnostic.Logger;
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
    private final static Logger LOG = Logger.getInstance(AlgoModuleConfigurable.class);

    private StatefulContractPanel statefulContractPanel = new StatefulContractPanel();

    @Nullable
    @Override
    public JComponent createComponent() {
        return statefulContractPanel.getMainPanel();
    }

    @Override
    public void addSupport(@NotNull Module module, @NotNull ModifiableRootModel model, @NotNull ModifiableModelsProvider provider) {
        String statefulContractName = statefulContractPanel.getStatefulContractName();
        String approvalProgramName = statefulContractPanel.getApprovalProgram();
        String clearStateProgramName = statefulContractPanel.getClearStateProgram();

        Project project = module.getProject();

        VirtualFile[] srcRoots = model.getSourceRoots();
        if (srcRoots != null && srcRoots.length > 0) {
            if (srcRoots[0].exists()) {
                createStatefulContractFiles(project, srcRoots[0], statefulContractName, approvalProgramName, clearStateProgramName);
            }
        }

     /*   AlgoProjectState projectState = AlgoProjectState.getInstance(project);
        if (projectState != null) {
            AlgoProjectState.State state = projectState.getState();
//            state.setSupportStatefulContract(true);
//            state.setApprovalProgramName(approvalProgramName);
//            state.setClearStateProgramName(clearStateProgramName);

            VirtualFile[] srcRoots = model.getSourceRoots();
            if (srcRoots != null && srcRoots.length > 0) {
                if (srcRoots[0].exists()) {
                    createStatefulContractFiles(project, srcRoots[0], contractName, approvalProgramName, clearStateProgramName);
                }
            }
        }*/
    }

    private void createStatefulContractFiles(Project project, VirtualFile srcRoot, String contractName, String approvalProgramName, String clearStateProgramName) {
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
                                if(LOG.isDebugEnabled()) {
                                    LOG.error(e);
                                }
                            }
                        }
                    });
                }
            }
        }

        //Update package json
        WriteCommandAction.runWriteCommandAction(project, ()-> {
            try {
                AlgoPkgJsonService pkgJsonService = AlgoPkgJsonService.getInstance(project);
                if(pkgJsonService == null) return;

                AlgoPackageJson packageJson = pkgJsonService.getPackageJson();

                if(packageJson == null)
                    packageJson = pkgJsonService.createPackageJson();

                AlgoPackageJson.StatefulContract statefulContract = new AlgoPackageJson.StatefulContract();
                statefulContract.setApprovalProgram("src/main/teal/" + approvalProgramName);
                statefulContract.setClearStateProgram("src/main/teal/" + clearStateProgramName);
                statefulContract.setName(contractName);

                packageJson.addStatefulContract(statefulContract);

                pkgJsonService.save();
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
