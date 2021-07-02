package com.bloxbean.algodea.idea.language.annotator;

import com.bloxbean.algodea.idea.language.opcode.TEALOpCodeFactory;
import com.bloxbean.algodea.idea.language.opcode.model.Field;
import com.bloxbean.algodea.idea.language.psi.*;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import static com.bloxbean.algodea.idea.language.TEALUtil.getTEALVersion;

public class TEALV3FieldsAnnotator implements Annotator {
    private static String  V3_SUPPORT_MSG = "Supported in TEAL v3 or later";
    public static final String GLOBAL_FIELDS = "global_fields";
    public static final String TXN_FIELDS = "txn_fields";

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        PsiFile psiFile = element.getContainingFile();
        Integer versionInt = getTEALVersion(psiFile);
        if (versionInt == null) return;

        if(versionInt < 3) {
            if(TEALTypes.GLOBAL_FIELD.equals(element.getNode().getElementType())) {
                String value = element.getNode().getText();
                Field field = TEALOpCodeFactory.getInstance().getField(GLOBAL_FIELDS, value);
                if(field != null && field.getSince() == 3) {
                    createError(holder);
                }

            } else if(TEALTypes.TXN_FIELD_ARG.equals(element.getNode().getElementType())) {
                String value = element.getNode().getText();
                Field field = TEALOpCodeFactory.getInstance().getField(TXN_FIELDS, value);
                if(field != null && field.getSince() == 3) {
                    createError(holder);
                }
            } else {
                return;
            }
        }
    }

    private void createError(@NotNull AnnotationHolder holder) {
        holder.newAnnotation(HighlightSeverity.ERROR,
                V3_SUPPORT_MSG).create();
    }
}
