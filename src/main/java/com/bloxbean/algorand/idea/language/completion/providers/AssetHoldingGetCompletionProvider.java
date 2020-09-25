package com.bloxbean.algorand.idea.language.completion.providers;

import com.bloxbean.algorand.idea.language.TEALLanguage;
import com.bloxbean.algorand.idea.language.TEALParserDefinition;
import com.bloxbean.algorand.idea.language.completion.metadata.atoms.TEALKeywords;
import com.bloxbean.algorand.idea.language.psi.TEALTypes;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class AssetHoldingGetCompletionProvider extends BaseCompletionProvider {

    public static final ElementPattern<PsiElement> PATTERN = psiElement()
            .afterLeaf(psiElement(TEALTypes.STATEACCESS_OP)
                    .withParent(psiElement(TEALTypes.ASSET_HOLDING_GET_OP)))
            .withLanguage(TEALLanguage.INSTANCE)
            .andNot(PlatformPatterns.psiElement(TEALParserDefinition.LINE_COMMENT))
            .andNot(PlatformPatterns.psiElement(TEALParserDefinition.BLOCK_COMMENT));

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        result.addAllElements(TEALKeywords.ASSET_HOLDING_GET_FIELDS_ELEMENTS);
        result.stopHere();
    }
}
