package com.bloxbean.algodea.idea.util;

import com.bloxbean.algodea.idea.module.AlgorandModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.util.Collection;

import static com.bloxbean.algodea.idea.module.AlgoModuleConstant.TEAL_FOLDER;

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

    public static String getRelativePathFromSourceRoot(Project project, VirtualFile srcFile) {

        Module[] modules = ModuleManager.getInstance(project).getModules();

        if(modules != null) {
            for (Module module : modules) {
                VirtualFile[] roots = ModuleRootManager.getInstance(module).getSourceRoots();
                if(roots != null) {
                    for (VirtualFile root : roots) {
                        String relPath = VfsUtil.getRelativePath(srcFile, root, File.separatorChar);
                        if (relPath != null)
                            return relPath;
                    }
                }
            }
        }

        return null;
    }

    public static VirtualFile getSourceVirtualFileByRelativePath(Project project, String relPath) {

        Module[] modules = ModuleManager.getInstance(project).getModules();

        if (modules != null) {
            for (Module module : modules) {
                VirtualFile[] roots = ModuleRootManager.getInstance(module).getSourceRoots();
                if(roots != null) {
                    for (VirtualFile root : roots) {
                        VirtualFile vf = VfsUtil.findRelativeFile(relPath, root);
                        if(vf != null)
                            return vf;
                    }
                }
            }
        }

        return null;
    }

    public static String getFirstSourceRootPath(Project project) {
        Collection<Module> modules = getAlgorandModules(project);
        if(modules != null && modules.size() > 0)
            return _getFirstTEALSourceRootPath(project);
        else
            return _getFirstSourceRootPath(project);
    }

    private static String _getFirstTEALSourceRootPath(Project project) {
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

    private static String _getFirstSourceRootPath(Project project) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        if(modules == null || modules.length == 0)
            return null;

        Module module = modules[0];
        VirtualFile[] roots = ModuleRootManager.getInstance(module).getSourceRoots();
        if(roots != null && roots.length > 0) {
            for(VirtualFile root: roots) {
                if(root != null && TEAL_FOLDER.equals(root.getName())) { //Check if any of the source root with name "teal"
                    return root.getPath();
                }
            }

            return roots[0].getPath();
        }

        return null;
    }

    //Not used now. //TODO .
    private static VirtualFile _getFirstTEALSourceRoot(Project project) {
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
}
