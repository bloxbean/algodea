// This is a generated file. Not intended for manual editing.
package com.bloxbean.algorand.idea.language.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.bloxbean.algorand.idea.language.psi.TEALTypes.*;
import static com.bloxbean.algorand.idea.language.psi.impl.TEALParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class TEALParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return tealFile(b, l + 1);
  }

  /* ********************************************************** */
  // 'err' | 'return' | 'pop' | 'dup' | 'dup2'
  //                                 | 'bnz' (ID)+
  //                                 | 'bz' (ID)+
  //                                 | 'b' (ID)+
  public static boolean FlowControlOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FlowControlOperation")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FLOW_CONTROL_OPERATION, "<flow control operation>");
    r = consumeToken(b, "err");
    if (!r) r = consumeToken(b, "return");
    if (!r) r = consumeToken(b, "pop");
    if (!r) r = consumeToken(b, "dup");
    if (!r) r = consumeToken(b, "dup2");
    if (!r) r = FlowControlOperation_5(b, l + 1);
    if (!r) r = FlowControlOperation_6(b, l + 1);
    if (!r) r = FlowControlOperation_7(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'bnz' (ID)+
  private static boolean FlowControlOperation_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FlowControlOperation_5")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "bnz");
    r = r && FlowControlOperation_5_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ID)+
  private static boolean FlowControlOperation_5_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FlowControlOperation_5_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, ID)) break;
      if (!empty_element_parsed_guard_(b, "FlowControlOperation_5_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // 'bz' (ID)+
  private static boolean FlowControlOperation_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FlowControlOperation_6")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "bz");
    r = r && FlowControlOperation_6_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ID)+
  private static boolean FlowControlOperation_6_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FlowControlOperation_6_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, ID)) break;
      if (!empty_element_parsed_guard_(b, "FlowControlOperation_6_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // 'b' (ID)+
  private static boolean FlowControlOperation_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FlowControlOperation_7")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "b");
    r = r && FlowControlOperation_7_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ID)+
  private static boolean FlowControlOperation_7_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "FlowControlOperation_7_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, ID)) break;
      if (!empty_element_parsed_guard_(b, "FlowControlOperation_7_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // SHA256
  //                     | KECCAK256
  //                     | SHA512_256
  //                                      | ED25519VERIFY
  //                                      | PLUS
  //                                      | MINUS
  //                                      | DIVIDE
  //                                      | TIMES
  //                                      | LESSTHAN
  //                                      | GREATERTHAN
  //                                      | LESSTHANEQUAL
  //                                      | GREATERTHANEQUAL
  //                                      | LOGICAL_AND
  //                                      | LOGICAL_OR
  //                                      | LOGICAL_EQUAL
  //                                      | LOGICAL_NOTEQUAL
  //                                      | NOT
  //                                      | LEN
  //                                      | ITOB
  //                                      | BTOI
  //                                      | MODULO
  //                                      | BITWISE_OR
  //                                      | BITWISE_AND
  //                                      | BITWISE_XOR
  //                                      | BITWISE_INVERT
  //                                      | MULW
  //                                      | ADDW
  //                                      | CONCAT
  //                                        |SUBSTRING NUMBER NUMBER
  //                                      | SUBSTRING3 NUMBER NUMBER
  public static boolean GeneralOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GeneralOperation")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GENERAL_OPERATION, "<general operation>");
    r = consumeToken(b, SHA256);
    if (!r) r = consumeToken(b, KECCAK256);
    if (!r) r = consumeToken(b, SHA512_256);
    if (!r) r = consumeToken(b, ED25519VERIFY);
    if (!r) r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    if (!r) r = consumeToken(b, DIVIDE);
    if (!r) r = consumeToken(b, TIMES);
    if (!r) r = consumeToken(b, LESSTHAN);
    if (!r) r = consumeToken(b, GREATERTHAN);
    if (!r) r = consumeToken(b, LESSTHANEQUAL);
    if (!r) r = consumeToken(b, GREATERTHANEQUAL);
    if (!r) r = consumeToken(b, LOGICAL_AND);
    if (!r) r = consumeToken(b, LOGICAL_OR);
    if (!r) r = consumeToken(b, LOGICAL_EQUAL);
    if (!r) r = consumeToken(b, LOGICAL_NOTEQUAL);
    if (!r) r = consumeToken(b, NOT);
    if (!r) r = consumeToken(b, LEN);
    if (!r) r = consumeToken(b, ITOB);
    if (!r) r = consumeToken(b, BTOI);
    if (!r) r = consumeToken(b, MODULO);
    if (!r) r = consumeToken(b, BITWISE_OR);
    if (!r) r = consumeToken(b, BITWISE_AND);
    if (!r) r = consumeToken(b, BITWISE_XOR);
    if (!r) r = consumeToken(b, BITWISE_INVERT);
    if (!r) r = consumeToken(b, MULW);
    if (!r) r = consumeToken(b, ADDW);
    if (!r) r = consumeToken(b, CONCAT);
    if (!r) r = parseTokens(b, 0, SUBSTRING, NUMBER, NUMBER);
    if (!r) r = parseTokens(b, 0, SUBSTRING3, NUMBER, NUMBER);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'intcblock'
  //                                   | 'intc'
  //                                   | 'intc_0'
  //                                   | 'intc_1'
  //                                   | 'intc_2'
  //                                   | 'intc_3'
  //                                   | 'bytecblock'
  //                                   | 'bytec'
  //                                   | 'bytec_0'
  //                                   | 'bytec_1'
  //                                   | 'bytec_2'
  //                                   | 'bytec_3'
  // //                                       ARG         = 'arg'
  //                                   | 'arg_0'
  //                                   | 'arg_1'
  //                                   | 'arg_2'
  //                                   | 'arg_3'
  //                                   | 'global'
  //                                   | 'load'
  //                                   | 'store'
  // //                                    | 'txn' TxnFieldArg
  // //                                       | 'gtxn' TxnFieldArg
  // //                                       | 'txna' TxnFieldArg
  // //                                       | 'gtxna' TxnFieldArg
  //                                   | TXN_LOADING_OP TxnFieldArg
  //                                        //TODO need clarification
  //                                        | 'addr'
  //                                        | 'arg' NUMBER
  public static boolean LoadingOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LoadingOperation")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOADING_OPERATION, "<loading operation>");
    r = consumeToken(b, "intcblock");
    if (!r) r = consumeToken(b, "intc");
    if (!r) r = consumeToken(b, "intc_0");
    if (!r) r = consumeToken(b, "intc_1");
    if (!r) r = consumeToken(b, "intc_2");
    if (!r) r = consumeToken(b, "intc_3");
    if (!r) r = consumeToken(b, "bytecblock");
    if (!r) r = consumeToken(b, "bytec");
    if (!r) r = consumeToken(b, "bytec_0");
    if (!r) r = consumeToken(b, "bytec_1");
    if (!r) r = consumeToken(b, "bytec_2");
    if (!r) r = consumeToken(b, "bytec_3");
    if (!r) r = consumeToken(b, "arg_0");
    if (!r) r = consumeToken(b, "arg_1");
    if (!r) r = consumeToken(b, "arg_2");
    if (!r) r = consumeToken(b, "arg_3");
    if (!r) r = consumeToken(b, "global");
    if (!r) r = consumeToken(b, "load");
    if (!r) r = consumeToken(b, "store");
    if (!r) r = LoadingOperation_19(b, l + 1);
    if (!r) r = consumeToken(b, "addr");
    if (!r) r = LoadingOperation_21(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // TXN_LOADING_OP TxnFieldArg
  private static boolean LoadingOperation_19(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LoadingOperation_19")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TXN_LOADING_OP);
    r = r && TxnFieldArg(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'arg' NUMBER
  private static boolean LoadingOperation_21(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LoadingOperation_21")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "arg");
    r = r && consumeToken(b, NUMBER);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'balance' | 'app_opted_in' | 'app_local_get'| 'app_local_get_ex' | 'app_global_get'
  //                             | 'app_global_get_ex'| 'app_local_put'| 'app_global_put'| 'app_local_del'
  //                             | 'app_global_del'
  //                             | 'asset_holding_get' ID
  //                             | 'asset_params_get'  ID
  public static boolean StateAccessOperation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StateAccessOperation")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STATE_ACCESS_OPERATION, "<state access operation>");
    r = consumeToken(b, "balance");
    if (!r) r = consumeToken(b, "app_opted_in");
    if (!r) r = consumeToken(b, "app_local_get");
    if (!r) r = consumeToken(b, "app_local_get_ex");
    if (!r) r = consumeToken(b, "app_global_get");
    if (!r) r = consumeToken(b, "app_global_get_ex");
    if (!r) r = consumeToken(b, "app_local_put");
    if (!r) r = consumeToken(b, "app_global_put");
    if (!r) r = consumeToken(b, "app_local_del");
    if (!r) r = consumeToken(b, "app_global_del");
    if (!r) r = StateAccessOperation_10(b, l + 1);
    if (!r) r = StateAccessOperation_11(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'asset_holding_get' ID
  private static boolean StateAccessOperation_10(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StateAccessOperation_10")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "asset_holding_get");
    r = r && consumeToken(b, ID);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'asset_params_get'  ID
  private static boolean StateAccessOperation_11(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StateAccessOperation_11")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "asset_params_get");
    r = r && consumeToken(b, ID);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'Sender'| 'Fee'| 'FirstValid'| 'FirstValidTime'| 'LastValid'| 'Note'| 'Lease'| 'Receiver'| 'Amount'
  //                        | 'CloseRemainderTo'|'VotePK'|'SelectionPK'|'VoteFirst'|'VoteLast'|'VoteKeyDilution'|'Type'
  //                        | 'TypeEnum'|'XferAsset'|'AssetAmount'|'AssetSender'|'AssetReceiver'|'AssetCloseTo'|'GroupIndex'
  //                        | 'TxID'|'ApplicationID'|'OnCompletion'|'ApplicationArgs'|'NumAppArgs'|'Accounts'|'NumAccounts'
  //                        |'ApprovalProgram'|'ClearStateProgram'|'RekeyTo'|'ConfigAsset'|'ConfigAssetTotal'|'ConfigAssetDecimals'
  //                        | 'ConfigAssetDefaultFrozen'|'ConfigAssetUnitName'|'ConfigAssetName'|'ConfigAssetURL'|'ConfigAssetMetadataHash'
  //                        | 'ConfigAssetManager'|'ConfigAssetReserve'|'ConfigAssetFreeze'|'ConfigAssetClawback'|'FreezeAsset'
  //                        | 'FreezeAssetAccount'|'FreezeAssetFrozen'
  static boolean TxnFieldArg(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TxnFieldArg")) return false;
    boolean r;
    r = consumeToken(b, "Sender");
    if (!r) r = consumeToken(b, "Fee");
    if (!r) r = consumeToken(b, "FirstValid");
    if (!r) r = consumeToken(b, "FirstValidTime");
    if (!r) r = consumeToken(b, "LastValid");
    if (!r) r = consumeToken(b, "Note");
    if (!r) r = consumeToken(b, "Lease");
    if (!r) r = consumeToken(b, "Receiver");
    if (!r) r = consumeToken(b, "Amount");
    if (!r) r = consumeToken(b, "CloseRemainderTo");
    if (!r) r = consumeToken(b, "VotePK");
    if (!r) r = consumeToken(b, "SelectionPK");
    if (!r) r = consumeToken(b, "VoteFirst");
    if (!r) r = consumeToken(b, "VoteLast");
    if (!r) r = consumeToken(b, "VoteKeyDilution");
    if (!r) r = consumeToken(b, "Type");
    if (!r) r = consumeToken(b, "TypeEnum");
    if (!r) r = consumeToken(b, "XferAsset");
    if (!r) r = consumeToken(b, "AssetAmount");
    if (!r) r = consumeToken(b, "AssetSender");
    if (!r) r = consumeToken(b, "AssetReceiver");
    if (!r) r = consumeToken(b, "AssetCloseTo");
    if (!r) r = consumeToken(b, "GroupIndex");
    if (!r) r = consumeToken(b, "TxID");
    if (!r) r = consumeToken(b, "ApplicationID");
    if (!r) r = consumeToken(b, "OnCompletion");
    if (!r) r = consumeToken(b, "ApplicationArgs");
    if (!r) r = consumeToken(b, "NumAppArgs");
    if (!r) r = consumeToken(b, "Accounts");
    if (!r) r = consumeToken(b, "NumAccounts");
    if (!r) r = consumeToken(b, "ApprovalProgram");
    if (!r) r = consumeToken(b, "ClearStateProgram");
    if (!r) r = consumeToken(b, "RekeyTo");
    if (!r) r = consumeToken(b, "ConfigAsset");
    if (!r) r = consumeToken(b, "ConfigAssetTotal");
    if (!r) r = consumeToken(b, "ConfigAssetDecimals");
    if (!r) r = consumeToken(b, "ConfigAssetDefaultFrozen");
    if (!r) r = consumeToken(b, "ConfigAssetUnitName");
    if (!r) r = consumeToken(b, "ConfigAssetName");
    if (!r) r = consumeToken(b, "ConfigAssetURL");
    if (!r) r = consumeToken(b, "ConfigAssetMetadataHash");
    if (!r) r = consumeToken(b, "ConfigAssetManager");
    if (!r) r = consumeToken(b, "ConfigAssetReserve");
    if (!r) r = consumeToken(b, "ConfigAssetFreeze");
    if (!r) r = consumeToken(b, "ConfigAssetClawback");
    if (!r) r = consumeToken(b, "FreezeAsset");
    if (!r) r = consumeToken(b, "FreezeAssetAccount");
    if (!r) r = consumeToken(b, "FreezeAssetFrozen");
    return r;
  }

  /* ********************************************************** */
  // statement*
  public static boolean program(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "program")) return false;
    Marker m = enter_section_(b, l, _NONE_, PROGRAM, "<program>");
    while (true) {
      int c = current_position_(b);
      if (!statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "program", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // COMMENT
  //                     | LoadingOperation
  //                     | FlowControlOperation
  //                     | StateAccessOperation
  //                     | GeneralOperation
  //                     | NL
  //                     | EOF
  public static boolean statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STATEMENT, "<statement>");
    r = consumeToken(b, COMMENT);
    if (!r) r = LoadingOperation(b, l + 1);
    if (!r) r = FlowControlOperation(b, l + 1);
    if (!r) r = StateAccessOperation(b, l + 1);
    if (!r) r = GeneralOperation(b, l + 1);
    if (!r) r = consumeToken(b, NL);
    if (!r) r = consumeToken(b, EOF);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // program
  static boolean tealFile(PsiBuilder b, int l) {
    return program(b, l + 1);
  }

}
