package com.bloxbean.algodea.idea.dryrun.ui;

import com.bloxbean.algodea.idea.nodeint.model.DryRunContext;
import com.bloxbean.algodea.idea.util.AlgoModuleUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.math.BigInteger;

public class DryRunSourceContextForm {
    public final static String LSIG = "lsig";
    public final static String APPROV = "approv";
    public final static String CLEARP = "clearp";

    private JTextField appIndexTf;
    private JTextField srcTxnIndexTf;
    private JComboBox sourceTypeCB;
    private TextFieldWithBrowseButton sourceFileTextBrowseTf;
    private JPanel mainPanel;

    private JTextField sourceFileTf;
    private String sourceFolder;

    private String[] types = {LSIG, APPROV, CLEARP};

    private boolean isStatefulContract;

    public DryRunSourceContextForm() {

    }

    public void initializeData(Project project, Long appId, boolean isStatefulContract) {
        this.isStatefulContract = isStatefulContract;

        srcTxnIndexTf.setText("0");

        if(appId != null) {
            appIndexTf.setText(String.valueOf(appId));
        }

        sourceFolder = AlgoModuleUtils.getFirstSourceRootPath(project);

        if(isStatefulContract)
            sourceTypeCB.removeItemAt(0); //remove lsig
        else {
            disableAppId();
            sourceTypeCB.setSelectedIndex(0); //logicsig
            sourceTypeCB.setEnabled(false);
        }
    }

    //If re-opening
    public void setSource(DryRunContext.Source source) {
        if(source == null)
            return;

        if(!StringUtil.isEmpty(source.code)) {
            sourceFileTextBrowseTf.setText(source.code);
        }

        if(!StringUtil.isEmpty(source.type)) {
            sourceTypeCB.setSelectedItem(source.type);
        }

        if(source.appIndex != null) {
            appIndexTf.setText(String.valueOf(source.appIndex));
        }

    }

    protected @Nullable ValidationInfo doValidate() {
        try {
            getSourceAppIndex();
        } catch (Exception e) {
            return new ValidationInfo("Enter valid application index. Error: " + e.getMessage(), appIndexTf);
        }

        try {
            getTxnIndex();
        } catch (Exception e) {
            return new ValidationInfo("Please provide a valid transaction index. Error: " + e.getMessage(), srcTxnIndexTf);
        }

        if(isStatefulContract) {
            String srcFile = sourceFileTf.getText();
            if(StringUtil.isEmpty(srcFile)) {
                return new ValidationInfo("Please select a valid source file", sourceFileTf);
            }
        }

        return null;
    }

    public void setTxnIndex(int index) {
        srcTxnIndexTf.setText(String.valueOf(index));
    }

    public void disableTxnIndex() {
        srcTxnIndexTf.setEditable(false);
    }

    public void disableAppId() {
        appIndexTf.setEditable(false);
    }

    public void selectSourceType(String type) {
        sourceTypeCB.setSelectedItem(type);
    }

    public void disableSourceType() {
        sourceTypeCB.setEnabled(false);
    }

    private BigInteger getSourceAppIndex() {
        return new BigInteger(StringUtil.trim(appIndexTf.getText()));
    }

    private Long getTxnIndex() {
        return Long.parseLong(StringUtil.trim(srcTxnIndexTf.getText()));
    }

    public DryRunContext.Source getDryRunSource() {
        DryRunContext.Source source = new DryRunContext.Source();

        source.code = StringUtil.trim(sourceFileTf.getText());
        source.type = (String)sourceTypeCB.getSelectedItem();
        source.appIndex = getSourceAppIndex();
        source.txnIndex = getTxnIndex();

        return source;
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
        sourceTypeCB = new ComboBox(types);

        sourceFileTf = new JTextField();
        sourceFileTextBrowseTf = new TextFieldWithBrowseButton(sourceFileTf, e -> {
            JFileChooser fc = new JFileChooser();
            if(sourceFolder != null) {
                fc.setCurrentDirectory(new File(sourceFolder));
            }
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

            sourceFileTf.setText(file.getAbsolutePath());
        });
    }

}
