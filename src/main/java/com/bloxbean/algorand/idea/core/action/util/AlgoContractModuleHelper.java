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
package com.bloxbean.algorand.idea.core.action.util;

import com.bloxbean.algorand.idea.compile.model.VarParam;
import com.bloxbean.algorand.idea.compile.ui.CompileVarTmplInputDialog;
import com.bloxbean.algorand.idea.compile.util.VarTmplUtil;
import com.bloxbean.algorand.idea.core.service.AlgoCacheService;
import com.bloxbean.algorand.idea.toolwindow.AlgoConsole;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlgoContractModuleHelper {
    private final static Logger LOG = Logger.getInstance(AlgoContractModuleHelper.class);

    public static final String BUILD_FOLDER = "out";
    public static final String GENERATED_SRC = "generated-src";

    public static VirtualFile getModuleOutputFolder(AlgoConsole console, Module module) {
//        Object requestor = new Object();
//
//        VirtualFile outFolder = null;
        VirtualFile moduleOutFolder = null;
        VirtualFile moduleRoot = module.getModuleFile().getParent();

        try {
            File moduleOutFolderFile = new File(
                    VfsUtil.virtualToIoFile(moduleRoot).getAbsolutePath() + File.separator
                            + BUILD_FOLDER + File.separator + module.getName());

            boolean created = FileUtil.createDirectory(moduleOutFolderFile);

            moduleOutFolder = VfsUtil.findFileByIoFile(moduleOutFolderFile, true);
            //
//            outFolder = moduleRoot.findChild(BUILD_FOLDER);
//            if(outFolder == null || !outFolder.exists())
//                outFolder = moduleRoot.createChildDirectory(requestor, BUILD_FOLDER);
//
//            if(outFolder != null && outFolder.exists()) {
//                moduleOutFolder = outFolder.findChild(module.getName());
//
//                if(moduleOutFolder == null || !moduleOutFolder.exists()) {
//                    moduleOutFolder = outFolder.createChildDirectory(requestor, module.getName());
//
//                }
//
//                if(moduleOutFolder == null || !moduleOutFolder.exists()) {
//                    console.showErrorMessage("Error creating module out folder for the output in " + outFolder.getCanonicalPath());
//                    return null;
//                }
//            } else {
//                console.showErrorMessage("Unable to create out folder in " + moduleRoot.getCanonicalPath());
//                return null;
//            }
        } catch (Exception io) {
            LOG.error(io);
            console.showErrorMessage("Unable to create out folder " + io.getMessage());
        }

        return moduleOutFolder;
    }

    public static File generateMergeSourceWithVariables(Project project, AlgoConsole console, VirtualFile moduleOutFolder, VirtualFile sourceFile) {
        File mergedSource = null;
        //Get list of VAR_TMPL_* if available in the source file
        List<VarParam> varParams = null;
        try {
            varParams = VarTmplUtil.getListOfVarTmplInTEALFile(sourceFile);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            console.showErrorMessage("Unable to read teal file to get VAR_TMPL_ variables");
            return null;
        }

        AlgoCacheService algoCacheService = project.getComponent(AlgoCacheService.class);
        if(algoCacheService != null) { //Get cached var values
            Map<String, String> cacheVars = algoCacheService.getVarsFromCache(sourceFile.getName());
            if(cacheVars != null) {
                varParams.stream().forEach(v -> {
                    String value = cacheVars.get(v.getName());
                    if (value != null)
                        v.setDefaultValue(value);
                });
            }
        }


        //If VAR_TMPL_* found
        if(varParams != null && varParams.size() > 0) {
            CompileVarTmplInputDialog compileVarTmplInputDialog =  new CompileVarTmplInputDialog(sourceFile.getName(), varParams);
            boolean result = compileVarTmplInputDialog.showAndGet();
            if(!result) {
                console.showWarningMessage("Compilation process was cancelled");
                return null;
            }

            List<VarParam> varParamsValues = compileVarTmplInputDialog.getParamsWithValues();

            VirtualFile genSrcFolder = createGeneratedSourceFolder(moduleOutFolder);
            if(genSrcFolder == null) {
                console.showErrorMessage("Compilation failed. 'generated-src' folder could not be created");
                return null;
            }

            //Create merged source file inside out/<module>/generated_src folder
            Object requestor = new Object();
            try {
                mergedSource = VarTmplUtil.createMergeSourceFile(requestor, sourceFile, genSrcFolder, varParamsValues);
            } catch (IOException ioException) {
                LOG.error("Error merging VAR_TMPL_* values with the source", ioException);
                console.showErrorMessage("Compilation failed. VAR_TMPL_ values could not be merged");
                return null;
            }

            //Update cache
            Map<String, String> varValuesToStoreInCache = new HashMap<>();
            varParamsValues.stream().forEach(varParam -> {
                varValuesToStoreInCache.put(varParam.getName(), varParam.getValue());
            });
            algoCacheService.updateVarsToCache(sourceFile.getName(), varValuesToStoreInCache);
        }
        return mergedSource;
    }

    private static VirtualFile createGeneratedSourceFolder(VirtualFile moduleOutFolder) {
        VirtualFile genFolder = moduleOutFolder.findChild(GENERATED_SRC);
        if(genFolder == null || !genFolder.exists()) {
            try {
                File genFile = new File(VfsUtil.virtualToIoFile(moduleOutFolder), GENERATED_SRC);
                FileUtil.createDirectory(genFile);
                genFolder = VfsUtil.findFileByIoFile(genFile, true);
                        //moduleOutFolder.createChildDirectory(new Object(), GENERATED_SRC);
            } catch (Exception e) {
                LOG.error("Unable to create generated_src folder", e);
                e.printStackTrace();
                return null;
            }
        }
        return  genFolder;
    }
}