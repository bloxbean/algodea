package com.bloxbean.algodea.idea.assets.ui;

import com.algorand.algosdk.v2.client.model.Asset;
import com.bloxbean.algodea.idea.assets.model.AssetMeta;
import com.bloxbean.algodea.idea.nodeint.exception.DeploymentTargetNotConfigured;
import com.bloxbean.algodea.idea.nodeint.service.AssetTransactionService;
import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import com.bloxbean.algodea.idea.util.StringUtility;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class AssetsChooserDialog {
    private JComboBox assetIdCB;
    private JButton assetSearchBtn;
    private JPanel assetChooserPanel;
    private JLabel assetLabel;

    private LogListener logListener;

    public AssetsChooserDialog() {

    }

    public void initializeData(Project project, LogListener listener) {
        this.logListener = logListener;
       // AlgoConsole algoConsole = AlgoConsole.getConsole(project);
        //algoConsole.clearAndshow();

        AssetTransactionService assetTransactionService = null;
        try {
            assetTransactionService = new AssetTransactionService(project, logListener);
        } catch (DeploymentTargetNotConfigured deploymentTargetNotConfigured) {
            deploymentTargetNotConfigured.printStackTrace();
            showErrorMessage("Algorand Node is not configured for deployment target", "Asset Search");
            return;
        }

        final AssetTransactionService finalAssetTransactionService = assetTransactionService;
        assetSearchBtn.addActionListener(e -> {
            //clearFieldsForModifyMode(); //TODO

            beforeSearch();
            Object selectedItem = assetIdCB.getSelectedItem();
            if(selectedItem == null) {
                afterSearch();
                return;
            }

            String assetId;
            if(selectedItem instanceof AssetMeta) {
                AssetMeta assetMeta = (AssetMeta) assetIdCB.getSelectedItem();//assetIdTf.getText();
                assetId = assetMeta.getId();
            } else {
                assetId = String.valueOf(selectedItem);
            }

            assetSearchBtn.setEnabled(false);
          //TODO  setOKActionEnabled(false);

            Long lassetId;
            try {
                lassetId = Long.parseLong(assetId);
            } catch (NumberFormatException ex) {
                Messages.showErrorDialog("Invalid asset id", "Asset Search");
                assetSearchBtn.setEnabled(true);
         //TODO       setOKActionEnabled(true);
                afterSearch();
                return;
            }

            Task.Backgroundable task = new Task.Backgroundable(project, "Serach Asset") {
                Asset asset;

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    try {

                        asset = finalAssetTransactionService.getAsset(lassetId);

                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }

                @Override
                public void onThrowable(@NotNull Throwable error) {
                    if(error != null && error.getCause() != null
                            &&error.getCause() instanceof DeploymentTargetNotConfigured) {
                        showErrorMessage("Algorand Node is not configured for deployment target", "Asset Search");
                    } else {
                        showErrorMessage(String.format("Error getting asset details for asset id: %s", assetId), "Fetching Asset details");
                    }
                }

                @Override
                public void onFinished() {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        assetSearchBtn.setEnabled(true);
                 //TODO       setOKActionEnabled(true);
                    });
                }

                @Override
                public void onSuccess() {
                    if(asset != null) {
                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                //populateWithAssetInfo(asset, assetId);
                            }
                        });
                    } else {
                        showErrorMessage(String.format("Error getting asset details for asset id: %s", assetId), "Fetching Asset details");
                    }
                }

                @Override
                public void onCancel() {
                    logListener.warn("Asset search was cancelled");

                }
            };

            BackgroundableProcessIndicator processIndicator = new BackgroundableProcessIndicator(task);
            ProgressManager.getInstance().runProcessWithProgressAsynchronously(task,processIndicator);
        });
    }

    private void showErrorMessage(String message, String title) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                Messages.showErrorDialog(message, title);
            }
        }, ModalityState.any());
    }

    public Long getAssetId() {
        try {
            Object selectedItem = assetIdCB.getSelectedItem();
            if(selectedItem == null) return null;

            String assetId;
            if(selectedItem instanceof AssetMeta) {
                AssetMeta assetMeta = (AssetMeta) selectedItem;
                if (assetMeta == null || assetMeta.getId() == null)
                    return null;
                assetId = assetMeta.getId();
            } else {
                assetId = String.valueOf(selectedItem);
            }
            return Long.parseLong(StringUtil.trim(assetId));
        } catch (Exception e) {
            return null;
        }
    }

    public JPanel getMainPanel() {
        return assetChooserPanel;
    }

    protected  void beforeSearch() {

    }

    protected void afterSearch() {

    }

    public void setAssetIdLable(String text) {
        assetLabel.setText(text);
    }

    protected abstract void onSuccessfulSearch(String assetId, AssetMeta asset);

    private void createUIComponents() {
        // TODO: place custom component creation code here

    }
}
