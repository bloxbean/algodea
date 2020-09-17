package com.bloxbean.algorand.idea.language.completion.providers;

import com.bloxbean.algorand.idea.language.TEALLanguage;
import com.bloxbean.algorand.idea.language.TEALParserDefinition;
import com.bloxbean.algorand.idea.language.completion.metadata.atoms.TEALKeywords;
import com.bloxbean.algorand.idea.language.psi.TEALFile;
import com.bloxbean.algorand.idea.language.psi.TEALProgram;
import com.bloxbean.algorand.idea.language.psi.TEALStatement;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public final class KeywordCompletionProvider extends BaseCompletionProvider {

    public static final ElementPattern<PsiElement> PATTERN = psiElement()  //Start of Statement
            .afterLeaf(psiElement().inside(TEALStatement.class))
            .withLanguage(TEALLanguage.INSTANCE)
            .andNot(PlatformPatterns.psiElement(TEALParserDefinition.LINE_COMMENT))
            .andNot(PlatformPatterns.psiElement(TEALParserDefinition.BLOCK_COMMENT));

    public static final ElementPattern<PsiElement> FIRST_ELEMENT_PATTERN = psiElement() //Top level
            .with(new PatternCondition<PsiElement>("topLevel") {
                @Override
                public boolean accepts(@NotNull PsiElement psiElement, ProcessingContext context) {
                    PsiElement parentPsiElement = psiElement.getParent();
                    if(parentPsiElement != null && parentPsiElement instanceof PsiErrorElement) {
                        return (parentPsiElement.getPrevSibling() != null)
                                && (parentPsiElement.getPrevSibling() instanceof TEALProgram);
                    } else {
                        return psiElement.getParent() instanceof TEALFile;
                    }
                }
            });

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  ProcessingContext context,
                                  @NotNull CompletionResultSet result) {
        result.addAllElements(TEALKeywords.KEYWORD_LOOKUP_ELEMENTS);
    }
}
