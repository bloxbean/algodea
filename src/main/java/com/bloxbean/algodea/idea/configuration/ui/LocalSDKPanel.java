package com.bloxbean.algodea.idea.configuration.ui;

import com.bloxbean.algodea.idea.configuration.model.AlgoLocalSDK;
import com.bloxbean.algodea.idea.core.util.AlgoSdkUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.twelvemonkeys.lang.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class LocalSDKPanel {
    private final static Logger LOG = Logger.getInstance(LocalSDKPanel.class);

    private JTextField versionTf;
    private JPanel mainPanel;
    private JTextField nameTf;
    private TextFieldWithBrowseButton homeTfWithBrowserBtn;
    private JLabel errorMsgLabel;
    private JTextField homeTf;

    public LocalSDKPanel() {
        this(null);
    }

    public LocalSDKPanel(AlgoLocalSDK algoLocalSDK) {
        super();

        if(algoLocalSDK != null) {
            nameTf.setText(algoLocalSDK.getName());
            homeTf.setText(algoLocalSDK.getHome());
            versionTf.setText(algoLocalSDK.getVersion());
        }
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
            errorMsgLabel.setText(""); //reset error msg
            versionTf.setText("");

            homeTf.setText(file.getAbsolutePath());

            String version = AlgoSdkUtil.getVersionString(homeTf.getText());
            if(StringUtil.isEmpty(version)) {
                versionTf.setText("");
                //Invalid version
                //LOG.info("Invalid sdk path : " + homeTf.getText());
                errorMsgLabel.setText("<html>Invalid Algorand home. Version could not be determined. " +
                        "<br/> Make sure \'goal\' is available under $ALGORAND_HOME/bin</html>");
                errorMsgLabel.setForeground(Color.red);
                return;
            } else {
                versionTf.setText(version);
            }
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
