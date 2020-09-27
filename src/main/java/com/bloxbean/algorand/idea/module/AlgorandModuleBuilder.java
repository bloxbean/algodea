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
import com.bloxbean.algorand.idea.module.facet.AlgoFacet;
import com.bloxbean.algorand.idea.module.facet.AlgoFacetType;
import com.bloxbean.algorand.idea.module.sdk.AlgoSdkType;
import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.facet.ModifiableFacetModel;
import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AlgorandModuleBuilder extends JavaModuleBuilder implements ModuleBuilderListener {
    private static final Logger LOG = Logger.getInstance(AlgorandModuleBuilder.class);

    private Sdk mySdk;

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
        return new ModuleWizardStep[]{new AlgorandModuleWizardStep(this)};
//        return new ModuleWizardStep[]{};
    }

    public void moduleCreated(@NotNull final Module module) {

        if (mySdk != null && mySdk.getSdkType() instanceof AlgoSdkType) {
            //setupFacet(module, mySdk);
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

  /*      ApplicationManager.getApplication().runWriteAction(new Runnable() {
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
        });*/
    }

    @Nullable
    public static ContentEntry findContentEntry(@NotNull ModuleRootModel model, @NotNull VirtualFile vFile) {
        final ContentEntry[] contentEntries = model.getContentEntries();
        for (ContentEntry contentEntry : contentEntries) {
            final VirtualFile contentEntryFile = contentEntry.getFile();
            if (contentEntryFile != null && VfsUtilCore.isAncestor(contentEntryFile, vFile, false)) {
                return contentEntry;
            }
        }
        return null;
    }

    public static void setupFacet(Module module, Sdk sdk) {
        final String facetId = AlgoFacetType.getInstance().getStringId();
        if (!StringUtil.isEmptyOrSpaces(facetId)) {

            final FacetManager facetManager = FacetManager.getInstance(module);
            final FacetType<?, ?> type = FacetTypeRegistry.getInstance().findFacetType(facetId);

            if (type != null) {

                if (facetManager.getFacetByType(type.getId()) == null) {
                    final ModifiableFacetModel model = facetManager.createModifiableModel();

                    final AlgoFacet facet = (AlgoFacet) facetManager.addFacet(type, type.getDefaultFacetName(), null);
                    facet.getConfiguration().setSdk(sdk);
                    model.addFacet(facet);
                    model.commit();
                }
            }
        }
    }

    @Override
    public boolean isSuitableSdkType(SdkTypeId sdkType) {
        return sdkType instanceof AlgoSdkType;

    }

    public void setSdk(Sdk sdk) {
        mySdk = sdk;
    }

    @Nullable
    @Override
    public ModuleWizardStep modifySettingsStep(SettingsStep settingsStep) {
        JavaSettingsStep step = (JavaSettingsStep) ProjectWizardStepFactory.getInstance().createJavaSettingsStep(settingsStep, this,
                this::isSuitableSdkType);

        if (step != null) {
            step.setSourcePath("src" + File.separator + "main" + File.separator + "teal");
        }
        return step;
    }

    @Override
    protected List<WizardInputField<?>> getAdditionalFields() {
        return Arrays.<WizardInputField<?>>asList(new AlgoWizardInputField());
    }

    @Override
    public ModuleType<?> getModuleType() {
        return new AlgorandModuleType();
    }

}
