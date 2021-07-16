package com.bloxbean.algodea.idea.configuration.ui;

import com.bloxbean.algodea.idea.configuration.service.AlgoProjectState;
import com.bloxbean.algodea.idea.core.action.util.AlgoContractModuleHelper;
import com.bloxbean.algodea.idea.module.AlgoModuleConstant;
import com.bloxbean.algodea.idea.pkg.AlgoPkgJsonService;
import com.bloxbean.algodea.idea.pkg.exception.PackageJsonException;
import com.bloxbean.algodea.idea.pkg.model.AlgoPackageJson;
import com.bloxbean.algodea.idea.util.IOUtil;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ContractSettingsConfigurationPanel {
    private final static Logger LOG = Logger.getInstance(ContractSettingsConfigurationPanel.class);

    private JTextField appProgTf;
    private JTextField clrProgTf;
    private TextFieldWithBrowseButton clearProgramTf;
    private TextFieldWithBrowseButton approvalProgramTf;
    private JPanel mainPanel;
    private JComboBox contractNameCB;
    private JButton newContractBtn;
    private JButton newContractResetBtn;
    private JTextField globalByteslicesTf;
    private JTextField globalIntTf;
    private JTextField localByteslicesTf;
    private JTextField localIntsTf;
    private JTextField extraPagesTf;
    private boolean newContractAdd = false;

    private AlgoPkgJsonService algoPkgJsonService;
    private String projectFolder;
    private String sourceFolder;

    public void poulateData(AlgoPkgJsonService pkgJsonService) {

        //statefulCB.setSelected(state.isSupportStatefulContract());
        this.algoPkgJsonService = pkgJsonService;
        AlgoPackageJson packageJson = null;
        try {
            pkgJsonService.load(); //load the latest copy
            packageJson = this.algoPkgJsonService.getPackageJson();
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
                    else
                        appProgTf.setText("");

                    if(!StringUtil.isEmpty(statefulContract.getClearStateProgram()))
                        clrProgTf.setText(statefulContract.getClearStateProgram());
                    else
                        clrProgTf.setText("");

                    globalByteslicesTf.setText(String.valueOf(statefulContract.getGlobalByteSlices()));
                    globalIntTf.setText(String.valueOf(statefulContract.getGlobalInts()));
                    localByteslicesTf.setText(String.valueOf(statefulContract.getLocalByteSlices()));
                    localIntsTf.setText(String.valueOf(statefulContract.getLocalInts()));
                    extraPagesTf.setText(String.valueOf(statefulContract.getExtraPages()));
                }
            } catch (PackageJsonException packageJsonException) {
                if(LOG.isDebugEnabled()) {
                    LOG.warn(packageJsonException);
                }

                IdeaUtil.showNotification("Algo Package Json Error", "Unable to read algo-package.json file : "
                        + packageJsonException.getMessage(), NotificationType.ERROR, null);
                return;
            }
        });

        if(contractNames.size() > 0) {
            contractNameCB.setSelectedIndex(0);
        }

        newContractResetBtn.setEnabled(false);

        newContractBtn.addActionListener(evt -> {
            newContractButtonClicked();
        });

        newContractResetBtn.addActionListener(e -> {
            resetNewContractButtonClicked();
        });
    }

    public void resetNewContractButtonClicked() {
        newContractAdd = false;
        newContractResetBtn.setEnabled(false);
        newContractBtn.setEnabled(true);
        contractNameCB.setEditable(false);

        if(contractNameCB.getModel().getSize() > 0)
            contractNameCB.removeItemAt(0);

        if(contractNameCB.getModel().getSize() > 0)
            contractNameCB.setSelectedIndex(0);
    }

    public void newContractButtonClicked() {
        newContractAdd = true;
        newContractBtn.setEnabled(false);
        newContractResetBtn.setEnabled(true);
        contractNameCB.setEditable(true);

        contractNameCB.insertItemAt("", 0);
        contractNameCB.setSelectedIndex(0);

        appProgTf.setText("");
        clrProgTf.setText("");

        globalByteslicesTf.setText("1");
        globalIntTf.setText("1");
        localByteslicesTf.setText("1");
        localIntsTf.setText("1");
        extraPagesTf.setText("0");
    }

    public void setProjectFolder(String projectFolder) {
        this.projectFolder = projectFolder;
    }

    public void setSourceFolder(String sourceFolder) {
        this.sourceFolder = sourceFolder;
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

    public int getGlobalByteslices() {
        try {
            return Integer.parseInt(StringUtil.trim(globalByteslicesTf.getText()));
        } catch (Exception e) {
            return 0;
        }
    }

    public int getGlobalInts() {
        try {
            return Integer.parseInt(StringUtil.trim(globalIntTf.getText()));
        } catch (Exception e) {
            return 0;
        }
    }

    public int getLocalByteslices() {
        try {
            return Integer.parseInt(StringUtil.trim(localByteslicesTf.getText()));
        } catch (Exception e) {
            return 0;
        }
    }

    public int getLocalInts() {
        try {
            return Integer.parseInt(StringUtil.trim(localIntsTf.getText()));
        } catch (Exception e) {
            return 0;
        }
    }

    public int getExtraPages() {
        try {
            return Integer.parseInt(StringUtil.trim(extraPagesTf.getText()));
        } catch (Exception e) {
            return 0;
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public ValidationInfo doValidate() {
        if (algoPkgJsonService == null) return null;

        if(contractNameCB.getModel().getSize() == 0) //No validation required.
            return null;

        if(StringUtil.isEmpty(getContractName())) {
            return new ValidationInfo("Contract Name cannot be empty", contractNameCB);
        }

        if(newContractAdd) {
            String contractName = getContractName();
            try {
                AlgoPackageJson.StatefulContract sfContract
                        = algoPkgJsonService.getStatefulContract(StringUtil.trim(contractName));
                if (sfContract != null) {
                    return new ValidationInfo("A stateful contract with same name already exists", contractNameCB);
                }
            } catch (PackageJsonException e) {
                if(LOG.isDebugEnabled()) {
                    LOG.warn(e);
                }
                return null;
            }
        }

        if(StringUtil.isEmpty(getApprovalProgram())) {
            return new ValidationInfo("Approval Program cannot be empty", appProgTf);
        }

        if(StringUtil.isEmpty(getClearStateProgram())) {
            return new ValidationInfo("Clear State program cannot be empty", clrProgTf);
        }

        if(!NumberUtils.isNumber(globalByteslicesTf.getText())) {
            return new ValidationInfo("Invalid Global Byteslices. Integer value expected.", globalByteslicesTf);
        }

        if(!NumberUtils.isNumber(globalIntTf.getText())) {
            return new ValidationInfo("Invalid Global Ints. Integer value expected.", globalIntTf);
        }

        if(!NumberUtils.isNumber(localByteslicesTf.getText())) {
            return new ValidationInfo("Invalid Local Byteslices. Integer value expected.", localByteslicesTf);
        }

        if(!NumberUtils.isNumber(localIntsTf.getText())) {
            return new ValidationInfo("Invalid Local Ints. Integer value expected.", localIntsTf);
        }

        if(!NumberUtils.isNumber(extraPagesTf.getText())) {
            return new ValidationInfo("Invalid Extra Pages. Integer value expected.", extraPagesTf);
        }

        return null;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        appProgTf = new JTextField();
        approvalProgramTf = new TextFieldWithBrowseButton(appProgTf, e -> {
            JFileChooser fc = new JFileChooser();
            if(sourceFolder != null)
                fc.setCurrentDirectory(new File(sourceFolder));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if(f.getName().endsWith(".teal") || f.getName().endsWith(".TEAL")
                            || f.getName().endsWith(".py") || f.getName().endsWith(".PY"))
                        return true;
                    else
                        return false;
                }

                @Override
                public String getDescription() {
                    return "TEAL / PyTeal file";
                }
            });

            fc.showDialog(mainPanel, "Select");
            File file = fc.getSelectedFile();
            if (file == null) {
                return;
            }

            if(!StringUtil.isEmpty(projectFolder)) {
                String relativePath = getRelativePathForSelectedSource(file);

                if(!StringUtil.isEmpty(relativePath))
                    appProgTf.setText(relativePath);
                else
                    appProgTf.setText(file.getAbsolutePath());
            }

        });

        clrProgTf = new JTextField();
        clearProgramTf = new TextFieldWithBrowseButton(clrProgTf, e -> {
            JFileChooser fc = new JFileChooser();
            if(sourceFolder != null)
                fc.setCurrentDirectory(new File(sourceFolder));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if(f.getName().endsWith(".teal") || f.getName().endsWith(".TEAL")
                            || f.getName().endsWith(".py") || f.getName().endsWith(".PY"))
                        return true;
                    else
                        return false;
                }

                @Override
                public String getDescription() {
                    return "TEAL / PyTeal file";
                }
            });
            fc.showDialog(mainPanel, "Select");
            File file = fc.getSelectedFile();
            if (file == null) {
                return;
            }

            if(!StringUtil.isEmpty(projectFolder)) {
                String relativePath = getRelativePathForSelectedSource(file);

                if(!StringUtil.isEmpty(relativePath))
                    clrProgTf.setText(relativePath);
                else
                    clrProgTf.setText(file.getAbsolutePath());
            }
        });
    }

    @Nullable
    private String getRelativePathForSelectedSource(File selectedFile) {
        String relativePath = null;

        if(selectedFile.getName().endsWith(".py") || selectedFile.getName().endsWith(".PY")) { //If python, set generated-src folder.
            String relativePathFromSrcRoot = FileUtil.getRelativePath(new File(sourceFolder), selectedFile);
            relativePath = AlgoContractModuleHelper.GENERATED_SRC + "/" + relativePathFromSrcRoot;

            relativePath = IOUtil.convertExtensionPyToTEAL(relativePath);
        } else {
            relativePath = FileUtil.getRelativePath(new File(projectFolder), selectedFile);
        }

        return relativePath;
    }

    public void updateDataToState(AlgoPkgJsonService algoPkgJsonService) {
        String name = getContractName();
        if(StringUtil.isEmpty(name)) return;

        try {
            AlgoPackageJson.StatefulContract sfContract = algoPkgJsonService.getStatefulContract(name);
            if (sfContract != null) { //Update an existing contract
                sfContract.setName(name);
                sfContract.setApprovalProgram(getApprovalProgram());
                sfContract.setClearStateProgram(getClearStateProgram());

                sfContract.setGlobalByteSlices(getGlobalByteslices());
                sfContract.setGlobalInts(getGlobalInts());
                sfContract.setLocalByteSlices(getLocalByteslices());
                sfContract.setLocalInts(getLocalInts());
                sfContract.setExtraPages(getExtraPages());

                algoPkgJsonService.setStatefulContract(sfContract);
                algoPkgJsonService.save();
            } else { //New contract
                AlgoPackageJson.StatefulContract statefulContract = new AlgoPackageJson.StatefulContract();
                statefulContract.setName(name);
                statefulContract.setApprovalProgram(getApprovalProgram());
                statefulContract.setClearStateProgram(getClearStateProgram());

                statefulContract.setGlobalByteSlices(getGlobalByteslices());
                statefulContract.setGlobalInts(getGlobalInts());
                statefulContract.setLocalByteSlices(getLocalByteslices());
                statefulContract.setLocalInts(getLocalInts());
                statefulContract.setExtraPages(getExtraPages());

                algoPkgJsonService.setStatefulContract(statefulContract);
                algoPkgJsonService.save();

                IdeaUtil.showNotification("Configuration", "New stateful contract added to " + AlgoModuleConstant.ALGO_PACKAGE_JSON, NotificationType.INFORMATION, null);
            }
        } catch (Exception e) {
            IdeaUtil.showNotification("Configuration", "algo-package.json could not be saved", NotificationType.ERROR, null);
        }
    }
}
