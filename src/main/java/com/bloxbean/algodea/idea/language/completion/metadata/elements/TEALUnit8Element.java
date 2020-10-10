package com.bloxbean.algodea.idea.language.completion.metadata.elements;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import org.jetbrains.annotations.NotNull;

public class TEALUnit8Element implements TEALElement {
    private int i;
    public TEALUnit8Element(int i) {
        this.i = i;
    }

    @Override
    public LookupElement getLookupElement() {
        return LookupElementBuilder
                .create(String.valueOf(i))
                .withInsertHandler(new InsertHandler<LookupElement>() {
                    @Override
                    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {

                    }
                });
    }
}
