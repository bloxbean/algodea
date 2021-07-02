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

package com.bloxbean.algodea.idea.language;

import com.bloxbean.algodea.idea.language.psi.TEALFile;
import com.bloxbean.algodea.idea.language.psi.TEALPragma;
import com.bloxbean.algodea.idea.language.psi.TEALPragmaVersion;
import com.bloxbean.algodea.idea.language.psi.TEALProgram;
import com.bloxbean.algodea.idea.language.psi.impl.TEALProgramImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public class TEALUtil {

    public static Integer getTEALVersion(PsiFile psiFile) {
        if(psiFile == null || !(psiFile instanceof TEALFile))
            return null;

        TEALFile tealFile = (TEALFile) psiFile;
        PsiElement firstChildElm = tealFile.getFirstChild();
        if(firstChildElm == null || !(firstChildElm instanceof TEALProgram)) {
            return null;
        }

        TEALPragma tealPragma = ((TEALProgramImpl)firstChildElm).getPragma();
        if(tealPragma == null)
            return null;

        TEALPragmaVersion pragmaVersion = tealPragma.getPragmaVersion();
        if(pragmaVersion == null)
            return null;

        String version = pragmaVersion.getUnsignedInteger().getText();
        int versionInt;
        try {
            versionInt = Integer.parseInt(version);
        } catch (Exception e) {
            return null;
        }
        return versionInt;
    }

}

