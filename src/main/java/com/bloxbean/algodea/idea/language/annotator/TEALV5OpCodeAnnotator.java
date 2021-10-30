package com.bloxbean.algodea.idea.language.annotator;

import com.bloxbean.algodea.idea.language.psi.*;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import static com.bloxbean.algodea.idea.language.TEALUtil.getTEALVersion;

public class TEALV5OpCodeAnnotator implements Annotator {
    private static String  V5_SUPPORT_MSG = "Supported in TEAL v5 or later";

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        PsiFile psiFile = element.getContainingFile();
        Integer versionInt = getTEALVersion(psiFile);
        if (versionInt == null) return;

        if(versionInt < 5) {
            if(element instanceof TEALEcdsaOp
                    || element instanceof TEALAppParamsGetOp
                    || element instanceof TEALLoadsOpcode
                    || element instanceof TEALStoresOpcode
                    || element instanceof TEALCoverOpcode
                    || element instanceof TEALUncoverOpcode
                    || element instanceof TEALExtractOpcode
                    || element instanceof TEALExtract3Opcode
                    || element instanceof TEALExtractUint16Opcode
                    || element instanceof TEALExtractUint32Opcode
                    || element instanceof TEALExtractUint64Opcode
                    || element instanceof TEALLogOpcode
                    || element instanceof TEALItxnBeginOpcode
                    || element instanceof TEALItxnFieldOpcode
                    || element instanceof TEALItxnSubmitOpcode
                    || element instanceof TEALItxnOpcode
            ) {
                holder.newAnnotation(HighlightSeverity.ERROR,
                        V5_SUPPORT_MSG).create();
            } else {
                return;
            }
        }
    }
}
