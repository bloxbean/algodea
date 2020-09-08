package com.bloxbean.algorand.idea.language;

import com.intellij.lexer.FlexAdapter;

public class TEALLexerAdapter extends FlexAdapter {
    public TEALLexerAdapter() {
        super(new TEALLexer(null));
    }
}
