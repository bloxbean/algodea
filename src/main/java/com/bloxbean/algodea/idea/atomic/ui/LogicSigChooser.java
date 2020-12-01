package com.bloxbean.algodea.idea.atomic.ui;

import com.algorand.algosdk.crypto.LogicsigSignature;
import com.bloxbean.algodea.idea.core.action.util.AlgoContractModuleHelper;
import com.bloxbean.algodea.idea.nodeint.model.LogicSigType;
import com.bloxbean.algodea.idea.nodeint.util.AlgoLogicsigUtil;
import com.bloxbean.algodea.idea.util.StringUtility;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class LogicSigChooser {
    private static final Logger LOG = Logger.getInstance(LogicSigChooser.class);

    private JTextField logicSigType;
    private JPanel mainPanel;
    private TextFieldWithBrowseButton senderLogicSigTextFieldWithBrowse;
    private JLabel logicSigLabel;
    private JLabel typeLabel;
    private JTextField senderLogSigTf;
    private String buildFolder;
    private String lsigPath;
    private boolean contractAccountType;
    private boolean delegatedSignatureType;
    private LogicsigSignature logicsigSignature;

    public LogicSigChooser() {
    }

    public void initialize(Project project, Module module) {
        //This is to align with account chooser label padding. Check accout chooser before changing this value
        logicSigLabel.setText(StringUtility.padLeft("Logic Sig File", 20));
        typeLabel.setText(StringUtility.padLeft("Type", 20));

        buildFolder = AlgoContractModuleHelper.getBuildFolder(project, module);
    }

    public String getLsigPath() {
        return lsigPath;
    }

    public boolean isContractAccountType() {
        return contractAccountType;
    }

    public boolean isAccountDelegationType() {
        return delegatedSignatureType;
    }

    private void loadLogicSigFile(String lsigPath) {
        logicSigType.setText("Loading Logic sig ...");
        this.lsigPath = lsigPath;
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    logicsigSignature = AlgoLogicsigUtil.getLogicSigFromFile(lsigPath);
                    LogicSigType type = AlgoLogicsigUtil.getType(logicsigSignature);

                    if(type != null) {
                        if (LogicSigType.DELEGATION_ACCOUNT.equals(type)) {
                            if(AlgoLogicsigUtil.isMultisigDelegatedAccount(logicsigSignature)) {
                                logicSigType.setText("Delegated Signature(Multi-sig) Logic Sig");
                            } else {
                                logicSigType.setText("Delegated Signature Logic Sig");
                            }
                            delegatedSignatureType = true;

                        } else if(LogicSigType.CONTRACT_ACCOUNT.equals(type)) {

                            String contractAddress = logicsigSignature.toAddress().toString();
                            if(!StringUtil.isEmpty(contractAddress)) {
                                logicSigType.setText("Contract Account ");
                                contractAccountType = true;
                            }
                        }
                    }
                } catch (Exception e) {
                    if(LOG.isDebugEnabled())
                        LOG.warn(e);
                    logicsigSignature = null;
                }
            }
        }, ModalityState.any());
    }

    public LogicsigSignature getLogicsigSignature() {
        return logicsigSignature;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        senderLogSigTf = new JTextField();
        senderLogicSigTextFieldWithBrowse = new TextFieldWithBrowseButton(senderLogSigTf, e -> {
            JFileChooser fc = new JFileChooser();
            if(buildFolder != null) {
                File lsigFolder = new File(buildFolder, "lsigs");
                if(lsigFolder.exists()) {
                    fc.setCurrentDirectory(lsigFolder);
                } else {
                    fc.setCurrentDirectory(new File(buildFolder));
                }
            }
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if(f.getName().endsWith(".lsig") || f.getName().endsWith(".LSIG"))
                        return true;
                    else
                        return false;
                }

                @Override
                public String getDescription() {
                    return "Logic Sig file";
                }
            });
            fc.showDialog(mainPanel, "Select");
            File file = fc.getSelectedFile();
            if (file == null) {
                return;
            }

            if(!StringUtil.isEmpty(buildFolder)) {
                senderLogSigTf.setText(file.getAbsolutePath());
                loadLogicSigFile(file.getAbsolutePath());
            }
        });

    }
}
