package com.bloxbean.algodea.idea.dryrun.ui;

import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.nodeint.model.DryRunContext;
import com.bloxbean.algodea.idea.util.AlgoModuleUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.*;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DryRunContextForm  {
    private JPanel mainPanel;
    private JTextField roundTf;
    private JTextField appIdsTf;
    private JTextField latestTimestampTf;
    private JButton addButton;
    private JButton removeButton;
    private JTextField protocolVersionTf;
    private JList accountList;
    private JScrollPane scrollPane;

    private DefaultListModel<String> listModel;

    public DryRunContextForm() {

    }

    public void initializeData(Project project, List<Long> appIds) {
        if(appIds != null && appIds.size() > 0) {
            String mergedAppIds = appIds.stream().map(l -> String.valueOf(l)).collect(Collectors.joining(","));
            appIdsTf.setText(String.valueOf(mergedAppIds));
        }

        attachListeners(project);
    }

    private void attachListeners(Project project) {
        addButton.addActionListener(e -> {
            AlgoAccount account = AccountChooser.getSelectedAccount(project, true);
            if(account == null)
                return;

            listModel.addElement(account.getAddress());
        });

        removeButton.addActionListener(e -> {
            int index = accountList.getSelectedIndex();
            if(index == -1) {
                Messages.showWarningDialog("Please select an account first to remove from the list.", "Remove");
                return;
            }

            listModel.remove(index);
        });
    }

    protected @Nullable ValidationInfo doValidate() {
        try {
            getApplications();
        } catch (Exception e) {
            return new ValidationInfo("Please provide valid application ids separated by comma. Error: " + e.getMessage(), appIdsTf);
        }

        try {
            getLatestTimestamp();
        } catch (Exception e) {
            return new ValidationInfo("Enter valid latest timestamp. Error: " + e.getMessage(), latestTimestampTf);
        }

        try {
            getRound();
        } catch (Exception e) {
            return new ValidationInfo("Enter valid round. Error: " + e.getMessage(), roundTf);
        }

        return null;
    }

    public List<String> getAccounts() {
        int size = listModel.getSize();
        if(size == 0)
            return Collections.EMPTY_LIST;

        List<String> accounts = new ArrayList<>();
        for(int i=0; i<size; i++) {
            accounts.add(listModel.get(i));
        }

        return accounts;
    }

    public List<Long> getApplications() {
        if(StringUtil.isEmpty(appIdsTf.getText()))
            return Collections.EMPTY_LIST;

        String[] ids = appIdsTf.getText().split(",");

        List<Long> lIds = new ArrayList<>();

        for (String id : ids) {
            lIds.add(Long.parseLong(StringUtil.trim(id)));
        }

        return lIds;
    }

    public Long getLatestTimestamp() {
        if(StringUtil.isEmpty(latestTimestampTf.getText()))
            return 0L;

        return Long.parseLong(StringUtil.trim(latestTimestampTf.getText()));
    }

    public BigInteger getRound() {
        if(StringUtil.isEmpty(roundTf.getText()))
            return BigInteger.ZERO;

        return new BigInteger(StringUtil.trim(roundTf.getText()));
    }

    public String getProtocolVersion() {
        if(StringUtil.isEmpty(protocolVersionTf.getText()))
            return null;

        return StringUtil.trim(protocolVersionTf.getText());
    }

    public DryRunContext getDryRunContext() {
        DryRunContext dryRunContext = new DryRunContext();
        dryRunContext.addresses = getAccounts();
        dryRunContext.appIds = getApplications();
        dryRunContext.latestTimestamp = getLatestTimestamp();
        dryRunContext.round = getRound();
        dryRunContext.protocol = getProtocolVersion();

        return dryRunContext;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        listModel = new DefaultListModel<>();
        accountList = new JBList(listModel);
        scrollPane = new JBScrollPane(accountList);
    }
}
