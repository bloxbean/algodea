package com.bloxbean.algorand.idea.language;

import com.bloxbean.algorand.idea.language.psi.TEALTypes;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class TEALCompletionContributor extends CompletionContributor {

    public TEALCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(TEALTypes.VALUE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        resultSet.addElement(LookupElementBuilder.create("Hello"));
                    }
                }
        );
    }

}