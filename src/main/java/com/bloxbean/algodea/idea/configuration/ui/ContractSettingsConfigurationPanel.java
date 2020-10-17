package com.bloxbean.algodea.idea.configuration.ui;

import com.bloxbean.algodea.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algodea.idea.module.AlgoModuleConstant;
import com.bloxbean.algodea.idea.pkg.AlgoPkgJsonService;
import com.bloxbean.algodea.idea.pkg.exception.PackageJsonException;
import com.bloxbean.algodea.idea.pkg.model.AlgoPackageJson;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ContractSettingsConfigurationPanel {
    private JTextField appProgTf;
    private JTextField clrProgTf;
    private TextFieldWithBrowseButton clearProgramTf;
    private TextFieldWithBrowseButton approvalProgramTf;
    private JPanel mainPanel;
    private JComboBox contractNameCB;
    private JButton newContractBtn;
    private JButton newContractResetBtn;
    private String sourceRootPath;
    private boolean newContractAdd = false;

    private AlgoPkgJsonService algoPkgJsonService;

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
            name = StringUtil.trim(name);
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

        newContractResetBtn.setEnabled(false);

        newContractBtn.addActionListener(evt -> {
            newContractAdd = true;
            newContractBtn.setEnabled(false);
            newContractResetBtn.setEnabled(true);
            contractNameCB.setEditable(true);

            contractNameCB.insertItemAt("", 0);
            contractNameCB.setSelectedIndex(0);

            appProgTf.setText("");
            clrProgTf.setText("");
        });

        newContractResetBtn.addActionListener(e -> {
            newContractAdd = false;
            newContractResetBtn.setEnabled(false);
            newContractBtn.setEnabled(true);
            contractNameCB.setEditable(false);

            if(contractNameCB.getModel().getSize() > 0)
                contractNameCB.removeItemAt(0);

            if(contractNameCB.getModel().getSize() > 0)
                contractNameCB.setSelectedIndex(0);
        });
    }

    public void setSourceRootPath(String sourceRootPath) {
        this.sourceRootPath = sourceRootPath;
    }

    public String getContractName() {
        return StringUtil.trim((String)contractNameCB.getSelectedItem());
    }

    public String getApprovalProgram() {
        return StringUtil.trim(appProgTf.getText());
    }

    public String getClearStateProgram() {
        return StringUtil.trim(clrProgTf.getText());
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public ValidationInfo doValidate() {
        if(!newContractAdd) return null; //validation disabled for existing contracts.

        if(algoPkgJsonService == null) return null;

        String contractName = getContractName();
        try {
            AlgoPackageJson.StatefulContract sfContract
                    = algoPkgJsonService.getStatefulContract(StringUtil.trim(contractName));
            if(sfContract != null) {
                return new ValidationInfo("A stateful contract with same name already exists", contractNameCB);
            }
        } catch (PackageJsonException e) {
            e.printStackTrace();
            return null;
        }
        return null;
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
            if (sfContract != null) { //Update an existing contract
                sfContract.setName(name);
                sfContract.setApprovalProgram(getApprovalProgram());
                sfContract.setClearStateProgram(getClearStateProgram());

                algoPkgJsonService.setStatefulContract(sfContract);
                algoPkgJsonService.save();
            } else { //New contract
                AlgoPackageJson.StatefulContract statefulContract = new AlgoPackageJson.StatefulContract();
                statefulContract.setName(name);
                statefulContract.setApprovalProgram(getApprovalProgram());
                statefulContract.setClearStateProgram(getClearStateProgram());

                algoPkgJsonService.setStatefulContract(statefulContract);
                algoPkgJsonService.save();

                IdeaUtil.showNotification("Configuration", "New stateful contract added to " + AlgoModuleConstant.ALGO_PACKAGE_JSON, NotificationType.INFORMATION, null);
            }
        } catch (Exception e) {
            IdeaUtil.showNotification("Configuration", "algo-package.json could not be saved", NotificationType.ERROR, null);
        }
    }
}
