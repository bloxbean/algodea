package com.equadon.intellij.mips.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import com.equadon.intellij.mips.MarsUtils;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.equadon.intellij.mips.lang.psi.MipsElementTypes.*;

%%

%{
  public __MipsLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class __MipsLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL = \R

IDENTIFIER = [\.\_\-A-Za-z0-9]+
REGISTER_NUMBER = \$\d*
REGISTER_NAME = \$[a-zA-Z]+\d*
FP_REGISTER_NAME = \$f[a-zA-Z0-9]*
NUMBER = (0x[A-F0-9]+|\-?\d*\.\d*)

COMMENT = \#.*

// Strings
ESCAPE_SEQUENCE = \\\" | "\\b" | "\\d" | "\\e" | "\\f" | "\\n" | "\\r" | "\\s" | "\\t" | "\\v" | "\\'" | "\\\\" | "\\[" | "\\{" | "\\]" | "\\}" | "\\`" | "\\$" | "\\=" | "\\%" | "\\," | "\\." | "\\_"
QUOTED_CHAR = \\\" | {ESCAPE_SEQUENCE} | [^\"\\]
QUOTED_STRING = {QUOTED_CHAR}+

%state IN_QUOTES

%%
<YYINITIAL> {
  ":"                   { return COLON; }
  "("                   { return LPAREN; }
  ")"                   { return RPAREN; }
  "+"                   { return PLUS; }
  "-"                   { return MINUS; }
  "\""                  { yybegin(IN_QUOTES); return LQUOTE; }
  {COMMENT}             { return com.equadon.intellij.mips.lang.psi.MipsTokenTypes.COMMENT; }
  {NUMBER}              { return MarsUtils.getTokenType(yytext()); }
  {FP_REGISTER_NAME}    { return FP_REGISTER_NAME; }
  {REGISTER_NUMBER}     { return REGISTER_NUMBER; }
  {REGISTER_NAME}       { return REGISTER_NAME; }
  " "                   { return WHITE_SPACE; }
  "\t"                  { return com.equadon.intellij.mips.lang.psi.MipsTokenTypes.TAB; }
  ","                   { return COMMA; }
  {EOL}                 { return EOL; }
  {IDENTIFIER}          { return MarsUtils.getTokenType(yytext()); }
}
<IN_QUOTES> {
  {QUOTED_STRING}       { return QUOTED_STRING; }
  "\""                  { yybegin(YYINITIAL); return RQUOTE; }
}

[^] { return BAD_CHARACTER; }
