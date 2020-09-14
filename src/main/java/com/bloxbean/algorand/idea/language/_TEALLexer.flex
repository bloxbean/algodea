package com.bloxbean.algorand.idea.language;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.bloxbean.algorand.idea.language.psi.TEALTypes.*;

%%

%{
  public _TEALLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _TEALLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

NL=\R
EOF=\Z
SPACE=[ \t\n\x0B\f\r]+
COMMENT="//".*
LOADING_OP=(intcblock|intc|intc_0|intc_1|intc_2|intc_3|bytecblock|bytec|bytec_0|bytec_1|bytec_2|bytec_3|arg|arg_0|arg_1|arg_2|arg_3|global|load|store|txn|gtxn|txna|gtxna|addr)
FLOWCONTROL_OP=(err|return|pop|dup|dup2|bnz|bz|b)
STATEACCESS_OP=(balance|app_opted_in|app_local_get|app_local_get_ex|app_global_get|app_global_get_ex|app_local_put|app_global_put|app_local_del|app_global_del|asset_holding_get|asset_params_get)
NAMED_INTEGER_CONSTANT=(NoOp|OptIn|CloseOut|ClearState|UpdateApplication|DeleteApplication)
TYPENUM_CONSTANT=(unknown|pay|keyreg|acfg|axfer|afrz|appl)
GLOBAL_FIELD=(MinTxnFee|MinBalance|MaxTxnLife|ZeroAddress|GroupSize|LogicSigVersion|Round|LatestTimestamp|CurrentApplicationID)
ASSET_FIELD=(AssetBalance|AssetFrozen)
ASSET_FIELD_1=(AssetTotal|AssetDecimals|AssetDefaultFrozen|AssetUnitName|AssetName|AssetURL|AssetMetadataHash|AssetManager|AssetReserve|AssetFreeze|AssetClawback)
NUMBER=[0-9]+(\.[0-9]*)?
ID=([a-zA-Z_?]+[a-zA-Z0-9_$.#@~?]*)

%%
<YYINITIAL> {
  {WHITE_SPACE}                 { return WHITE_SPACE; }

  "sha256"                      { return SHA256; }
  "keccak256"                   { return KECCAK256; }
  "sha512_256"                  { return SHA512_256; }
  "ed25519verify"               { return ED25519VERIFY; }
  "+"                           { return PLUS; }
  "-"                           { return MINUS; }
  "/"                           { return DIVIDE; }
  "*"                           { return TIMES; }
  "<"                           { return LESSTHAN; }
  ">"                           { return GREATERTHAN; }
  "<="                          { return LESSTHANEQUAL; }
  ">="                          { return GREATERTHANEQUAL; }
  "&&"                          { return LOGICAL_AND; }
  "\\|\\|"                      { return LOGICAL_OR; }
  "=="                          { return LOGICAL_EQUAL; }
  "!="                          { return LOGICAL_NOTEQUAL; }
  "!"                           { return NOT; }
  "len"                         { return LEN; }
  "itob"                        { return ITOB; }
  "btoi"                        { return BTOI; }
  "%"                           { return MODULO; }
  "\\|"                         { return BITWISE_OR; }
  "&"                           { return BITWISE_AND; }
  "^"                           { return BITWISE_XOR; }
  "~"                           { return BITWISE_INVERT; }
  "mulw"                        { return MULW; }
  "addw"                        { return ADDW; }
  "concat"                      { return CONCAT; }
  "substring"                   { return SUBSTRING; }
  "substring3"                  { return SUBSTRING3; }

  {NL}                          { return NL; }
  {EOF}                         { return EOF; }
  {SPACE}                       { return SPACE; }
  {COMMENT}                     { return COMMENT; }
  {LOADING_OP}                  { return LOADING_OP; }
  {FLOWCONTROL_OP}              { return FLOWCONTROL_OP; }
  {STATEACCESS_OP}              { return STATEACCESS_OP; }
  {NAMED_INTEGER_CONSTANT}      { return NAMED_INTEGER_CONSTANT; }
  {TYPENUM_CONSTANT}            { return TYPENUM_CONSTANT; }
  {GLOBAL_FIELD}                { return GLOBAL_FIELD; }
  {ASSET_FIELD}                 { return ASSET_FIELD; }
  {ASSET_FIELD_1}               { return ASSET_FIELD_1; }
  {NUMBER}                      { return NUMBER; }
  {ID}                          { return ID; }

}

{NL}+                       { yybegin(YYINITIAL); return NL; }

. { return BAD_CHARACTER; }
