package com.bloxbean.algodea.idea.configuration.ui;

import com.bloxbean.algodea.idea.configuration.model.AlgoLocalSDK;
import com.bloxbean.algodea.idea.core.util.AlgoSdkUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.SystemInfo;
import com.twelvemonkeys.lang.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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

        homeTf.setToolTipText("<html>Folder where the \'goal\' binary is available. \n Example: AlgorandNode folder or AlgorandNode/bin folder</html>");

        homeTf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                checkGoalExecutable();
            }
        });
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

            checkGoalExecutable();
        });
    }

    private void checkGoalExecutable() {
        errorMsgLabel.setText(""); //reset error msg
        versionTf.setText("");

        if(!new File(homeTf.getText() + File.separator + getGoalCommand()).exists()) {
            versionTf.setText("");
            printError("<html>\'goal\' was not found. Please make sure \'goal\' file is available under the selected folder.</html>");
            return;
        }

        String version = null;
        try {
            version = AlgoSdkUtil.getVersionString(homeTf.getText());
        } catch (Exception exception) {
            versionTf.setText("");
            printError(exception.getMessage());
            return;
        }
        if(StringUtil.isEmpty(version)) {
            versionTf.setText("");
            //Invalid version
            printError("<html>Invalid Algorand Binary folder. Version could not be determined. " +
                    "<br/> Make sure \'goal\' is available under the selected folder</html>");
            return;
        } else {
            versionTf.setText(version);
        }
    }

    private void printError(String msg) {
        errorMsgLabel.setText(msg);
        errorMsgLabel.setForeground(Color.red);
    }

    private String getGoalCommand() {
        String goalCmd = "goal";
        if(SystemInfo.isWindows)
            goalCmd = "goal.exe";

        return goalCmd;
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
