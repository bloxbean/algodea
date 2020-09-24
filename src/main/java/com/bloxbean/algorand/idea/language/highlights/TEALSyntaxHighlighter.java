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

package com.bloxbean.algorand.idea.language.highlights;

import com.bloxbean.algorand.idea.language.TEALLexerAdapter;
import com.bloxbean.algorand.idea.language.psi.TEALTypes;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.bloxbean.algorand.idea.language.psi.TEALTypes.*;

public class TEALSyntaxHighlighter extends SyntaxHighlighterBase {

    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{TEALSyntaxColors.BAD_CHARACTER};

    private static final TextAttributesKey[] LINE_COMMENT_KEYS = new TextAttributesKey[]{TEALSyntaxColors.LINE_COMMENT};
    private static final TextAttributesKey[] BLOCK_COMMENT_KEYS = new TextAttributesKey[]{TEALSyntaxColors.BLOCK_COMMENT};

    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{TEALSyntaxColors.KEYWORD};
    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{TEALSyntaxColors.VARIABLE};

    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{TEALSyntaxColors.NUMBER};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{TEALSyntaxColors.STRING};

    private static final TextAttributesKey[] OPERATION_SIGN_KEYS = new TextAttributesKey[]{TEALSyntaxColors.OPERATION_SIGN};
    private static final TextAttributesKey[] SEMICOLON_KEYS = new TextAttributesKey[]{TEALSyntaxColors.SEMICOLON};
    private static final TextAttributesKey[] PARENTHESES_KEYS = new TextAttributesKey[]{TEALSyntaxColors.PARENTHESES};
    private static final TextAttributesKey[] CURLY_BRACES_KEYS = new TextAttributesKey[]{TEALSyntaxColors.CURLY_BRACES};
    private static final TextAttributesKey[] SQUARE_BRACES_KEYS = new TextAttributesKey[]{TEALSyntaxColors.SQUARE_BRACES};
    private static final TextAttributesKey[] COMMA_KEYS = new TextAttributesKey[]{TEALSyntaxColors.COMMA};
    private static final TextAttributesKey[] DOT_KEYS = new TextAttributesKey[]{TEALSyntaxColors.DOT};

    private static final TokenSet OPERATIONS = TokenSet.create(
            SHA256,
            KECCAK256,
            SHA512_256,
            ED25519VERIFY,
            PLUS,
            MINUS,
            DIVIDE,
            TIMES,
            LESSTHAN,
            GREATERTHAN,
            LESSTHANEQUAL,
            GREATERTHANEQUAL,

            LOGICAL_AND,
            LOGICAL_OR,
            LOGICAL_EQUAL,
            LOGICAL_NOTEQUAL,

            NOT,
            LEN,
            ITOB,
            BTOI,

            MODULO,
            BITWISE_OR,
            BITWISE_AND,
            BITWISE_XOR,
            BITWISE_INVERT,
            MULW,
            ADDW,
            CONCAT,
            SUBSTRING,
            SUBSTRING3
    );
//
    private static final Map<IElementType, TextAttributesKey> keys = new HashMap<>();

    static {
        fillMap(keys, OPERATIONS, TEALSyntaxColors.FUNCTION);
        keys.put(LOADING_OP, TEALSyntaxColors.KEYWORD);
        keys.put(TXN_LOADING_OP, TEALSyntaxColors.KEYWORD);
        keys.put(FLOWCONTROL_OP, TEALSyntaxColors.KEYWORD);
        keys.put(STATEACCESS_OP, TEALSyntaxColors.KEYWORD);
        keys.put(TEALTypes.COMMENT, TEALSyntaxColors.LINE_COMMENT);
        keys.put(TokenType.BAD_CHARACTER, TEALSyntaxColors.BAD_CHARACTER);
//        fillMap(keys, STRINGS, ELM_STRING);
//        fillMap(keys, COMMENTS, ELM_COMMENT);
//        fillMap(keys, PARENTHESES, ELM_PARENTHESIS);
//        fillMap(keys, BRACES, ELM_BRACES);
//        fillMap(keys, BRACKETS, ELM_BRACKETS);
//        fillMap(keys, OPERATORS, ELM_OPERATOR);
//        keys.put(ARROW, ELM_ARROW);
//        keys.put(EQ, ELM_EQ);
//        keys.put(COMMA, ELM_COMMA);
//        keys.put(DOT, ELM_DOT);
//        keys.put(NUMBER_LITERAL, ELM_NUMBER);
//        keys.put(PIPE, ELM_PIPE);
//        keys.put(TokenType.BAD_CHARACTER, ELM_BAD_CHAR);
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
//        if (tokenType.equals(LOADING_OP)) {
//            return KEYWORD_KEYS;
//        }
//        else if (tokenType.equals(FLOWCONTROL_OP)) {
//            return KEYWORD_KEYS;
//        }
//        else if (tokenType.equals(STATEACCESS_OP)) {
//            return KEYWORD_KEYS;
//        }
//        else if (tokenType.equals(TEALParserDefinition.BLOCK_COMMENT)) {
//            return BLOCK_COMMENT_KEYS;
//        }
//        if (isKeywordTokenType(tokenType)) {
//            return KEYWORD_KEYS;
//        }
//        if (tokenType.equals(TEALTypes.L_IDENTIFIER) || tokenType.equals(TEALTypes.L_IDENTIFIER_TEXT)) {
//            return IDENTIFIER_KEYS;
//        }
//        if (tokenType.equals(TEALTypes.L_DECIMAL) || tokenType.equals(TEALTypes.L_INTEGER)) {
//            return NUMBER_KEYS;
//        }
//        if (tokenType.equals(TEALTypes.L_STRING)) {
//            return STRING_KEYS;
//        }
//        if (tokenType.equals(TEALTypes.SEMICOLON)) {
//            return SEMICOLON_KEYS;
//        }
//        if (tokenType.equals(TEALTypes.PARENTHESE_OPEN) || tokenType.equals(TEALTypes.PARENTHESE_CLOSE)) {
//            return PARENTHESES_KEYS;
//        }
//        if (tokenType.equals(TEALTypes.BRACKET_CURLYOPEN)
//                || tokenType.equals(TEALTypes.BRACKET_CURLYCLOSE)) {
//            return CURLY_BRACES_KEYS;
//        }
//        if (tokenType.equals(TEALTypes.BRACKET_SQUAREOPEN)
//                || tokenType.equals(TEALTypes.BRACKET_SQUARECLOSE)) {
//            return SQUARE_BRACES_KEYS;
//        }
//        if (tokenType.equals(TEALTypes.OP_COMMA)) {
//            return COMMA_KEYS;
//        }
//        if (tokenType.equals(TEALTypes.OP_DOT)) {
//            return DOT_KEYS;
//        }
//        if (isOperationTokenType(tokenType)) {
//            return OPERATION_SIGN_KEYS;
//        }
//        if (tokenType.equals(TokenType.BAD_CHARACTER)) {
//            return BAD_CHAR_KEYS;
//        }
//        return EMPTY_KEYS;
    }

//    private boolean isKeywordTokenType(IElementType tokenType) {
//        if (tokenType instanceof TEALTokenType) {
//            TEALTokenType tealTokenType = (TEALTokenType) tokenType;
//            System.out.println("token Original name: " + tealTokenType);
//            return tealTokenType.getOriginalName().startsWith("FLOW") ||
//                    tealTokenType.getOriginalName().startsWith("O_") ||
//                    tealTokenType.getOriginalName().startsWith("F_") ||
//                    tealTokenType.getOriginalName().startsWith("S_");
//        }
//        return false;
//    }

    private boolean isOperationTokenType(IElementType tokenType) {
        return false;
//        return tokenType.equals(TEALTypes.OP_BACTICK)
//                || tokenType.equals(TEALTypes.OP_COLON)
//                || tokenType.equals(TEALTypes.OP_DIVIDE)
//                || tokenType.equals(TEALTypes.OP_EQUAL)
//                || tokenType.equals(TEALTypes.OP_GREATERTHANEQUALS)
//                || tokenType.equals(TEALTypes.OP_GREATHERTHEN)
//                || tokenType.equals(TEALTypes.OP_INVALIDNOTEQUALS)
//                || tokenType.equals(TEALTypes.OP_LESSTHANEQUALS)
//                || tokenType.equals(TEALTypes.OP_LESSTHEN)
//                || tokenType.equals(TEALTypes.OP_MINUS)
//                || tokenType.equals(TEALTypes.OP_MODULO)
//                || tokenType.equals(TEALTypes.OP_MUL)
//                || tokenType.equals(TEALTypes.OP_NOTEQUALS)
//                || tokenType.equals(TEALTypes.OP_PIPE)
//                || tokenType.equals(TEALTypes.OP_PLUS)
//                || tokenType.equals(TEALTypes.OP_PLUSEQUALS)
//                || tokenType.equals(TEALTypes.OP_POW)
//                || tokenType.equals(TEALTypes.OP_QUESTIONSIGN)
//                || tokenType.equals(TEALTypes.OP_RANGE)
//                || tokenType.equals(TEALTypes.OP_REGEXMATCH);
    }


}
