package com.bloxbean.algodea.idea.dryrun.ui;

import com.algorand.algosdk.crypto.Address;
import com.bloxbean.algodea.idea.nodeint.model.DryRunContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DryRunContextEntryDialog extends DialogWrapper {
    private JPanel mainPanel;
    private JPanel sourcePanel;
    private JPanel generalPanel;
    private DryRunSourceContextForm dryRunSourceInputForm;
    private DryRunContextForm dryRunContextForm;

    private boolean enableGeneralContextInfo;
    private boolean enableSourceInfo;

    public DryRunContextEntryDialog(@Nullable Project project,
                                    java.util.List<Long> appIds, java.util.List<Address> accounts, List<Long> foreignApps, boolean isStatefulContract, boolean enableGeneralContextInfo, boolean enableSourceInfo) {
        super(project, true);
        init();
        setTitle("Dry Run Context");

        this.enableSourceInfo = enableSourceInfo;
        this.enableGeneralContextInfo = enableGeneralContextInfo;

        if(enableGeneralContextInfo) {
            dryRunContextForm.initializeData(project, appIds);
        } else {
            generalPanel.setVisible(false);
        }

        Long appId = null;
        if(enableSourceInfo) {
            if(appIds != null && appIds.size() > 0)
                appId = appIds.get(0);

            dryRunSourceInputForm.initializeData(project, appId, isStatefulContract);
        } else {
            sourcePanel.setVisible(false);
        }

        if(enableSourceInfo && !enableGeneralContextInfo) {
            mainPanel.setMinimumSize(new Dimension(650, 300));
            mainPanel.setPreferredSize(new Dimension(650, 300));
        }

        if(accounts != null && !accounts.isEmpty()) {
            dryRunContextForm.setAccounts(accounts);
        }

        if(foreignApps != null && !foreignApps.isEmpty()) {
            List<Long> allApps = new ArrayList<>();
            if(appId != null)
                allApps.add(appId);

            allApps.addAll(foreignApps);
            dryRunContextForm.setApplications(allApps);
        }

    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        ValidationInfo validationInfo = null;

        if(enableSourceInfo) {
            validationInfo = dryRunSourceInputForm.doValidate();
            if (validationInfo != null)
                return validationInfo;
        }

        if(enableGeneralContextInfo) {
            validationInfo = dryRunContextForm.doValidate();
            if(validationInfo != null)
                return validationInfo;
        }

        return null;
    }

    public DryRunContext getDryRunContext() {
        DryRunContext dryRunContext = dryRunContextForm.getDryRunContext();

        if(enableSourceInfo) {
            DryRunContext.Source dryrunSource = dryRunSourceInputForm.getDryRunSource();
            dryRunContext.sources = new ArrayList<>();
            dryRunContext.sources.add(dryrunSource);
        }

        return dryRunContext;
    }

    public DryRunContext.Source getDryRunSource() {
        if(enableSourceInfo) {
            return dryRunSourceInputForm.getDryRunSource();
        } else {
            return null;
        }
    }

    public DryRunSourceContextForm getDryRunSourceInputForm() {
        return dryRunSourceInputForm;
    }

    public DryRunContextForm getDryRunContextForm() {
        return dryRunContextForm;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        dryRunSourceInputForm = new DryRunSourceContextForm();
        dryRunContextForm = new DryRunContextForm();
    }
}
