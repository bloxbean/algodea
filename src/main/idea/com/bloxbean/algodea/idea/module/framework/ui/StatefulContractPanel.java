package com.bloxbean.algodea.idea.module.framework.ui;

import com.intellij.ui.components.JBTextField;
import com.twelvemonkeys.lang.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class StatefulContractPanel {
    private JPanel mainPanel;
    private JTextField approvalProgramTf;
    private JTextField clearStateProgramTf;
    private JLabel errorMessageLabel;
    private JTextField statefulContractTf;

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

    private void createUIComponents() {
        // TODO: place custom component creation code here
        statefulContractTf = new JBTextField("SfContract-1");
        approvalProgramTf = new JBTextField("approval_progam.teal");
        clearStateProgramTf = new JBTextField("clear_state_program.teal");

        statefulContractTf.setInputVerifier(new NotEmptyTextVerifier());
        approvalProgramTf.setInputVerifier(new FileNameInputVerifier());
        clearStateProgramTf.setInputVerifier(new FileNameInputVerifier());

        errorMessageLabel = new JLabel();
        errorMessageLabel.setText(" ");
    }

    public class FileNameInputVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            String text = ((JTextField) input).getText();
                if(isFilenameValid(text) && text.endsWith(".teal")) {
                    errorMessageLabel.setText("");
                    input.setBorder(BorderFactory.createEmptyBorder());
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
                return true;
            } else {
                errorMessageLabel.setText("Provide a name for the stateful contract");
                errorMessageLabel.setForeground(Color.RED);
                return false;
            }
        }
    }

}
