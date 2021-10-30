/*
 * Copyright (c) 2021 BloxBean Project
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
import com.bloxbean.algodea.idea.language.psi.TEALTypes;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.bloxbean.algodea.idea.language.TEALUtil.getTEALVersion;
import static com.intellij.patterns.PlatformPatterns.psiElement;

public final class ItxnTxnArgsCompletionProvider extends BaseCompletionProvider {

    public static final ElementPattern<PsiElement> PATTERN = PlatformPatterns
            .psiElement()
            .andOr(StandardPatterns.or(
                    psiElement().afterLeaf(
                        psiElement(TEALTypes.INNER_TRANSACTION_OP)
                            .withParent(psiElement(TEALTypes.ITXN_FIELD_OPCODE))
                    ),
                    psiElement().afterLeaf(
                            psiElement(TEALTypes.INNER_TRANSACTION_OP)
                                    .withParent(psiElement(TEALTypes.ITXN_OPCODE))
                    )
            ))
            .withLanguage(TEALLanguage.INSTANCE)
            .andNot(psiElement(TEALParserDefinition.LINE_COMMENT))
            .andNot(psiElement(TEALParserDefinition.BLOCK_COMMENT));

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  ProcessingContext context,
                                  @NotNull CompletionResultSet result) {

        result.addAllElements(getTxnArgsLookupElements(parameters));
        result.stopHere();
    }

    private Collection<LookupElement> getTxnArgsLookupElements(CompletionParameters parameters) {
        Integer version = getTEALVersion(parameters.getOriginalFile());
        List<LookupElement> txnFieldElements = new ArrayList<>();

        txnFieldElements.addAll(TEALKeywords.TXNARGS_LOOKUP_ELEMENTS);
        if(version != null) {
            if (version >= 3) {
                txnFieldElements.addAll(txnFieldElements.size() - 1, TEALKeywords.TXNARGS_LOOKUP_ELEMENTS_V3);
            }
            if (version >= 4) {
                txnFieldElements.addAll(txnFieldElements.size() - 1, TEALKeywords.TXNARGS_LOOKUP_ELEMENTS_V4);
            }
            if (version >= 5) {
                txnFieldElements.addAll(txnFieldElements.size() - 1, TEALKeywords.TXNARGS_LOOKUP_ELEMENTS_V5);
            }
        }

        return txnFieldElements;
    }
}
