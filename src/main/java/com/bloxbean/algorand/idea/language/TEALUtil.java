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

