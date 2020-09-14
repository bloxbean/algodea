package com.bloxbean.algorand.idea.language.completion;

import com.bloxbean.algorand.idea.language.psi.TEALTypes;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class TEALCompletionContributor extends CompletionContributor {

    public TEALCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(TEALTypes.STATEMENT),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        resultSet.addElement(LookupElementBuilder.create("intc"));
                        resultSet.addElement(LookupElementBuilder.create("int"));
                        resultSet.addElement(LookupElementBuilder.create("abc"));
                    }
                }
        );

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(TEALTypes.TXN_FIELD_ARG),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        resultSet.addElement(LookupElementBuilder.create("Sender"));
                        resultSet.addElement(LookupElementBuilder.create("Fee"));
                    }
                }
        );

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(TEALTypes.ID),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {

                        resultSet.addElement(LookupElementBuilder.create("load"));
                        resultSet.addElement(LookupElementBuilder.create("loaddf"));
                    }
                }
        );
    }

}