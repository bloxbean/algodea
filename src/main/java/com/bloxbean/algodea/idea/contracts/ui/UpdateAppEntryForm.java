/*
 * Copyright (c) 2020 BloxBean Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bloxbean.algodea.idea.contracts.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class UpdateAppEntryForm {
    private JPanel mainPanel;
    private TextFieldWithBrowseButton approvalProgramTf;
    private TextFieldWithBrowseButton clearStateProgramTf;
    private JTextField appProgTf;
    private JTextField clrProgTf;
    private String sourceRootPath;

    public UpdateAppEntryForm() {

    }
//    public UpdateAppEntryForm(Project project, String approvalProgram, String clearStateProgram) {
//        super();
//        initialize(project, approvalProgram, clearStateProgram);
//    }

    public void initializeData(Project project, String approvalProgram, String clearStateProgram) {
        if (!StringUtil.isEmpty(approvalProgram)) {
            appProgTf.setText(approvalProgram);
        }

        if (!StringUtil.isEmpty(clearStateProgram)) {
            clrProgTf.setText(clearStateProgram);
        }

    }

    public String getApprovalProgram() {
        return approvalProgramTf.getText();
    }

    public String getClearStateProgram() {
        return clearStateProgramTf.getText();
    }


    //    @Override
    protected @Nullable JComponent getMainPanel() {
        return mainPanel;
    }

    //    @Override
    protected @Nullable ValidationInfo doValidate() {

        if (StringUtil.isEmpty(appProgTf.getText())) {
            return new ValidationInfo("Please select a valid approval program", approvalProgramTf);
        }

        if (StringUtil.isEmpty(clrProgTf.getText())) {
            return new ValidationInfo("Please select a valid clear state program", clearStateProgramTf);
        }

        return null;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

        appProgTf = new JTextField();
        approvalProgramTf = new TextFieldWithBrowseButton(appProgTf, e -> {
            JFileChooser fc = new JFileChooser();
            if (sourceRootPath != null)
                fc.setCurrentDirectory(new File(sourceRootPath));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.getName().endsWith(".teal") || f.getName().endsWith(".TEAL"))
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

            if (!com.twelvemonkeys.lang.StringUtil.isEmpty(sourceRootPath)) {
                String relativePath = FileUtil.getRelativePath(new File(sourceRootPath), file);
                if (!com.twelvemonkeys.lang.StringUtil.isEmpty(relativePath))
                    appProgTf.setText(relativePath);
                else
                    appProgTf.setText(file.getAbsolutePath());
            }

        });

        clrProgTf = new JTextField();
        clearStateProgramTf = new TextFieldWithBrowseButton(clrProgTf, e -> {
            JFileChooser fc = new JFileChooser();
            if (sourceRootPath != null)
                fc.setCurrentDirectory(new File(sourceRootPath));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.getName().endsWith(".teal") || f.getName().endsWith(".TEAL"))
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

            if (!com.twelvemonkeys.lang.StringUtil.isEmpty(sourceRootPath)) {
                String relativePath = FileUtil.getRelativePath(new File(sourceRootPath), file);
                if (!com.twelvemonkeys.lang.StringUtil.isEmpty(relativePath))
                    clrProgTf.setText(relativePath);
                else
                    clrProgTf.setText(file.getAbsolutePath());
            }
        });
    }
}
