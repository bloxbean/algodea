package com.bloxbean.algorand.idea.configuration.ui;

import com.bloxbean.algorand.idea.configuration.service.AlgoProjectState;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.twelvemonkeys.lang.StringUtil;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ContractSettingsConfigurationPanel {
    private JCheckBox statefulCB;
    private JTextField statefulDeployFlagsTf;
    private JTextField appProgTf;
    private JTextField clrProgTf;
    private TextFieldWithBrowseButton clearProgramTf;
    private TextFieldWithBrowseButton approvalProgramTf;
    private JPanel mainPanel;
    private String sourceRootPath;

    public void poulateData(AlgoProjectState.State state) {
        if(!StringUtil.isEmpty(state.getApprovalProgramName())) {
            appProgTf.setText(state.getApprovalProgramName());
        }

        if(!StringUtil.isEmpty(state.getClearStateProgramName())) {
            clrProgTf.setText(state.getClearStateProgramName());
        }

        if(!StringUtil.isEmpty(state.getStatefulDeployArgs())) {
            statefulDeployFlagsTf.setText(state.getStatefulDeployArgs());
        }

        statefulCB.setSelected(state.isSupportStatefulContract());
    }

    public void setSourceRootPath(String sourceRootPath) {
        this.sourceRootPath = sourceRootPath;
    }

    public boolean isStatefulSupportEnabled() {
        return statefulCB.isSelected();
    }

    public String getApprovalProgram() {
        return appProgTf.getText();
    }

    public String getClearStateProgram() {
        return clrProgTf.getText();
    }

    public String getStatefulDeployFlags() {
        return statefulDeployFlagsTf.getText();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        appProgTf = new JTextField();
        approvalProgramTf = new TextFieldWithBrowseButton(appProgTf, e -> {
            JFileChooser fc = new JFileChooser();
            if(sourceRootPath != null)
                fc.setCurrentDirectory(new File(sourceRootPath));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if(f.getName().endsWith(".teal") || f.getName().endsWith(".TEAL"))
                        return true;
                    else
                        return false;
                }

                @Override
                public String getDescription() {
                    return "TEAL file";
                }
            });

            fc.showDialog(mainPanel, "Select");
            File file = fc.getSelectedFile();
            if (file == null) {
                return;
            }

            if(!StringUtil.isEmpty(sourceRootPath)) {
                String relativePath = FileUtil.getRelativePath(new File(sourceRootPath), file);
                if(!StringUtil.isEmpty(relativePath))
                    appProgTf.setText(relativePath);
                else
                    appProgTf.setText(file.getAbsolutePath());
            }

        });

        clrProgTf = new JTextField();
        clearProgramTf = new TextFieldWithBrowseButton(clrProgTf, e -> {
            JFileChooser fc = new JFileChooser();
            if(sourceRootPath != null)
                fc.setCurrentDirectory(new File(sourceRootPath));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if(f.getName().endsWith(".teal") || f.getName().endsWith(".TEAL"))
                        return true;
                    else
                        return false;
                }

                @Override
                public String getDescription() {
                    return "TEAL file";
                }
            });
            fc.showDialog(mainPanel, "Select");
            File file = fc.getSelectedFile();
            if (file == null) {
                return;
            }

            if(!StringUtil.isEmpty(sourceRootPath)) {
                String relativePath = FileUtil.getRelativePath(new File(sourceRootPath), file);
                if(!StringUtil.isEmpty(relativePath))
                    clrProgTf.setText(relativePath);
                else
                    clrProgTf.setText(file.getAbsolutePath());
            }
        });
    }

    public void updateDataToState(AlgoProjectState.State state) {
        state.setSupportStatefulContract(isStatefulSupportEnabled());
        if(isStatefulSupportEnabled()) {
            state.setApprovalProgramName(getApprovalProgram());
            state.setClearStateProgramName(getClearStateProgram());
            state.setStatefulDeployArgs(getStatefulDeployFlags());
        } else {
            state.setApprovalProgramName("");
            state.setClearStateProgramName("");
        }
    }
}
