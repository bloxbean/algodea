package com.bloxbean.algodea.idea.module.framework;

import com.bloxbean.algodea.idea.module.ProjectGeneratorUtil;
import com.bloxbean.algodea.idea.module.ui.StatefulContractPanel;
import com.intellij.framework.addSupport.FrameworkSupportInModuleConfigurable;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableModelsProvider;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AlgoModuleConfigurable extends FrameworkSupportInModuleConfigurable {
    private final static Logger LOG = Logger.getInstance(AlgoModuleConfigurable.class);

    private StatefulContractPanel statefulContractPanel = new StatefulContractPanel(false);

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
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    ProjectGeneratorUtil.createStatefulContractFiles(project, srcRoots[0], statefulContractName,
                            approvalProgramName, clearStateProgramName);
                });
            }
        }
    }
}
