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

package com.bloxbean.algodea.idea.language.completion.providers;

import com.bloxbean.algodea.idea.language.TEALLanguage;
import com.bloxbean.algodea.idea.language.TEALParserDefinition;
import com.bloxbean.algodea.idea.language.completion.metadata.atoms.TEALKeywords;
import com.bloxbean.algodea.idea.language.psi.TEALPragma;
import com.bloxbean.algodea.idea.language.psi.TEALProgram;
import com.bloxbean.algodea.idea.language.psi.TEALStatement;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.bloxbean.algodea.idea.language.TEALUtil.getTEALVersion;
import static com.intellij.patterns.PlatformPatterns.psiElement;

public final class KeywordCompletionProvider extends BaseCompletionProvider {
    private static final Logger LOG = Logger.getInstance(KeywordCompletionProvider.class);

    private final static String FIRST_LINE = "FIRST_LINE";

    public static final ElementPattern<PsiElement> PATTERN = psiElement()  //Start of Statement
            .afterLeaf(psiElement().inside(TEALStatement.class))
            .withLanguage(TEALLanguage.INSTANCE)
            .andNot(PlatformPatterns.psiElement(TEALParserDefinition.LINE_COMMENT))
            .andNot(PlatformPatterns.psiElement(TEALParserDefinition.BLOCK_COMMENT));

    public static final ElementPattern<PsiElement> FIRST_ELEMENT_AFTER_PRAGMA_PATTERN = psiElement() //Top level
            .afterLeaf(psiElement().inside(TEALPragma.class));

    public static final ElementPattern<PsiElement> FIRST_ELEMENT_PATTERN = psiElement()
            .with(new PatternCondition<PsiElement>("topLevel") {
                @Override
                public boolean accepts(@NotNull PsiElement psiElement, ProcessingContext context) {
                    PsiElement parentPsiElement = psiElement.getParent();

                    if (parentPsiElement != null && parentPsiElement instanceof PsiErrorElement) {
                        context.put(FIRST_LINE, FIRST_LINE);
                        return (parentPsiElement.getPrevSibling() != null)
                                && (parentPsiElement.getPrevSibling() instanceof TEALProgram);
                    } else {
                        context.put(FIRST_LINE, FIRST_LINE);
                        return psiElement.getParent() instanceof TEALProgram;
                    }
                }
            });

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  ProcessingContext context,
                                  @NotNull CompletionResultSet result) {
        if (context.get(FIRST_LINE) != null) {
            result.addElement(TEALKeywords.PRAGMA_LINE);
        }

        if(parameters.getPosition() != null) {

            if(LOG.isDebugEnabled())
                LOG.info("Offset >>> " + parameters.getPosition().getStartOffsetInParent());

           PsiElement parentElement = parameters.getPosition().getParent();

           int offset = parameters.getPosition().getStartOffsetInParent();
           if(offset > 0 && parentElement instanceof TEALStatement)
               return;
        }

        Integer version = getTEALVersion(parameters.getOriginalFile());
        result.addAllElements(TEALKeywords.KEYWORD_LOOKUP_ELEMENTS);

        if(version != null) {
            if (version >= 3) {
                result.addAllElements(TEALKeywords.KEYWORD_LOOKUP_ELEMENTS_V3);
            }
            if (version >= 4) {
                result.addAllElements(TEALKeywords.KEYWORD_LOOKUP_ELEMENTS_V4);
            }
            if (version >= 5) {
                result.addAllElements(TEALKeywords.KEYWORD_LOOKUP_ELEMENTS_V5);
            }
            if (version >= 6) {
                result.addAllElements(TEALKeywords.KEYWORD_LOOKUP_ELEMENTS_V6);
            }
        }

        result.stopHere();
    }
}
