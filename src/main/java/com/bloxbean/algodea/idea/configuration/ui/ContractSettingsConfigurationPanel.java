package com.bloxbean.algodea.idea.configuration.ui;

import com.bloxbean.algodea.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algodea.idea.pkg.AlgoPkgJsonService;
import com.bloxbean.algodea.idea.pkg.exception.PackageJsonException;
import com.bloxbean.algodea.idea.pkg.model.AlgoPackageJson;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.io.FileUtil;
import com.twelvemonkeys.lang.StringUtil;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ContractSettingsConfigurationPanel {
    private JTextField statefulDeployFlagsTf;
    private JTextField appProgTf;
    private JTextField clrProgTf;
    private TextFieldWithBrowseButton clearProgramTf;
    private TextFieldWithBrowseButton approvalProgramTf;
    private JPanel mainPanel;
    private JComboBox contractNameCB;
    private String sourceRootPath;

    public void poulateData(AlgoProjectState.State state, AlgoPkgJsonService pkgJsonService) {

        //statefulCB.setSelected(state.isSupportStatefulContract());
        AlgoPackageJson packageJson = null;
        try {
            pkgJsonService.load(); //load the latest copy
            packageJson = pkgJsonService.getPackageJson();
        } catch (PackageJsonException e) {
            IdeaUtil.showNotification("Algo Package Json", "algo-package.json could not be loaded", NotificationType.ERROR, null);
        }

        if(packageJson == null)
            packageJson = new AlgoPackageJson();

        List<String> contractNames = packageJson.getStatefulContractList().stream().map(c -> c.getName()).collect(Collectors.toList());
        if(contractNames != null) {
            for (String name : contractNames) {
                contractNameCB.addItem(name);
            }
        }

        contractNameCB.addActionListener((e) -> {
            String name = (String)contractNameCB.getSelectedItem();
            if(StringUtil.isEmpty(name))
                return;

            try {
                AlgoPackageJson.StatefulContract statefulContract = pkgJsonService.getPackageJson().getStatefulContractByName(name);
                if(statefulContract != null) {
                    if(!StringUtil.isEmpty(statefulContract.getApprovalProgram()))
                        appProgTf.setText(statefulContract.getApprovalProgram());

                    if(!StringUtil.isEmpty(statefulContract.getClearStateProgram()))
                        clrProgTf.setText(statefulContract.getClearStateProgram());
                }
            } catch (PackageJsonException packageJsonException) {
                packageJsonException.printStackTrace();
                IdeaUtil.showNotification("Algo Package Json Error", "Unable to read algo-package.json file", NotificationType.ERROR, null);
                return;
            }
        });

        if(contractNames.size() > 0) {
            contractNameCB.setSelectedIndex(0);
        }
    }

    public void setSourceRootPath(String sourceRootPath) {
        this.sourceRootPath = sourceRootPath;
    }

    public String getContractName() {
        return (String)contractNameCB.getSelectedItem();
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

    public void updateDataToState(AlgoProjectState.State state, AlgoPkgJsonService algoPkgJsonService) {
        String name = getContractName();
        if(StringUtil.isEmpty(name)) return;

        try {
            AlgoPackageJson.StatefulContract sfContract = algoPkgJsonService.getStatefulContract(name);
            if (sfContract != null) {
                sfContract.setName(name);
                sfContract.setApprovalProgram(getApprovalProgram());
                sfContract.setClearStateProgram(getClearStateProgram());

                algoPkgJsonService.setStatefulContract(sfContract);
                algoPkgJsonService.save();
            }
        } catch (Exception e) {
            IdeaUtil.showNotification("Configuration", "algo-package.json could not be saved", NotificationType.ERROR, null);
        }
    }
}
