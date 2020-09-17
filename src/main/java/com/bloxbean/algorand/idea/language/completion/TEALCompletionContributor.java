package com.bloxbean.algorand.idea.language.completion;

import com.bloxbean.algorand.idea.language.completion.providers.KeywordCompletionProvider;
import com.bloxbean.algorand.idea.language.completion.providers.TxnArgCompletionProvider;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;

public class TEALCompletionContributor extends CompletionContributor {

    public TEALCompletionContributor() {
        extend(CompletionType.BASIC,
                KeywordCompletionProvider.PATTERN,
                new KeywordCompletionProvider());

        extend(CompletionType.BASIC,
                KeywordCompletionProvider.FIRST_ELEMENT_PATTERN,
                new KeywordCompletionProvider());

        extend(CompletionType.BASIC,
                TxnArgCompletionProvider.PATTERN,
                new TxnArgCompletionProvider());
    }

}