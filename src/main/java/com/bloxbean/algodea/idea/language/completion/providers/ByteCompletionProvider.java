package com.bloxbean.algodea.idea.language.completion.providers;

import com.bloxbean.algodea.idea.language.TEALLanguage;
import com.bloxbean.algodea.idea.language.TEALParserDefinition;
import com.bloxbean.algodea.idea.language.psi.TEALTypes;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class ByteCompletionProvider extends BaseCompletionProvider {
    private List<LookupElement> options;
    private String[] byte_args = {"\"\"", "0x", "base64", "base32", "b64", "b32" };

    public static final ElementPattern<PsiElement> PATTERN = psiElement()
            .afterLeaf(psiElement(TEALTypes.BYTE)
                    .withParent(psiElement(TEALTypes.BYTE_STATEMENT)))
            .withLanguage(TEALLanguage.INSTANCE)
            .andNot(PlatformPatterns.psiElement(TEALParserDefinition.LINE_COMMENT))
            .andNot(PlatformPatterns.psiElement(TEALParserDefinition.BLOCK_COMMENT));

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        result.addAllElements(getOptions());

        result.stopHere();
    }

    protected List<LookupElement> getOptions() {
        if(options != null && options.size() > 0)
            return options;

        options = new ArrayList<>();
        for(String ba: byte_args) {
            options.add(LookupElementBuilder.create(ba));
        }

        return options;

    }
}
