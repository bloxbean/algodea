package com.bloxbean.algorand.idea.module.framework;

import com.bloxbean.algorand.idea.module.framework.ui.StatefulContractPanel;
import com.bloxbean.algorand.idea.configuration.service.AlgoProjectState;
import com.intellij.framework.addSupport.FrameworkSupportInModuleConfigurable;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.application.ApplicationManager;
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
    }

    private boolean createFile(String file, VirtualFile folder, final String templateName)
            throws Exception {
        final FileTemplate template = FileTemplateManager.getDefaultInstance().getInternalTemplate(templateName);

        VirtualFile srcFile = folder.createChildData(this, file);
        VfsUtil.saveText(srcFile, template.getText());

        return true;
    }
}