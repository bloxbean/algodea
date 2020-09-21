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

package com.bloxbean.algorand.idea.module;

import com.bloxbean.algorand.idea.common.AlgoIcons;
import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class AlgorandModuleBuilder extends ModuleBuilder implements ModuleBuilderListener {
    private static final Logger LOG = Logger.getInstance(AlgorandModuleBuilder.class);

    private Sdk mySdk;

    public AlgorandModuleBuilder() {
        addListener(this);
    }

    @Override
    public String getBuilderId() {
        return "algorand_asc";
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
        //return new ModuleWizardStep[]{new AlgorandModuleWizardStep()};
        return new ModuleWizardStep[]{};
    }

    public void moduleCreated(@NotNull final Module module) {

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                try {
                    String moduleFilePath = module.getModuleFilePath();
                    File moduleFolder = new File(moduleFilePath).getParentFile();

                    if(moduleFolder.exists()) {
                        File src = new File(moduleFolder, "src");
                        File test = new File(moduleFolder, "test");

                        VirtualFile srcVFile = VfsUtil.createDirectories(src.getPath());
                        VirtualFile testVFile = VfsUtil.createDirectories(test.getPath());

                        ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();

                        if(model.getContentEntries().length > 0) {
                            model.getContentEntries()[0].addSourceFolder(srcVFile, false);
                            model.getContentEntries()[0].addSourceFolder(testVFile, true);
                        } else {
                            model.addContentEntry(VfsUtil.findFile(Paths.get(moduleFolder.toURI()), false));
                            model.getContentEntries()[0].addSourceFolder(srcVFile, false);
                            model.getContentEntries()[0].addSourceFolder(testVFile, true);

                            if(LOG.isDebugEnabled())
                                LOG.debug("Added source and test source roots for Algorand project");
                        }

                        model.commit();
                    }
                } catch (Exception e) {//
                }
            }
        });
    }

//    public void setSdk(Sdk sdk) {
//        mySdk = sdk;
//    }

    @Nullable
    @Override
    public ModuleWizardStep modifySettingsStep(SettingsStep settingsStep) {

        ModuleWizardStep step = super.modifySettingsStep(settingsStep);
        return step;
    }

    @Override
    protected List<WizardInputField<?>> getAdditionalFields() {
        return Collections.emptyList();
        //return Arrays.<WizardInputField<?>>asList(new AlgoBackWizardInputField());
    }

    @Override
    public ModuleType<?> getModuleType() {
        return new AlgorandModuleType();
    }

}
