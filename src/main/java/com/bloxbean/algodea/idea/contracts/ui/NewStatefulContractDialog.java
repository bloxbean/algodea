package com.bloxbean.algodea.idea.contracts.ui;

import com.bloxbean.algodea.idea.configuration.ui.ContractSettingsConfigurationPanel;
import com.bloxbean.algodea.idea.pkg.AlgoPkgJsonService;
import com.bloxbean.algodea.idea.util.AlgoModuleUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.twelvemonkeys.lang.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class NewStatefulContractDialog extends DialogWrapper {
    private JPanel mainPanel;
    private ContractSettingsConfigurationPanel contractSettingsPanel;
    private AlgoPkgJsonService pkgJsonService;

    public NewStatefulContractDialog(@Nullable Project project) {
        super(project, true);
        init();
        setTitle("Stateful Contract Configuration");

        this.pkgJsonService = AlgoPkgJsonService.getInstance(project);
        initializeData(project);
    }

    private void initializeData(Project project) {
        if(pkgJsonService == null) {
            Messages.showWarningDialog("algo-package.json could not be loaded.", "Algo Package Json Error");
            return;
        }
        contractSettingsPanel.poulateData(pkgJsonService);

        String projectFolder = project.getBasePath();

        String sourcePath = AlgoModuleUtils.getFirstSourceRootPath(project);//getFirstTEALSourceRootPath(project);

        if(sourcePath == null) {
            sourcePath = AlgoModuleUtils.getModuleDirPath(project);
        }

        if(!StringUtil.isEmpty(sourcePath)) {
            contractSettingsPanel.setSourceFolder(sourcePath);
        }

        if(!StringUtil.isEmpty(projectFolder)) {
            contractSettingsPanel.setProjectFolder(projectFolder);
        }

        contractSettingsPanel.newContractButtonClicked();
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        return contractSettingsPanel.doValidate();
    }

    public void save() {
        contractSettingsPanel.updateDataToState(pkgJsonService);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        this.contractSettingsPanel = new ContractSettingsConfigurationPanel();
    }
}
