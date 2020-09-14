// This is a generated file. Not intended for manual editing.
package com.bloxbean.algorand.idea.language.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.bloxbean.algorand.idea.language.psi.impl.*;

public interface TEALTypes {

  IElementType FLOW_CONTROL_OPERATION = new TEALElementType("FLOW_CONTROL_OPERATION");
  IElementType GENERAL_OPERATION = new TEALElementType("GENERAL_OPERATION");
  IElementType LOADING_OPERATION = new TEALElementType("LOADING_OPERATION");
  IElementType STATEMENT = new TEALElementType("STATEMENT");
  IElementType STATE_ACCESS_OPERATION = new TEALElementType("STATE_ACCESS_OPERATION");
  IElementType TXN_FIELD_ARG = new TEALElementType("TXN_FIELD_ARG");

  IElementType ADDW = new TEALTokenType("addw");
  IElementType ASSET_FIELD = new TEALTokenType("ASSET_FIELD");
  IElementType ASSET_FIELD_1 = new TEALTokenType("ASSET_FIELD_1");
  IElementType BITWISE_AND = new TEALTokenType("&");
  IElementType BITWISE_INVERT = new TEALTokenType("~");
  IElementType BITWISE_OR = new TEALTokenType("\\|");
  IElementType BITWISE_XOR = new TEALTokenType("^");
  IElementType BTOI = new TEALTokenType("btoi");
  IElementType COMMENT = new TEALTokenType("COMMENT");
  IElementType CONCAT = new TEALTokenType("concat");
  IElementType DIVIDE = new TEALTokenType("/");
  IElementType ED25519VERIFY = new TEALTokenType("ed25519verify");
  IElementType EOF = new TEALTokenType("EOF");
  IElementType FLOWCONTROL_OP = new TEALTokenType("FLOWCONTROL_OP");
  IElementType GLOBAL_FIELD = new TEALTokenType("GLOBAL_FIELD");
  IElementType GREATERTHAN = new TEALTokenType(">");
  IElementType GREATERTHANEQUAL = new TEALTokenType(">=");
  IElementType ID = new TEALTokenType("ID");
  IElementType ITOB = new TEALTokenType("itob");
  IElementType KECCAK256 = new TEALTokenType("keccak256");
  IElementType LEN = new TEALTokenType("len");
  IElementType LESSTHAN = new TEALTokenType("<");
  IElementType LESSTHANEQUAL = new TEALTokenType("<=");
  IElementType LOADING_OP = new TEALTokenType("LOADING_OP");
  IElementType LOGICAL_AND = new TEALTokenType("&&");
  IElementType LOGICAL_EQUAL = new TEALTokenType("==");
  IElementType LOGICAL_NOTEQUAL = new TEALTokenType("!=");
  IElementType LOGICAL_OR = new TEALTokenType("\\|\\|");
  IElementType MINUS = new TEALTokenType("-");
  IElementType MODULO = new TEALTokenType("%");
  IElementType MULW = new TEALTokenType("mulw");
  IElementType NAMED_INTEGER_CONSTANT = new TEALTokenType("NAMED_INTEGER_CONSTANT");
  IElementType NL = new TEALTokenType("NL");
  IElementType NOT = new TEALTokenType("!");
  IElementType NUMBER = new TEALTokenType("NUMBER");
  IElementType PLUS = new TEALTokenType("+");
  IElementType SHA256 = new TEALTokenType("sha256");
  IElementType SHA512_256 = new TEALTokenType("sha512_256");
  IElementType STATEACCESS_OP = new TEALTokenType("STATEACCESS_OP");
  IElementType SUBSTRING = new TEALTokenType("substring");
  IElementType SUBSTRING3 = new TEALTokenType("substring3");
  IElementType TIMES = new TEALTokenType("*");
  IElementType TYPENUM_CONSTANT = new TEALTokenType("TYPENUM_CONSTANT");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == FLOW_CONTROL_OPERATION) {
        return new TEALFlowControlOperationImpl(node);
      }
      else if (type == GENERAL_OPERATION) {
        return new TEALGeneralOperationImpl(node);
      }
      else if (type == LOADING_OPERATION) {
        return new TEALLoadingOperationImpl(node);
      }
      else if (type == STATEMENT) {
        return new TEALStatementImpl(node);
      }
      else if (type == STATE_ACCESS_OPERATION) {
        return new TEALStateAccessOperationImpl(node);
      }
      else if (type == TXN_FIELD_ARG) {
        return new TEALTxnFieldArgImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
