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
import com.bloxbean.algodea.idea.language.completion.metadata.TEALKeywordConstant;
import com.bloxbean.algodea.idea.language.psi.TEALTypes;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public final class GTxnArgCompletionProvider extends BaseTxnArgCompletionProvider {
    private final static String GTXNA = "gtxna";
    private final static String FIRST_ARG = "FIRST_ARG";

    public static final ElementPattern<PsiElement> FIRSTARG_PATTERN = PlatformPatterns
            .psiElement()
            .andOr(
                    StandardPatterns.or(
                            psiElement().afterLeaf(
                                    psiElement(TEALTypes.TXN_LOADING_OP)
                                            .withParent(psiElement(TEALTypes.GTXN_OPCODE))
                            ),
                            psiElement().afterLeaf(
                                    psiElement(TEALTypes.TXN_LOADING_OP)
                                            .withParent(psiElement(TEALTypes.GTXNA_OPCODE))
                            ).with(new PatternCondition<PsiElement>("nfa_gtxna") {
                                @Override
                                public boolean accepts(@NotNull PsiElement psiElement, ProcessingContext context) {
                                    context.put(GTXNA, GTXNA);
                                    return true;
                                }
                            }),
                            psiElement().afterLeaf(
                                    psiElement(TEALTypes.TXN_LOADING_OP)
                                            .withParent(psiElement(TEALTypes.GTXNAS_OPCODE))
                            )
                    )
            )
            .withLanguage(TEALLanguage.INSTANCE)
            .andNot(PlatformPatterns.psiElement(TEALParserDefinition.LINE_COMMENT))
            .andNot(PlatformPatterns.psiElement(TEALParserDefinition.BLOCK_COMMENT))
            .with(new PatternCondition<PsiElement>("nfa_gtxn_first_arg") {

                @Override
                public boolean accepts(@NotNull PsiElement psiElement, ProcessingContext context) {
                    context.put(FIRST_ARG, FIRST_ARG);
                    return true;
                }
            });


    public static final ElementPattern<PsiElement> SECONDARG_PATTERN = PlatformPatterns
            .psiElement()
            .afterLeaf(
                    psiElement(TEALTypes.L_INTEGER)
                            .withParent(
                                    psiElement(TEALTypes.UNSIGNED_INTEGER)
                                            .afterSibling(
                                                    psiElement().andOr(
                                                            StandardPatterns.or(
                                                                    psiElement(TEALTypes.GTXN_OPCODE),
                                                                    psiElement(TEALTypes.GTXNA_OPCODE)
                                                                            .with(new PatternCondition<PsiElement>("nfa_gtxn_first_arg") {

                                                                                @Override
                                                                                public boolean accepts(@NotNull PsiElement psiElement, ProcessingContext context) {
                                                                                    context.put(GTXNA, GTXNA);
                                                                                    return true;
                                                                                }
                                                                            }),
                                                                    psiElement(TEALTypes.GTXNAS_OPCODE)
                                                            )
                                                    )
                                            )
                            )
            )
            .withLanguage(TEALLanguage.INSTANCE)
            .andNot(PlatformPatterns.psiElement(TEALParserDefinition.LINE_COMMENT))
            .andNot(PlatformPatterns.psiElement(TEALParserDefinition.BLOCK_COMMENT));

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  ProcessingContext context,
                                  @NotNull CompletionResultSet result) {

        if (context.get(FIRST_ARG) != null) {

            if (context.get(GTXNA) != null) {
                result.addAllElements(getTxnArgsLookupElementsStream(parameters)
                        .stream()
                        .map(e ->
                                e.getCompositeLookupElement(TEALKeywordConstant.UINT8_PLACEHOLDER,
                                        TEALKeywordConstant.UINT8_PLACEHOLDER))
                        .collect(Collectors.toList()));
            } else {
                result.addAllElements(getTxnArgsLookupElementsStream(parameters)
                        .stream()
                        .map(e ->
                                e.getCompositeLookupElement(TEALKeywordConstant.UINT8_PLACEHOLDER,
                                        null))
                        .collect(Collectors.toList()));
            }
        } else { //second arg position
            if (context.get(GTXNA) != null) {
                result.addAllElements(getTxnArgsLookupElementsStream(parameters)
                        .stream()
                        .map(e ->
                                e.getCompositeLookupElement(null,
                                        TEALKeywordConstant.UINT8_PLACEHOLDER))
                        .collect(Collectors.toList()));
            } else {
                result.addAllElements(getTxnArgsLookupElements(parameters));
            }
        }

        result.stopHere();
    }
}
