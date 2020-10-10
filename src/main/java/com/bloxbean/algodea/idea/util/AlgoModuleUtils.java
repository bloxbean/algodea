package com.bloxbean.algodea.idea.util;

import com.bloxbean.algodea.idea.module.AlgorandModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Collection;

public class AlgoModuleUtils {
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

    public static VirtualFile getFirstSourceRoot(Project project) {
        Collection<Module> modules = getAlgorandModules(project);
        if(modules == null || modules.size() == 0)
            return null;

        Module module = modules.iterator().next();
        VirtualFile[] roots = ModuleRootManager.getInstance(module).getSourceRoots();
        if(roots != null && roots.length > 0)
            return roots[0];
        else
            return null;
    }

    public static String getFirstSourceRootPath(Project project) {
        Collection<Module> modules = getAlgorandModules(project);
        if(modules == null || modules.size() == 0)
            return null;

        Module module = modules.iterator().next();
        VirtualFile[] roots = ModuleRootManager.getInstance(module).getSourceRoots();
        if(roots != null && roots.length > 0)
            return roots[0].getPath();
        else
            return null;
    }
}
