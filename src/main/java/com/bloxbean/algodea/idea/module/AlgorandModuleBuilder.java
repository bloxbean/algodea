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

package com.bloxbean.algodea.idea.module;

import com.bloxbean.algodea.idea.common.AlgoIcons;
import com.bloxbean.algodea.idea.pkg.AlgoPkgJsonService;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.ide.util.projectWizard.*;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.intellij.ide.projectView.actions.MarkRootActionBase.findContentEntry;

public class AlgorandModuleBuilder extends ModuleBuilder implements ModuleBuilderListener {
    private static final Logger LOG = Logger.getInstance(AlgorandModuleBuilder.class);

    private List<Pair<String,String>> mySourcePaths;

    public AlgorandModuleBuilder() {
        addListener(this);
    }

    @Override
    public String getBuilderId() {
        return "Algorand";
    }


    @Override
    public Icon getNodeIcon() {
        return AlgoIcons.ALGO_ICON;
    }

    @Override
    public String getDescription() {
        return "Algorand Smart Contract";
    }

    @Override
    public String getPresentableName() {
        return "Algorand Smart Contract";
    }

    @Override
    public String getGroupName() {
        return "Algorand";
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(WizardContext wizardContext, ModulesProvider modulesProvider) {
        return new ModuleWizardStep[]{};
    }

    public void moduleCreated(@NotNull final Module module) {
        VirtualFile[] roots = ModuleRootManager.getInstance(module).getSourceRoots();
        if (roots.length == 1) {
                VirtualFile srcRoot = roots[0];
                if (srcRoot.getName().equals("teal")) {
                    VirtualFile main = srcRoot.getParent();
                    if (main != null && "main".equals(main.getName())) {
                        final VirtualFile src = main.getParent();
                        if (src != null) {
                        ApplicationManager.getApplication().runWriteAction(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    VirtualFile test = src.createChildDirectory(this, "test");
                                    if (test != null) {
                                        VirtualFile testSrc = test.createChildDirectory(this, "teal");
                                        ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();
                                        ContentEntry entry = findContentEntry(model, testSrc);
                                        if (entry != null) {
                                            entry.addSourceFolder(testSrc, true);
                                            model.commit();
                                        }
                                    }
                                } catch (IOException e) {//
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel rootModel) throws ConfigurationException {
        rootModel.inheritSdk();

        ContentEntry contentEntry = doAddContentEntry(rootModel);
        if (contentEntry != null) {
            final List<Pair<String,String>> sourcePaths = getSourcePaths();

            if (sourcePaths != null) {
                for (final Pair<String, String> sourcePath : sourcePaths) {
                    String first = sourcePath.first;
                    new File(first).mkdirs();
                    final VirtualFile sourceRoot = LocalFileSystem.getInstance()
                            .refreshAndFindFileByPath(FileUtil.toSystemIndependentName(first));
                    if (sourceRoot != null) {
                        contentEntry.addSourceFolder(sourceRoot, false, sourcePath.second);
                    }
                }
            }
        }

        Project project = rootModel.getProject();
        if(project != null) {
            //Create algo-package.json
            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    AlgoPkgJsonService.getInstance(project).createPackageJson();
                } catch (Exception e) {
                    IdeaUtil.showNotification(project,
                            "Project creation",
                            "algo-package.json could not be crated. Please create it " +
                                    "manually and restart the IDE.", NotificationType.WARNING, null);
                    if(LOG.isDebugEnabled()) {
                        LOG.error("Unable to create algo-package.json", e);
                    }
                }
            });
        }
    }

    @Override
    protected List<WizardInputField<?>> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public ModuleType<?> getModuleType() {
        return new AlgorandModuleType();
    }


    public List<Pair<String, String>> getSourcePaths() throws ConfigurationException {
        if (mySourcePaths == null) {
            final List<Pair<String, String>> paths = new ArrayList<>();
            @NonNls final String path = getContentEntryPath() + File.separator + "src" + File.separator + "main" + File.separator + "teal";
            new File(path).mkdirs();
            paths.add(Pair.create(path, ""));
            return paths;
        }
        return mySourcePaths;
    }

    public void setSourcePaths(List<Pair<String, String>> sourcePaths) {
        mySourcePaths = sourcePaths != null ? new ArrayList<>(sourcePaths) : null;
    }

    public void addSourcePath(Pair<String, String> sourcePathInfo) {
        if (mySourcePaths == null) {
            mySourcePaths = new ArrayList<>();
        }
        mySourcePaths.add(sourcePathInfo);
    }

}
