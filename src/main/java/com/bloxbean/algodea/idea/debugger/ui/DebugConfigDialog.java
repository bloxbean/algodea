package com.bloxbean.algodea.idea.debugger.ui;

import com.bloxbean.algodea.idea.debugger.service.DebugConfigState;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

public class DebugConfigDialog extends DialogWrapper {
    private JPanel panel;
    private TextFieldWithBrowseButton chromeExecPathBrowseBtn;
    private JTextField chromeExecPath;
    private JButton browseButton;
    private JTextField debugPortTf;
    private JCheckBox autoDetectCB;

    public DebugConfigDialog() {
        super(true);
        init();
        setTitle("Debugger Config");

        initData();
    }

    public void initData() {
        debugPortTf.setText("9229");
        debugPortTf.setEditable(false);

        loadDebugConfig();

        autoDetectCB.addActionListener(actionEvent -> {
            if(autoDetectCB.isSelected()) {
                chromeExecPath.setText("");
                chromeExecPathBrowseBtn.setEnabled(false);
                browseButton.setEnabled(false);
            } else {
                chromeExecPath.setText("");
                chromeExecPathBrowseBtn.setEnabled(true);
                browseButton.setEnabled(true);
            }
        });
    }

    private void loadDebugConfig() {
        DebugConfigState debugConfig = DebugConfigState.getInstance();

        boolean autoDetect = debugConfig.isAutoDetectChromePath();
        if(autoDetect) {
            autoDetectCB.setSelected(true);
            chromeExecPath.setText("");
            chromeExecPathBrowseBtn.setEnabled(false);
        } else {
            autoDetectCB.setSelected(false);
            if(!StringUtil.isEmpty(debugConfig.getChromeExecPath())) {
                chromeExecPath.setText(debugConfig.getChromeExecPath());
            }
            chromeExecPathBrowseBtn.setEnabled(true);
        }

        String debugPort = debugConfig.getDebugPort();
        if(!StringUtil.isEmpty(debugPort)) {
            try {
                int port = Integer.parseInt(debugPort);
                debugPortTf.setText(debugPort);
            } catch (Exception e) {
            }
        }
    }

    public void storeDebugConfig() {
        DebugConfigState debugConfig = DebugConfigState.getInstance();
        boolean autoDetect = isAutoDetectChromePath();
        if(autoDetect) {
            debugConfig.setAutoDetectChromePath(true);
            debugConfig.setChromeExecPath("");
        } else {
            debugConfig.setAutoDetectChromePath(false);
            debugConfig.setChromeExecPath(getChromeExecPath());
        }

        String debugPort = getDebugPort();
        if(debugPort != null) {
            debugConfig.setDebugPort(debugPort);
        }
    }

    public boolean isAutoDetectChromePath() {
        return autoDetectCB.isSelected();
    }

    public String getChromeExecPath() {
        if(autoDetectCB.isSelected())
            return null;
        else
            return chromeExecPath.getText();
    }

    public String getDebugPort() {
        try {
            Integer.parseInt(debugPortTf.getText());
            return debugPortTf.getText();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return panel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        chromeExecPath = new JTextField();
        chromeExecPathBrowseBtn = new TextFieldWithBrowseButton(chromeExecPath, e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.showDialog(panel, "Select");
            File file = fc.getSelectedFile();
            if (file == null) {
                return;
            }

            chromeExecPath.setText(file.getAbsolutePath());
        });
    }
}
