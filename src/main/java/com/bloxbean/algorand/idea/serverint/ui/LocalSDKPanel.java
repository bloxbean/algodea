package com.bloxbean.algorand.idea.serverint.ui;

import com.bloxbean.algorand.idea.core.util.AlgoSdkUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.twelvemonkeys.lang.StringUtil;

import javax.swing.*;
import java.io.File;

public class LocalSDKPanel {
    private final static Logger LOG = Logger.getInstance(LocalSDKPanel.class);

    private JTextField versionTf;
    private JPanel mainPanel;
    private JTextField nameTf;
    private TextFieldWithBrowseButton homeTfWithBrowserBtn;
    private JTextField homeTf;

    public LocalSDKPanel() {
        super();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public String getHome() {
        return homeTf.getText();
    }

    public String getName() {
        return nameTf.getText();
    }

    public String getVersion() {
        return versionTf.getText();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        homeTf = new JTextField();
        homeTfWithBrowserBtn = new TextFieldWithBrowseButton(homeTf, e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.showDialog(mainPanel, "Select");
            File file = fc.getSelectedFile();
            if (file == null) {
                return;
            }
            homeTf.setText(file.getAbsolutePath());

            String version = AlgoSdkUtil.getVersionString(homeTf.getText());
            if(StringUtil.isEmpty(version)) {
                //Invalid version
                LOG.info("Invalid sdk path : " + homeTf.getText());
                return;
            }
            versionTf.setText(version);
            //Get goal version.
        });
    }

    public JTextField getVersionTf() {
        return versionTf;
    }

    public JTextField getNameTf() {
        return nameTf;
    }

    public JTextField getHomeTf() {
        return homeTf;
    }

}
