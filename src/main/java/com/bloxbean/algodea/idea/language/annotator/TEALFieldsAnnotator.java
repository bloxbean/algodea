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
import static com.bloxbean.algodea.idea.language.completion.metadata.atoms.TEALKeywords.*;

public class TEALFieldsAnnotator implements Annotator {
    private static String  V3_SUPPORT_MSG = "Supported in TEAL v3 or later";
    private static String  V4_SUPPORT_MSG = "Supported in TEAL v4 or later";
    private static String  V5_SUPPORT_MSG = "Supported in TEAL v5 or later";
    private static String  V6_SUPPORT_MSG = "Supported in TEAL v6 or later";

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        PsiFile psiFile = element.getContainingFile();
        Integer versionInt = getTEALVersion(psiFile);
        if (versionInt == null) return;

        createErrorIfRequired(3, element, versionInt,  holder, V3_SUPPORT_MSG);
        createErrorIfRequired(4, element, versionInt,  holder, V4_SUPPORT_MSG);
        createErrorIfRequired(5, element, versionInt, holder, V5_SUPPORT_MSG);
        createErrorIfRequired(6, element, versionInt, holder, V6_SUPPORT_MSG);
    }

    private void createErrorIfRequired(int tealSpecVersion, @NotNull PsiElement element,
                                       int actualVersion, @NotNull AnnotationHolder holder, String errorMsg) {
        if(actualVersion < tealSpecVersion) {
            if (TEALTypes.GLOBAL_FIELD.equals(element.getNode().getElementType())) {
                String value = element.getNode().getText();
                Field field = TEALOpCodeFactory.getInstance().getField(GLOBAL_FIELDS, value);
                if (field != null && field.getSince() == tealSpecVersion) {
                    createError(holder, errorMsg);
                }

            } else if (TEALTypes.TXN_FIELD_ARG.equals(element.getNode().getElementType())) {
                String value = element.getNode().getText();
                Field field = TEALOpCodeFactory.getInstance().getField(TXN_FIELDS, value);
                if (field != null && field.getSince() == tealSpecVersion) {
                    createError(holder, errorMsg);
                }
            } else if (TEALTypes.ASSET_PARAMS_GET_FIELD.equals(element.getNode().getElementType())) {
                String value = element.getNode().getText();
                Field field = TEALOpCodeFactory.getInstance().getField(ASSET_PARAMS_GET_FIELDS, value);
                if (field != null && field.getSince() == tealSpecVersion) {
                    createError(holder, errorMsg);
                }
            } else if (TEALTypes.APP_PARAMS_GET_FIELD.equals(element.getNode().getElementType())) {
                String value = element.getNode().getText();
                Field field = TEALOpCodeFactory.getInstance().getField(APP_PARAMS_GET_FIELDS, value);
                if (field != null && field.getSince() == tealSpecVersion) {
                    createError(holder, errorMsg);
                }
            } else if (TEALTypes.ACCT_PARAMS_GET_FIELD.equals(element.getNode().getElementType())) {
                String value = element.getNode().getText();
                Field field = TEALOpCodeFactory.getInstance().getField(ACCT_PARAMS_GET_FIELDS, value);
                if (field != null && field.getSince() == tealSpecVersion) {
                    createError(holder, errorMsg);
                }
            } else {
                return;
            }
        }
    }

    private void createError(@NotNull AnnotationHolder holder, String errorMsg) {
        holder.newAnnotation(HighlightSeverity.ERROR,
                errorMsg).create();
    }
}
