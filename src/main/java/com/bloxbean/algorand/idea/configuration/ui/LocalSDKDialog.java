package com.bloxbean.algorand.idea.configuration.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.twelvemonkeys.lang.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LocalSDKDialog extends DialogWrapper {

    private LocalSDKPanel localSDKPanel;

    public LocalSDKDialog(Project project) {
        super(project);
        localSDKPanel = new LocalSDKPanel();
        init();
        setTitle("Local Algorand SDK");
    }
    @Override
    protected @Nullable JComponent createCenterPanel() {
        return localSDKPanel.getMainPanel();
    }

    public String getHome() {
        return localSDKPanel.getHome();
    }

    public String getName() {
        return localSDKPanel.getName();
    }

    public String getVersion() {
        return localSDKPanel.getVersion();
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        if(StringUtil.isEmpty(localSDKPanel.getName())) {
            return new ValidationInfo("Invalid Name", localSDKPanel.getNameTf());
        }

        if(StringUtil.isEmpty(localSDKPanel.getHome())) {
            return new ValidationInfo("Invalid Algorand Home", localSDKPanel.getHomeTf());
        }

        if(StringUtil.isEmpty(localSDKPanel.getVersion())) {
            return new ValidationInfo("Invalid Version Number or Version could not be determined", localSDKPanel.getVersionTf());
        }

        return null;
    }
}
