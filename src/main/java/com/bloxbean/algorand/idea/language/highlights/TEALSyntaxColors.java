package com.bloxbean.algorand.idea.language.highlights;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class TEALSyntaxColors {
    public static final TextAttributesKey LINE_COMMENT =
            createTextAttributesKey("TEAL_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BLOCK_COMMENT =
            createTextAttributesKey("TEAL_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);

    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("TEAL_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey VARIABLE =
            createTextAttributesKey("TEAL_VARIABLE", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
    public static final TextAttributesKey FUNCTION =
            createTextAttributesKey("TEAL_FUNCTION", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    public static final TextAttributesKey LABEL =
            createTextAttributesKey("TEAL_LABEL", DefaultLanguageHighlighterColors.CLASS_REFERENCE);
    public static final TextAttributesKey RELATIONSHIP_TYPE =
            createTextAttributesKey("TEAL_RELATIONSHIP_TYPE", DefaultLanguageHighlighterColors.CLASS_REFERENCE);
    public static final TextAttributesKey PARAMETER =
            createTextAttributesKey("TEAL_PARAMETER", DefaultLanguageHighlighterColors.INSTANCE_FIELD);

    public static final TextAttributesKey SEMICOLON =
            createTextAttributesKey("TEAL_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON);
    public static final TextAttributesKey OPERATION_SIGN =
            createTextAttributesKey("TEAL_OPERATION", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey PARENTHESES =
            createTextAttributesKey("TEAL_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey CURLY_BRACES =
            createTextAttributesKey("TEAL_CURLY_BRACES", DefaultLanguageHighlighterColors.BRACKETS);
    public static final TextAttributesKey SQUARE_BRACES =
            createTextAttributesKey("TEAL_SQUARE_BRACES", DefaultLanguageHighlighterColors.BRACKETS);
    public static final TextAttributesKey COMMA =
            createTextAttributesKey("TEAL_COMMA", DefaultLanguageHighlighterColors.COMMA);
    public static final TextAttributesKey DOT =
            createTextAttributesKey("TEAL_DOT", DefaultLanguageHighlighterColors.DOT);

    public static final TextAttributesKey STRING =
            createTextAttributesKey("TEAL_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("CYPHER_NUMBER", DefaultLanguageHighlighterColors.NUMBER);

    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("SIMPLE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
}
