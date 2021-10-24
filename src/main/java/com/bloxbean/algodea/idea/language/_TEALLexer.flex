package com.bloxbean.algodea.idea.language;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.bloxbean.algodea.idea.language.psi.TEALTypes.*;

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

NL=\r\n
EOF=\Z
SPACE=[ \t\n\x0B\f\r]+
COMMENT="//".*
L_INTEGER=0|[1-9][0-9]*
L_STRING=('([^'\\]|\\.)*'|\"([^\"\\]|\\.)*\")
LOADING_OP=(intcblock|intc|intc_0|intc_1|intc_2|intc_3|bytecblock|bytec|bytec_0|bytec_1|bytec_2|bytec_3|arg|arg_0|arg_1|arg_2|arg_3|global|load|store|pushint|pushbytes|gload|gloads|gaid|gaids|loads|stores|bzero)
FLOWCONTROL_OP=(err|return|pop|dup|dup2|bnz|bz|b|dig|swap|select|cover|assert|callsub|retsub)
STATEACCESS_OP=(balance|app_opted_in|app_local_get|app_local_get_ex|app_global_get|app_global_get_ex|app_local_put|app_global_put|app_local_del|app_global_del|asset_holding_get|asset_params_get|app_params_get|min_balance)
TXN_LOADING_OP=(txn|gtxn|txna|gtxna|gtxns|gtxnsa)
NAMED_INTEGER_CONSTANT=(NoOp|OptIn|CloseOut|ClearState|UpdateApplication|DeleteApplication)
TYPENUM_CONSTANT=(unknown|pay|keyreg|acfg|axfer|afrz|appl|Unknown|Payment|KeyRegistration|AssetConfig|AssetTransfer|AssetFreeze|ApplicationCall)
GLOBAL_FIELD=(MinTxnFee|MinBalance|MaxTxnLife|ZeroAddress|GroupSize|LogicSigVersion|Round|LatestTimestamp|CurrentApplicationID|CreatorAddress|CurrentApplicationAddress|GroupID)
ASSET_HOLDING_GET_FIELD=(AssetBalance|AssetFrozen)
ASSET_PARAMS_GET_FIELD=(AssetTotal|AssetDecimals|AssetDefaultFrozen|AssetUnitName|AssetName|AssetURL|AssetMetadataHash|AssetManager|AssetReserve|AssetFreeze|AssetClawback|AssetCreator)
ECDSA_OP=(ecdsa_verify|ecdsa_pk_decompress|ecdsa_pk_recover)
APP_PARAMS_GET_FIELD=(AppApprovalProgram|AppClearStateProgram|AppGlobalNumUint|AppGlobalNumByteSlice|AppLocalNumUint|AppLocalNumByteSlice|AppExtraProgramPages|AppCreator|AppAddress)
OCTAL=(0)[0-9]+
HEX=(0x|0X)[a-fA-F0-9]*
VAR_TMPL=(VAR_TMPL_)([a-zA-Z0-9_$.#@~?]+)
ID=([a-zA-Z0-9_$.#@~?=+-]+[a-zA-Z0-9_$.#@~?=+-]*)
//BASE32=([A-Z2-7]{8})*([A-Z2-7]{2}={6}|[A-Z2-7]{4}={4}|[A-Z2-7]{5}={3}|[A-Z2-7]{7}=)?
//BASE64=[-A-Za-z0-9+=]{1,50}|=[^=]|={3}

%%
<YYINITIAL> {
  {WHITE_SPACE}                  { return WHITE_SPACE; }

  "sha256"                       { return SHA256; }
  "keccak256"                    { return KECCAK256; }
  "sha512_256"                   { return SHA512_256; }
  "ed25519verify"                { return ED25519VERIFY; }
  "+"                            { return PLUS; }
  "-"                            { return MINUS; }
  "/"                            { return DIVIDE; }
  "*"                            { return TIMES; }
  "<"                            { return LESSTHAN; }
  ">"                            { return GREATERTHAN; }
  "<="                           { return LESSTHANEQUAL; }
  ">="                           { return GREATERTHANEQUAL; }
  "&&"                           { return LOGICAL_AND; }
  "||"                           { return LOGICAL_OR; }

  //v4
  "shl"                          { return SHL_OPCODE; }
  "shr"                          { return SHR_OPCODE; }
  "sqrt"                         { return SQRT_OPCODE; }
  "bitlen"                       { return BITLEN_OPCODE; }
  "exp"                          { return EXP_OPCODE; }
  //v4 ends

  "=="                           { return LOGICAL_EQUAL; }
  "!="                           { return LOGICAL_NOTEQUAL; }
  "!"                            { return NOT; }
  "len"                          { return LEN; }
  "itob"                         { return ITOB; }
  "btoi"                         { return BTOI; }
  "%"                            { return MODULO; }
  "|"                            { return BITWISE_OR; }
  "&"                            { return BITWISE_AND; }
  "^"                            { return BITWISE_XOR; }
  "~"                            { return BITWISE_INVERT; }
  "mulw"                         { return MULW; }
  "addw"                         { return ADDW; }
  "divmodw"                      { return DIVMODW;} //v4
  "expw"                         { return EXPW_OPCODE; } //v4
  "getbit"                       { return GETBIT; }
  "setbit"                       { return SETBIT; }
  "getbyte"                      { return GETBYTE; }
  "setbyte"                      { return SETBYTE; }
  "concat"                       { return CONCAT; }
  "substring"                    { return SUBSTRING; }
  "substring3"                   { return SUBSTRING3; }
  "version"                      { return VERSION; }
  "int"                          { return INT; }
  "byte"                         { return BYTE; }
  "addr"                         { return ADDR; }
  "#pragma"                      { return PRAGMA_KEYWORD; }
  ":"                            { return COLON; }

  "b+"                           { return B_PLUS_OPCODE; }
  "b-"                           { return B_MINUS_OPCODE; }
  "b/"                           { return B_DIV_OPCODE; }
  "b*"                           { return B_TIMES_OPCODE; }
  "b<"                           { return B_LESS_THAN_OPCODE; }
  "b>"                           { return B_GREATER_THAN_OPCODE; }
  "b<="                          { return B_LESS_THAN_EQ_OPCODE; }
  "b>="                          { return B_GREATER_THAN_EQ_OPCODE; }
  "b=="                          { return B_EQUAL_OPCODE; }
  "b!="                          { return B_NOT_EQUAL_OPCODE; }
  "b%"                           { return B_MODULO_OPCODE; }
  "b|"                           { return B_BITWISE_OR_OPCODE; }
  "b&"                           { return B_BITWISE_AND_OPCODE; }
  "b^"                           { return B_BITWISE_XOR_OPCODE; }
  "b~"                           { return B_INVERT_OPCODE; }

  {ECDSA_OP}                     { return ECDSA_OP;}

  {NL}                           { return NL; }
  {EOF}                          { return EOF; }
  {SPACE}                        { return SPACE; }
  {COMMENT}                      { return COMMENT; }
  {L_INTEGER}                    { return L_INTEGER; }
  {L_STRING}                     { return L_STRING; }
  {LOADING_OP}                   { return LOADING_OP; }
  {FLOWCONTROL_OP}               { return FLOWCONTROL_OP; }
  {STATEACCESS_OP}               { return STATEACCESS_OP; }
  {TXN_LOADING_OP}               { return TXN_LOADING_OP; }
  {NAMED_INTEGER_CONSTANT}       { return NAMED_INTEGER_CONSTANT; }
  {TYPENUM_CONSTANT}             { return TYPENUM_CONSTANT; }
  {GLOBAL_FIELD}                 { return GLOBAL_FIELD; }
  {ASSET_HOLDING_GET_FIELD}      { return ASSET_HOLDING_GET_FIELD; }
  {ASSET_PARAMS_GET_FIELD}       { return ASSET_PARAMS_GET_FIELD; }
  {APP_PARAMS_GET_FIELD}         { return APP_PARAMS_GET_FIELD; }
  {OCTAL}                        { return OCTAL; }
  {HEX}                          { return HEX; }
  {VAR_TMPL}                     { return VAR_TMPL; }
  {ID}                           { return ID; }
//  {BASE32}                       { return BASE32; }
//  {BASE64}                       { return BASE64; }

}

[^] { return BAD_CHARACTER; }
