package com.bloxbean.algodea.idea.dryrun.ui;

import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse;
import com.bloxbean.algodea.idea.account.model.AlgoAccount;
import com.bloxbean.algodea.idea.account.service.AccountChooser;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.model.DryRunContext;
import com.bloxbean.algodea.idea.nodeint.service.LogListenerAdapter;
import com.bloxbean.algodea.idea.nodeint.service.NetworkService;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
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
    private AlgoConsole console;

    public DryRunContextForm() {

    }

    public void initializeData(Project project, List<Long> appIds) {
        if(appIds != null && appIds.size() > 0) {
            String mergedAppIds = appIds.stream()
                    .filter(l -> l != null && l != 0)
                    .map(l -> String.valueOf(l))
                    .collect(Collectors.joining(","));
            appIdsTf.setText(String.valueOf(mergedAppIds));
        }

        console = AlgoConsole.getConsole(project);
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

    public void updateNetworkDefaults(Project project) {
        ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
            @Override
            public void run() {
                ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
                //Get network info
                try {
                    NetworkService networkService = new NetworkService(project, new LogListenerAdapter(console));
                    TransactionParametersResponse transactionParametersResponse = networkService.getNetworkInfo();

                    Integer latestTimestamp = null;
                    try {
                        String timeStampStr = networkService.getBlockTimeStamp(transactionParametersResponse.lastRound);
                        latestTimestamp = Integer.parseInt(timeStampStr);
                    } catch (Exception e) {}

                    roundTf.setText(String.valueOf(transactionParametersResponse.lastRound));
                    protocolVersionTf.setText(transactionParametersResponse.consensusVersion);
                    if(latestTimestamp != null) {
                        latestTimestampTf.setText(String.valueOf(latestTimestamp));
                    }

                } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
                    Messages.showWarningDialog(deploymentTargetNotConfigured.getMessage(), "Error");
                    console.showErrorMessage(deploymentTargetNotConfigured.getMessage(), deploymentTargetNotConfigured);
                } catch (Exception exception) {
                    console.showErrorMessage(exception.getMessage(), exception);
                }

                progressIndicator.setFraction(1.0);
            }
        } , "Connecting to Algorand Node to fetch network info...", true, project);
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

    public void setAccounts(List<Address> addresses) {
        if(addresses == null || addresses.isEmpty())
            return;

        for(Address address: addresses) {
            try {
                listModel.addElement(address.encodeAsString());
            } catch (NoSuchAlgorithmException e) {

            }
        }
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

    public void setApplications(List<Long> appIds) {
        if(appIds == null || appIds.isEmpty())
            return;

        String appIdsStr = appIds.stream().map(l -> String.valueOf(l)).collect(Collectors.joining(","));
        appIdsTf.setText(appIdsStr);
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
