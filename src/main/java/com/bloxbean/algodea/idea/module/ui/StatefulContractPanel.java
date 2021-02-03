package com.bloxbean.algodea.idea.module.ui;

import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class StatefulContractPanel {
    private JPanel mainPanel;
    private JTextField approvalProgramTf;
    private JTextField clearStateProgramTf;
    private JLabel errorMessageLabel;
    private JTextField statefulContractTf;
    private JCheckBox createStfulSmartContractCB;
    private boolean enableCreateStatefulContractSelection;
    private SettingChangeListener settingChangeListener;

    public StatefulContractPanel(boolean enableCreateStatefulContractSelection) {
        this.enableCreateStatefulContractSelection = enableCreateStatefulContractSelection;
        createStfulSmartContractCB.setVisible(enableCreateStatefulContractSelection);

        if(enableCreateStatefulContractSelection) {
            statefulContractTf.setEnabled(false);
            approvalProgramTf.setEnabled(false);
            clearStateProgramTf.setEnabled(false);
        }

        createStfulSmartContractCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!enableCreateStatefulContractSelection)
                    return;

                if(createStfulSmartContractCB.isSelected()) {
                    statefulContractTf.setEnabled(true);
                    approvalProgramTf.setEnabled(true);
                    clearStateProgramTf.setEnabled(true);
                } else {
                    statefulContractTf.setEnabled(false);
                    approvalProgramTf.setEnabled(false);
                    clearStateProgramTf.setEnabled(false);
                }
            }
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public String getStatefulContractName() {
        return statefulContractTf.getText();
    }

    public String getApprovalProgram() {
        return approvalProgramTf.getText();
    }

    public String getClearStateProgram() {
        return clearStateProgramTf.getText();
    }

    public boolean isCreateStatefulContract() {
        return createStfulSmartContractCB.isSelected();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        statefulContractTf = new JBTextField("SfContract-1");
        approvalProgramTf = new JBTextField("approval_progam.teal");
        clearStateProgramTf = new JBTextField("clear_state_program.teal");

       // if(!enableCreateStatefulContractSelection) { //Only in IDEA ide
            statefulContractTf.setInputVerifier(new NotEmptyTextVerifier());
            approvalProgramTf.setInputVerifier(new FileNameInputVerifier());
            clearStateProgramTf.setInputVerifier(new FileNameInputVerifier());
        //}

        errorMessageLabel = new JLabel();
        errorMessageLabel.setText(" ");
    }

    public ValidationInfo doValidate() {
        if(!enableCreateStatefulContractSelection)
            return null;

        String contractName = getStatefulContractName();
        if(StringUtil.isEmpty(contractName)) {
            return new ValidationInfo("Contract Name cannot be empty", statefulContractTf);
        }

        String approvalProgram = getApprovalProgram();
        if(StringUtil.isEmpty(approvalProgram)) {
            return new ValidationInfo("Approval Program cannot be empty", approvalProgramTf);
        } else if(!approvalProgram.endsWith(".teal")) {
            return new ValidationInfo("Should be a valid file name and with extension .teal", approvalProgramTf);
        }

        String clearProgram = getClearStateProgram();
        if(StringUtil.isEmpty(clearProgram)) {
            return new ValidationInfo("Clear State Program cannot be empty", clearStateProgramTf);
        } else if(!clearProgram.endsWith(".teal")) {
            return new ValidationInfo("Should be a valid file name and with extension .teal", clearStateProgramTf);
        }

        return null;
    }

    public void addSettingChangeListener(SettingChangeListener settingChangeListener) {
        this.settingChangeListener = settingChangeListener;
    }

    public class FileNameInputVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            String text = ((JTextField) input).getText();
                if(isFilenameValid(text) && text.endsWith(".teal")) {
                    errorMessageLabel.setText("");
                    input.setBorder(BorderFactory.createEmptyBorder());

                    if(settingChangeListener != null)
                        settingChangeListener.settingsChanged();
                    return true;
                } else {
                    input.setBorder(BorderFactory.createLineBorder(Color.RED));
                    errorMessageLabel.setText("Should be a valid file name and with extension .teal");
                    errorMessageLabel.setForeground(Color.RED);
                    return false;
                }

        }

        public boolean isFilenameValid(String file) {
            File f = new File(file);
            try {
                return f.getCanonicalFile().getName().equals(file);
            } catch (IOException e) {
                return false;
            }
        }
    }

    public class NotEmptyTextVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            String text = ((JTextField) input).getText();
            if(!StringUtil.isEmpty(text)) {
                errorMessageLabel.setText("");
                if(settingChangeListener != null)
                    settingChangeListener.settingsChanged();
                return true;
            } else {
                errorMessageLabel.setText("Provide a name for the stateful contract");
                errorMessageLabel.setForeground(Color.RED);
                return false;
            }
        }
    }

    public interface SettingChangeListener {
        public void settingsChanged();
    }

}
