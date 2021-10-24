package com.bloxbean.algodea.idea.language.completion.providers;

import com.bloxbean.algodea.idea.language.TEALLanguage;
import com.bloxbean.algodea.idea.language.TEALParserDefinition;
import com.bloxbean.algodea.idea.language.psi.TEALTypes;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.bloxbean.algodea.idea.language.TEALUtil.getTEALVersion;
import static com.bloxbean.algodea.idea.language.completion.metadata.atoms.TEALKeywords.APP_PARAMS_GET_FIELDS_ELEMENTS_MAP;
import static com.intellij.patterns.PlatformPatterns.psiElement;

public class AppParamsGetCompletionProvider extends BaseCompletionProvider{
    public static final ElementPattern<PsiElement> PATTERN = psiElement()
            .afterLeaf(psiElement(TEALTypes.STATEACCESS_OP)
                    .withParent(psiElement(TEALTypes.APP_PARAMS_GET_OP)))
            .withLanguage(TEALLanguage.INSTANCE)
            .andNot(PlatformPatterns.psiElement(TEALParserDefinition.LINE_COMMENT))
            .andNot(PlatformPatterns.psiElement(TEALParserDefinition.BLOCK_COMMENT));

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        Integer version = getTEALVersion(parameters.getOriginalFile());
        selectFieldsByVersion(APP_PARAMS_GET_FIELDS_ELEMENTS_MAP, result, version);

        result.stopHere();
    }
}
