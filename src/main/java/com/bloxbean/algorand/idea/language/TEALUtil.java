package com.bloxbean.algorand.idea.language;
import com.bloxbean.algorand.idea.language.psi.TEALFile;
//import com.bloxbean.algorand.idea.language.psi.TEALProperty;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TEALUtil {

//    // Searches the entire project for Simple language files with instances of the Simple property with the given key
//    public static List<TEALProperty> findProperties(Project project, String key) {
//        List<TEALProperty> result = new ArrayList<>();
//        Collection<VirtualFile> virtualFiles =
//                FileTypeIndex.getFiles(TEALFileType.INSTANCE, GlobalSearchScope.allScope(project));
//        for (VirtualFile virtualFile : virtualFiles) {
//            TEALFile simpleFile = (TEALFile) PsiManager.getInstance(project).findFile(virtualFile);
//            if (simpleFile != null) {
//                TEALProperty[] properties = PsiTreeUtil.getChildrenOfType(simpleFile, TEALProperty.class);
//                if (properties != null) {
//                    for (TEALProperty property : properties) {
//                 //TODO 6. PSI Helper Utilities
////                        if (key.equals(property.getKey())) {
////                            result.add(property);
////                        }
//                    }
//                }
//            }
//        }
//        return result;
//    }
//
//    public static List<TEALProperty> findProperties(Project project) {
//        List<TEALProperty> result = new ArrayList<>();
//        Collection<VirtualFile> virtualFiles =
//                FileTypeIndex.getFiles(TEALFileType.INSTANCE, GlobalSearchScope.allScope(project));
//        for (VirtualFile virtualFile : virtualFiles) {
//            TEALFile simpleFile = (TEALFile) PsiManager.getInstance(project).findFile(virtualFile);
//            if (simpleFile != null) {
//                TEALProperty[] properties = PsiTreeUtil.getChildrenOfType(simpleFile, TEALProperty.class);
//                if (properties != null) {
//                    Collections.addAll(result, properties);
//                }
//            }
//        }
//        return result;
//    }

}

