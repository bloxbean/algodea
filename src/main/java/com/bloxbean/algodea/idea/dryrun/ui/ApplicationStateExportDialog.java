package com.bloxbean.algodea.idea.dryrun.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApplicationStateExportDialog extends DialogWrapper {
    private JPanel mainPanel;
    private JTextField appIdsTf;

    public ApplicationStateExportDialog() {
        super(true);
        init();
        setTitle("Export Applications");
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        if(getApplications() == null || getApplications().size() == 0)
            return new ValidationInfo("Please provide valid application ids separated by comma", appIdsTf);
        return null;
    }

    public List<Long> getApplications() {
        if(StringUtil.isEmpty(appIdsTf.getText()))
            return Collections.EMPTY_LIST;

        String[] ids = appIdsTf.getText().split(",");

        List<Long> lIds = new ArrayList<>();
        try {
            for (String id : ids) {
                lIds.add(Long.parseLong(StringUtil.trim(id)));
            }
        } catch (Exception e) {
            return Collections.EMPTY_LIST;
        }

        return lIds;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }
}
