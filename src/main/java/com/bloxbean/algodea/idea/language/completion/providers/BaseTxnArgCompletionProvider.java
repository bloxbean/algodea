package com.bloxbean.algodea.idea.language.completion.providers;

import com.bloxbean.algodea.idea.language.completion.metadata.atoms.TEALKeywords;
import com.bloxbean.algodea.idea.language.completion.metadata.elements.TEALFieldElement;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.lookup.LookupElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.bloxbean.algodea.idea.language.TEALUtil.getTEALVersion;

public abstract class BaseTxnArgCompletionProvider extends BaseCompletionProvider {

    protected Collection<TEALFieldElement> getTxnArgsLookupElementsStream(CompletionParameters parameters) {
        Integer version = getTEALVersion(parameters.getOriginalFile());
        List<TEALFieldElement> txnFieldElements = new ArrayList<>();

        txnFieldElements.addAll(TEALKeywords.TXNARGS_LOOKUP_ELEMENTS_STREAM);
        if(version != null) {
            if (version >= 3) {
                txnFieldElements.addAll(txnFieldElements.size() - 1, TEALKeywords.TXNARGS_LOOKUP_ELEMENTS_STREAM_V3);
            }
            if (version >= 4) {
                txnFieldElements.addAll(txnFieldElements.size() - 1, TEALKeywords.TXNARGS_LOOKUP_ELEMENTS_STREAM_V4);
            }
            if (version >= 5) {
                txnFieldElements.addAll(txnFieldElements.size() - 1, TEALKeywords.TXNARGS_LOOKUP_ELEMENTS_STREAM_V5);
            }
        }

        return txnFieldElements;
    }

    protected Collection<LookupElement> getTxnArgsLookupElements(CompletionParameters parameters) {
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
