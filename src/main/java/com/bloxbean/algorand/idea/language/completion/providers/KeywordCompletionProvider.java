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
