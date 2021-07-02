package com.bloxbean.algodea.idea.language.annotator;

import com.bloxbean.algodea.idea.language.psi.*;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import static com.bloxbean.algodea.idea.language.TEALUtil.getTEALVersion;

public class TEALV3OpCodeAnnotator implements Annotator {
    private static String  V3_SUPPORT_MSG = "Supported in TEAL v3 or later";

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        PsiFile psiFile = element.getContainingFile();
        Integer versionInt = getTEALVersion(psiFile);
        if (versionInt == null) return;

        if(versionInt < 3) {
            if(element instanceof TEALGtxnsOpcode
                    || element instanceof TEALGtxnsaOpcode
                    || element instanceof TEALDigOpcode
                    || element instanceof TEALGetbitOpcode
                    || element instanceof TEALSetbitOpcode
                    || element instanceof TEALGetbyteOpcode
                    || element instanceof TEALSetbyteOpcode
                    || element instanceof TEALPushIntOpcode
                    || element instanceof TEALPushByteOpcode
                    || element instanceof TEALSwapOpcode
                    || element instanceof TEALSelectOpcode
                    || element instanceof TEALAssertOpcode
                    || element instanceof TEALMinBalanceOpcode
            ) {
                holder.newAnnotation(HighlightSeverity.ERROR,
                        V3_SUPPORT_MSG).create();
            } else {
                return;
            }
        }
    }
}
