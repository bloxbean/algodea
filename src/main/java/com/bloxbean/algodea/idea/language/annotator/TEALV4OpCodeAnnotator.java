package com.bloxbean.algodea.idea.language.annotator;

import com.bloxbean.algodea.idea.language.psi.*;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import static com.bloxbean.algodea.idea.language.TEALUtil.getTEALVersion;

public class TEALV4OpCodeAnnotator implements Annotator {
    private static String  V4_SUPPORT_MSG = "Supported in TEAL v4 or later";

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        PsiFile psiFile = element.getContainingFile();
        Integer versionInt = getTEALVersion(psiFile);
        if (versionInt == null) return;

        if(versionInt < 4) {
            if(element instanceof TEALCallsubOpcode
                    || element instanceof TEALRetsubOpcode
                    || element instanceof TEALGloadOpcode
                    || element instanceof TEALGloadsOpcode
                    || element instanceof TEALGaidOpcode
                    || element instanceof TEALGaidsOpcode
                    || element instanceof TEALDivmodwOpcode
            ) {
                holder.newAnnotation(HighlightSeverity.ERROR,
                        V4_SUPPORT_MSG).create();
            } else {
                return;
            }
        }
    }
}
