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

package com.bloxbean.algodea.idea.language.highlights;

import com.bloxbean.algodea.idea.language.TEALLexerAdapter;
import com.bloxbean.algodea.idea.language.completion.metadata.atoms.TEALKeywords;
import com.bloxbean.algodea.idea.language.psi.TEALTypes;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.bloxbean.algodea.idea.language.psi.TEALTypes.*;

public class TEALSyntaxHighlighter extends SyntaxHighlighterBase {

    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    //https://developer.algorand.org/docs/reference/teal/specification/#arithmetic-logic-and-cryptographic-operations
    private static final TokenSet OPERATIONS = TokenSet.create(TEALKeywords.GENERAL_OPERATIONS_ELEMENTS.toArray(new IElementType[0]));

    private static final TokenSet PSEUDO_OP_CODES = TokenSet.create(
            INT, BYTE, ADDR, METHOD
    );
//
    private static final Map<IElementType, TextAttributesKey> keys = new HashMap<>();

    static {
        fillMap(keys, OPERATIONS, TEALSyntaxColors.FUNCTION);
        fillMap(keys, PSEUDO_OP_CODES, TEALSyntaxColors.KEYWORD);
        keys.put(PSEUDO_OP, TEALSyntaxColors.KEYWORD);
        keys.put(TXN_LOADING_OP, TEALSyntaxColors.KEYWORD);
        keys.put(LOADING_OP, TEALSyntaxColors.KEYWORD);
        keys.put(FLOWCONTROL_OP, TEALSyntaxColors.KEYWORD);
        keys.put(STATEACCESS_OP, TEALSyntaxColors.KEYWORD);
        keys.put(INNER_TRANSACTION_OP, TEALSyntaxColors.KEYWORD);

        keys.put(VERSION, TEALSyntaxColors.KEYWORD);
        keys.put(PRAGMA_KEYWORD, TEALSyntaxColors.KEYWORD);
        keys.put(PRAGMA_VERSION, TEALSyntaxColors.LABEL);
        keys.put(TEALTypes.COMMENT, TEALSyntaxColors.LINE_COMMENT);
        keys.put(TokenType.BAD_CHARACTER, TEALSyntaxColors.BAD_CHARACTER);
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new TEALLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        TextAttributesKey tak = keys.get(tokenType);
        return tak != null ? pack(keys.get(tokenType)) : EMPTY_KEYS;
    }
}
