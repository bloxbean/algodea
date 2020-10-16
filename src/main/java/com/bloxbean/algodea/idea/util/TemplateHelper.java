package com.bloxbean.algodea.idea.util;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Properties;

public class TemplateHelper {

    public static boolean createFile(Properties props, String outputFile, VirtualFile folder, final String templateName)
            throws Exception {
        final FileTemplate template = FileTemplateManager.getDefaultInstance().getInternalTemplate(templateName);

        String packageSrc = FileTemplateUtil.mergeTemplate(props, template.getText(), true);

        VirtualFile srcFile = folder.createChildData(TemplateHelper.class, outputFile);
        VfsUtil.saveText(srcFile, packageSrc);

        return true;
    }
}
