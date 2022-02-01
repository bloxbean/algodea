package com.bloxbean.algodea.idea.codegen.service;

import com.bloxbean.algodea.idea.codegen.CodeGenLang;
import com.bloxbean.algodea.idea.codegen.service.exception.CodeGenerationException;
import com.bloxbean.algodea.idea.core.action.util.AlgoContractModuleHelper;
import com.bloxbean.algodea.idea.nodeint.service.LogListener;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SdkCodeGenExportUtil {

    public static void writeGeneratedCodeFile(List<FileContent> fileContentList, String targetFolder, LogListener logListener)
            throws CodeGenerationException {

        if (StringUtil.isEmpty(targetFolder))
            throw new CodeGenerationException("Target folder can't be null");

        try {
            VirtualFile targetVFolder = VfsUtil.createDirectories(targetFolder);
            if (targetVFolder == null)
                throw new CodeGenerationException("Target folder cannot be created");

            for (FileContent fileContent : fileContentList) {
                if (fileContent.isSkipIfExists()) {
                    String fileName = fileContent.getFileName().endsWith(fileContent.getExtension())?
                            fileContent.getFileName(): fileContent.getFileName() + fileContent.getExtension();

                    //check if file exists
                    if (targetVFolder.findChild(fileName) != null) {
                        logListener.info("File exists. Skipping ... " + fileName);
                        continue;
                    }
                }

                String outputFileName = getOutputFileName(targetVFolder, fileContent.getFileName(), fileContent.getExtension(), logListener);
                ApplicationManager.getApplication().runWriteAction(() -> {
                    try {
                        VirtualFile outputFile = targetVFolder.findOrCreateChildData(targetFolder, outputFileName);
                        outputFile.setBinaryContent(fileContent.getContent().getBytes(StandardCharsets.UTF_8));
                        logListener.info("Generated file : " + outputFile.getCanonicalPath());
                    } catch (Exception e) {
                        logListener.error("Code Generation failed", e);
                    }
                });
            }

        } catch (CodeGenerationException | IOException e) {
            e.printStackTrace();
            logListener.error("Code Generation failed", e);
        }
    }

    public static String getSdkCodeGenerationFolder(Project project, Module module, CodeGenLang lang, LogListener logListener) {
        VirtualFile genFolderVf = AlgoContractModuleHelper.getGeneratedSourceFolder(project, module, true);

        if (genFolderVf == null) {
            logListener.error("Target code generation folder not found");
            return null;
        }

        String path = genFolderVf.getCanonicalPath();
        return path + File.separator + "client" + File.separator + lang.toString().toLowerCase();
    }

    private static String getOutputFileName(VirtualFile folder, String outputFileName, String extension, LogListener logListener) {
        if (outputFileName != null && outputFileName.endsWith(extension))
            outputFileName = outputFileName.substring(0, outputFileName.indexOf(extension));

        final String finalOutputFileName = outputFileName;
        String txnOutFileName = finalOutputFileName;

        if (folder.findChild(txnOutFileName + extension) != null) {
            int ret = 0;
            try {
                ret = Messages.showYesNoCancelDialog(String.format("Already a file exists with file name %s. Do you want to overwrite?",
                        txnOutFileName + extension), "Code Generation", "Overwrite", "Create New", "Cancel", AllIcons.General.QuestionDialog);
            } catch (Error e) {
                //TODO BigSur 2020.3.3 error
                ret = Messages.NO;
            }

            if (ret == Messages.NO) {
                int i = 0;
                while (folder.findChild(txnOutFileName + extension) != null)
                    txnOutFileName = finalOutputFileName + "-" + ++i;
            } else if (ret == Messages.CANCEL) {
                logListener.warn("Code Generation" + " was cancelled");
                return null;
            }
        }

        return txnOutFileName + extension;
    }
}
