package com.bloxbean.algorand.idea.module;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.PathChooserDialog;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.labels.ActionLink;
import com.intellij.util.download.DownloadableFileDescription;
import com.intellij.util.download.DownloadableFileService;
import com.intellij.util.download.FileDownloader;
import com.intellij.util.io.ZipUtil;
import org.apache.commons.compress.archivers.tar.TarUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AlgoSdkPanel extends JPanel {

  public static final String LAST_USED_ALGORAND_HOME = "LAST_USED_ALGORAND_HOME";
  private ActionLink myDownloadLink;
  private JPanel myRoot;
  private AlgoSdkComboBox mySdkComboBox;
  private JLabel descLabel;

  public AlgoSdkPanel() {
    super(new BorderLayout());
    add(myRoot, BorderLayout.CENTER);
  }

  private void createUIComponents() {
    myDownloadLink = new ActionLink("Download and extract Algorand node binaries for your OS and point to the node folder", new AnAction() {
      @Override
      public void actionPerformed(AnActionEvent anActionEvent) {
        BrowserUtil.browse("https://github.com/algorand/go-algorand/releases");
      }
    });

    descLabel = new JLabel();
    descLabel.setText("Algorand node binaries are currently only available for Linux and Mac OS. " +
            "<br/>For Windows OS, you can get editor support for TEAL, but you cannot compile your TEAL file. " +
            "<br/>Please download the node_stable_<os>_<version>.tar.gz file.");
  }

  public String getSdkName() {
    final Sdk selectedSdk = mySdkComboBox.getSelectedSdk();
    return selectedSdk == null ? null : selectedSdk.getName();
  }

  public Sdk getSdk() {
    return mySdkComboBox.getSelectedSdk();
  }

  public void setSdk(Sdk sdk) {
    mySdkComboBox.getComboBox().setSelectedItem(sdk);
  }
}
