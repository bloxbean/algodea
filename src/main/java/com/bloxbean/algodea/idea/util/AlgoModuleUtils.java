package com.bloxbean.algodea.idea.util;

import com.bloxbean.algodea.idea.module.AlgorandModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Collection;

public class AlgoModuleUtils {
    private final static String TEAL_FOLDER = "teal";

    public static Collection<Module> getAlgorandModules(Project project) {
        return ModuleUtil.getModulesOfType(project, AlgorandModuleType.getInstance());
    }

    public static String getModuleDirPath(Project project) {
        Collection<Module> modules = getAlgorandModules(project);
        if(modules == null || modules.size() == 0)
            return null;

        Module module = modules.iterator().next();
        return ModuleUtil.getModuleDirPath(module);
    }

    public static VirtualFile getFirstTEALSourceRoot(Project project) {
        Collection<Module> modules = getAlgorandModules(project);
        if(modules == null || modules.size() == 0)
            return null;

        Module module = modules.iterator().next();
        VirtualFile[] roots = ModuleRootManager.getInstance(module).getSourceRoots();
        if(roots != null && roots.length > 0) {
            for(VirtualFile root: roots) {
                if(root != null && TEAL_FOLDER.equals(root.getName())) {
                    return root;
                }
            }
        }

        return null;
    }

    public static String getFirstTEALSourceRootPath(Project project) {
        Collection<Module> modules = getAlgorandModules(project);
        if(modules == null || modules.size() == 0)
            return null;

        Module module = modules.iterator().next();
        VirtualFile[] roots = ModuleRootManager.getInstance(module).getSourceRoots();
        if(roots != null && roots.length > 0) {
            for(VirtualFile root: roots) {
                if(root != null && TEAL_FOLDER.equals(root.getName())) {
                    return root.getPath();
                }
            }
        }

        return null;
    }
}
