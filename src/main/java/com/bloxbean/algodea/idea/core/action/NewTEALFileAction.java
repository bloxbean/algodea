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

package com.bloxbean.algodea.idea.core.action;

import com.bloxbean.algodea.idea.core.action.util.AlgoFileTemplateUtil;
import com.bloxbean.algodea.idea.module.AlgorandModuleType;
import com.bloxbean.algodea.idea.common.AlgoIcons;
import com.bloxbean.algodea.idea.pkg.AlgoPkgJsonService;
import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.actions.CreateFromTemplateAction;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Properties;

public class NewTEALFileAction extends CreateFromTemplateAction<PsiFile> {

    public NewTEALFileAction() {
        super("TEAL File", null, AlgoIcons.TEAL_FILE_ICON);
    }

    @Override
    protected boolean isAvailable(DataContext dataContext) {

        final Module module = LangDataKeys.MODULE.getData(dataContext);
        final ModuleType moduleType = module == null ? null : ModuleType.get(module);
        boolean isAlgorandModule = moduleType instanceof AlgorandModuleType;

        if(!isAlgorandModule) { //For non algorand modules. Check if algo-package.json available.
            AlgoPkgJsonService pkgJsonService = AlgoPkgJsonService.getInstance(module.getProject());
            if (pkgJsonService != null)
                isAlgorandModule = pkgJsonService.isAlgoProject();
        }

        return super.isAvailable(dataContext) && isAlgorandModule;
    }

    @Override
    protected String getActionName(PsiDirectory directory, String newName, String templateName) {
        return "Creating File " + newName;
    }

    @Override
    protected void buildDialog(Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
//        builder.setTitle(IdeBundle.message("action.create.new.class"));
        builder.setTitle("Create new TEAL file");
        for (FileTemplate fileTemplate : AlgoFileTemplateUtil.getApplicableTemplates()) {
            final String templateName = fileTemplate.getName();
            final String shortName = AlgoFileTemplateUtil.getTemplateShortName(templateName);
            final Icon icon = AllIcons.FileTypes.Any_type;
            builder.addKind(shortName, icon, templateName);
        }
        builder.setValidator(new InputValidatorEx() {
            @Override
            public String getErrorText(String inputString) {
                if (inputString.length() > 0 && !StringUtil.isJavaIdentifier(inputString)) {
                    return "This is not a valid TEAL file name";
                }
                return null;
            }

            @Override
            public boolean checkInput(String inputString) {
                return true;
            }

            @Override
            public boolean canClose(String inputString) {
                return !StringUtil.isEmptyOrSpaces(inputString) && getErrorText(inputString) == null;
            }
        });
    }

    @Nullable
    @Override
    protected PsiFile createFile(String tealName, String templateName, PsiDirectory dir) {
        try {
            return createFile(tealName, dir, templateName).getContainingFile();
        }
        catch (Exception e) {
            LOG.error("Unable to create teal file");
            throw new RuntimeException(e);
        }
    }

    private static PsiElement createFile(String className, @NotNull PsiDirectory directory, final String templateName)
            throws Exception {
        final Properties props = new Properties(FileTemplateManager.getDefaultInstance().getDefaultProperties());
        props.setProperty(FileTemplate.ATTRIBUTE_NAME, className);

        final FileTemplate template = FileTemplateManager.getDefaultInstance().getInternalTemplate(templateName);

        return FileTemplateUtil.createFromTemplate(template, className, props, directory, NewTEALFileAction.class.getClassLoader());
    }
}
