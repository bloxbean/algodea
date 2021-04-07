package com.bloxbean.algodea.idea.stateless.ui;

import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LogicSigSignParamDialog extends DialogWrapper {

    private JPanel mainPanel;
    private LogicSigSigningAccountForm logicSigSignAccForm;
    private ArgsInputForm argsInputForm;

    public LogicSigSignParamDialog(@Nullable Project project) throws DeploymentTargetNotConfigured {
        super(project, true);
        initializeData(project);
        init();
        setTitle("Create Logic Sig");
    }

    public void initializeData(Project project) throws DeploymentTargetNotConfigured {
        logicSigSignAccForm.initialize(project);
        argsInputForm.initializeData(project);
    }

//    @Override
//    protected @NotNull List<ValidationInfo> doValidateAll() {
//        List<ValidationInfo> validationInfos = new ArrayList<>();
//
//        ValidationInfo validatedInfo = logicSigSignAccForm.doValidate();
//        if(validatedInfo != null)
//            validationInfos.add(validatedInfo);
//
//        return validationInfos;
//    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        return logicSigSignAccForm.doValidate();
    }

    public LogicSigSigningAccountForm getLogicSigSignAccountForm() {
        return logicSigSignAccForm;
    }

    public ArgsInputForm getArgsInputForm() {return argsInputForm;}

    private void createUIComponents() {
        // TODO: place custom component creation code here
        logicSigSignAccForm = new LogicSigSigningAccountForm();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }
}
