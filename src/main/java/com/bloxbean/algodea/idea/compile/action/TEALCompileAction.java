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
package com.bloxbean.algodea.idea.compile.action;

import com.bloxbean.algodea.idea.configuration.model.AlgoLocalSDK;
import com.bloxbean.algodea.idea.core.action.util.AlgoContractModuleHelper;
import com.bloxbean.algodea.idea.language.psi.TEALFile;
import com.bloxbean.algodea.idea.nodeint.AlgoServerConfigurationHelper;
import com.bloxbean.algodea.idea.toolwindow.AlgoConsole;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TEALCompileAction extends AnAction {
    private final static Logger LOG = Logger.getInstance(TEALCompileAction.class);

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        PsiFile file = e.getDataContext().getData(CommonDataKeys.PSI_FILE);

        if (file != null && file instanceof TEALFile) {
            e.getPresentation().setEnabled(true);
        } else {
            e.getPresentation().setVisible(false);
            e.getPresentation().setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (LOG.isDebugEnabled())
            LOG.debug("Compile TEAL file");

        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        if (project == null)
            return;

        final Module module = LangDataKeys.MODULE.getData(e.getDataContext());
        if(module == null)
            return;

        FileDocumentManager.getInstance().saveAllDocuments();

        final AlgoConsole console = AlgoConsole.getConsole(project);
        console.clearAndshow();

        AlgoLocalSDK localSDK = AlgoServerConfigurationHelper.getCompilerLocalSDK(project);
        if(localSDK == null) {
            Messages.showErrorDialog("Algorand Local SDK is not set for this module.", "TEAL Compilation");
            return;
        }

        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (!(psiFile instanceof TEALFile)) {
            Messages.showErrorDialog("Not a TEAL fille", "TEAL Compilation");
            return;
        }

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                String cwd = project.getBasePath();

                final String outputFileName = psiFile.getVirtualFile().getName() + ".tok";

                VirtualFile sourceFile = psiFile.getVirtualFile();

               //module output folder
                VirtualFile moduleOutFolder = AlgoContractModuleHelper.getModuleOutputFolder(console, module);

                //Delete previous compiled file
                if(moduleOutFolder != null && moduleOutFolder.exists()) {
                    VirtualFile outputFile = moduleOutFolder.findChild(outputFileName);
                    if (outputFile != null && outputFile.exists()) {
                        try {
                            outputFile.delete(this);
                        } catch (IOException ioException) {

                        }
                    }
                }

                File mergedSource  = AlgoContractModuleHelper.generateMergeSourceWithVariables(project, console, moduleOutFolder, sourceFile);

                //Compilation configuration setup
                String outputFilePath = null;
                if(moduleOutFolder != null) {
                    outputFilePath = moduleOutFolder.getCanonicalPath() + File.separator + outputFileName;
                }

                List<String> cmd = new ArrayList<>();
                cmd.add(localSDK.getHome() + File.separator + "bin" + File.separator + "goal");
                cmd.add("clerk");
                cmd.add("compile");

                if(mergedSource != null)
                    cmd.add(mergedSource.getAbsolutePath());
                else
                    cmd.add(sourceFile.getCanonicalPath());

                if(outputFilePath != null) {
                    cmd.add("-o");
                    cmd.add(outputFilePath);
                }

                OSProcessHandler handler;
                try {
                    handler = new OSProcessHandler(
                            new GeneralCommandLine(cmd).withWorkDirectory(cwd)
                    );
                } catch (ExecutionException ex) {
                    ex.printStackTrace();
                    return;
                }

                console.getView().attachToProcess(handler);
                handler.startNotify();

                final VirtualFile folderToRefresh = moduleOutFolder;
                final VirtualFile moduleOutputFolderToRefresh = moduleOutFolder;
                handler.addProcessListener(new ProcessListener() {
                    @Override
                    public void startNotified(@NotNull ProcessEvent event) {
                        console.showInfoMessage("TEAL Compilation started...");
                    }

                    @Override
                    public void processTerminated(@NotNull ProcessEvent event) {
                        if(event.getExitCode() == 0) {
                            console.showInfoMessage("Compilation successful.");
                        } else {
                            console.showErrorMessage("Compilation failed.");
                        }

                        if(folderToRefresh != null) {
                            folderToRefresh.refresh(false, false);
                        }

                        if(moduleOutputFolderToRefresh != null) {
                            moduleOutputFolderToRefresh.refresh(false, true);
                            VirtualFile outputVFile = moduleOutputFolderToRefresh.findChild(outputFileName);
                            if(outputVFile != null)
                                outputVFile.refresh(true, true);
                        }
                    }

                    @Override
                    public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {

                    }
                });
            }

        });


    }
}
