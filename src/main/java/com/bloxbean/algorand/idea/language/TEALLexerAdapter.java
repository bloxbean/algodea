package com.bloxbean.algorand.idea.language;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.FlexLexer;
import org.jetbrains.annotations.NotNull;
import com.bloxbean.algorand.idea.language.TEALLexer;

public class TEALLexerAdapter extends FlexAdapter {
    public TEALLexerAdapter() {
        super(new TEALLexer(null));
    }
}
