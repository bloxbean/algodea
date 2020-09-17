package com.bloxbean.algorand.idea.language.completion.metadata.elements;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;

public class TEALKeywordElement implements TEALElement {

    private final String keyword;

    public TEALKeywordElement(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public LookupElement getLookupElement() {
        return LookupElementBuilder
                        .create(keyword)
                        .bold();
    }
}
