package com.bloxbean.algodea.idea.stateless.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MultiSigLogicSigDialog extends DialogWrapper {
    private JPanel mainPanel;
    private MultiSigLogicSigCreateInputForm multiSigLSigInputForm;
    private ArgsInputForm argsInputForm;

    public MultiSigLogicSigDialog(@Nullable Project project) {
        super(project, true);
        init();
        setTitle("Multi Signature - Logic Sig generation");

        multiSigLSigInputForm.initializeData(project);
        argsInputForm.initializeData(project);
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        ValidationInfo mslsValidationInfo = multiSigLSigInputForm.doValidate();
        if(mslsValidationInfo == null)
            return argsInputForm.doValidate();
        else
            return mslsValidationInfo;
    }

    public MultiSigLogicSigCreateInputForm getMultiSigLSigInputForm() {
        return multiSigLSigInputForm;
    }

    public ArgsInputForm getArgsInputForm() {
        return argsInputForm;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        multiSigLSigInputForm = new MultiSigLogicSigCreateInputForm();
        argsInputForm = new ArgsInputForm();
    }
}
