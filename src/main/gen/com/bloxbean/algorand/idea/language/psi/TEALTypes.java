// This is a generated file. Not intended for manual editing.
package com.bloxbean.algorand.idea.language.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.bloxbean.algorand.idea.language.psi.impl.*;

public interface TEALTypes {

  IElementType ADDR_PARAM = new TEALElementType("ADDR_PARAM");
  IElementType ADDR_STATEMENT = new TEALElementType("ADDR_STATEMENT");
  IElementType ARG_OPERATION = new TEALElementType("ARG_OPERATION");
  IElementType ASSET_HOLDING_GET_OP = new TEALElementType("ASSET_HOLDING_GET_OP");
  IElementType ASSET_HOLDING_GET_OPERATION = new TEALElementType("ASSET_HOLDING_GET_OPERATION");
  IElementType ASSET_PARAMS_GET_OP = new TEALElementType("ASSET_PARAMS_GET_OP");
  IElementType ASSET_PARAMS_GET_OPERATION = new TEALElementType("ASSET_PARAMS_GET_OPERATION");
  IElementType BRANCH = new TEALElementType("BRANCH");
  IElementType BRANCH_OPERATION = new TEALElementType("BRANCH_OPERATION");
  IElementType BYTEC_OPERATION = new TEALElementType("BYTEC_OPERATION");
  IElementType BYTE_STATEMENT = new TEALElementType("BYTE_STATEMENT");
  IElementType FLOW_CONTROL_OPERATION = new TEALElementType("FLOW_CONTROL_OPERATION");
  IElementType GENERAL_OPERATION = new TEALElementType("GENERAL_OPERATION");
  IElementType GLOBAL_OPERATION = new TEALElementType("GLOBAL_OPERATION");
  IElementType GLOBAL_OP_CODE = new TEALElementType("GLOBAL_OP_CODE");
  IElementType GTXNA_LOADING_OPERATION = new TEALElementType("GTXNA_LOADING_OPERATION");
  IElementType GTXNA_OPCODE = new TEALElementType("GTXNA_OPCODE");
  IElementType GTXN_LOADING_OPERATION = new TEALElementType("GTXN_LOADING_OPERATION");
  IElementType GTXN_OPCODE = new TEALElementType("GTXN_OPCODE");
  IElementType INTC_OPERATION = new TEALElementType("INTC_OPERATION");
  IElementType INT_STATEMENT = new TEALElementType("INT_STATEMENT");
  IElementType LOADING_OPERATION = new TEALElementType("LOADING_OPERATION");
  IElementType LOAD_OPERATION = new TEALElementType("LOAD_OPERATION");
  IElementType PRAGMA = new TEALElementType("PRAGMA");
  IElementType PRAGMA_VERSION = new TEALElementType("PRAGMA_VERSION");
  IElementType PROGRAM = new TEALElementType("PROGRAM");
  IElementType PSEUDO_OP = new TEALElementType("PSEUDO_OP");
  IElementType STATEMENT = new TEALElementType("STATEMENT");
  IElementType STATE_ACCESS_OPERATION = new TEALElementType("STATE_ACCESS_OPERATION");
  IElementType STORE_OPERATION = new TEALElementType("STORE_OPERATION");
  IElementType SUBSTRING_OPERATION = new TEALElementType("SUBSTRING_OPERATION");
  IElementType TXNA_LOADING_OPERATION = new TEALElementType("TXNA_LOADING_OPERATION");
  IElementType TXNA_OPCODE = new TEALElementType("TXNA_OPCODE");
  IElementType TXN_FIELD_ARG = new TEALElementType("TXN_FIELD_ARG");
  IElementType TXN_LOADING_OPERATION = new TEALElementType("TXN_LOADING_OPERATION");
  IElementType TXN_OPCODE = new TEALElementType("TXN_OPCODE");
  IElementType UNSIGNED_INTEGER = new TEALElementType("UNSIGNED_INTEGER");

  IElementType ADDR = new TEALTokenType("addr");
  IElementType ADDW = new TEALTokenType("addw");
  IElementType ASSET_HOLDING_GET_FIELD = new TEALTokenType("ASSET_HOLDING_GET_FIELD");
  IElementType ASSET_PARAMS_GET_FIELD = new TEALTokenType("ASSET_PARAMS_GET_FIELD");
  IElementType BASE32 = new TEALTokenType("BASE32");
  IElementType BASE64 = new TEALTokenType("BASE64");
  IElementType BITWISE_AND = new TEALTokenType("&");
  IElementType BITWISE_INVERT = new TEALTokenType("~");
  IElementType BITWISE_OR = new TEALTokenType("|");
  IElementType BITWISE_XOR = new TEALTokenType("^");
  IElementType BTOI = new TEALTokenType("btoi");
  IElementType BYTE = new TEALTokenType("byte");
  IElementType COLON = new TEALTokenType(":");
  IElementType COMMENT = new TEALTokenType("COMMENT");
  IElementType CONCAT = new TEALTokenType("concat");
  IElementType DIVIDE = new TEALTokenType("/");
  IElementType ED25519VERIFY = new TEALTokenType("ed25519verify");
  IElementType EOF = new TEALTokenType("EOF");
  IElementType FLOWCONTROL_OP = new TEALTokenType("FLOWCONTROL_OP");
  IElementType GLOBAL_FIELD = new TEALTokenType("GLOBAL_FIELD");
  IElementType GREATERTHAN = new TEALTokenType(">");
  IElementType GREATERTHANEQUAL = new TEALTokenType(">=");
  IElementType HEX = new TEALTokenType("HEX");
  IElementType ID = new TEALTokenType("ID");
  IElementType INT = new TEALTokenType("int");
  IElementType ITOB = new TEALTokenType("itob");
  IElementType KECCAK256 = new TEALTokenType("keccak256");
  IElementType LEN = new TEALTokenType("len");
  IElementType LESSTHAN = new TEALTokenType("<");
  IElementType LESSTHANEQUAL = new TEALTokenType("<=");
  IElementType LOADING_OP = new TEALTokenType("LOADING_OP");
  IElementType LOGICAL_AND = new TEALTokenType("&&");
  IElementType LOGICAL_EQUAL = new TEALTokenType("==");
  IElementType LOGICAL_NOTEQUAL = new TEALTokenType("!=");
  IElementType LOGICAL_OR = new TEALTokenType("||");
  IElementType L_INTEGER = new TEALTokenType("l_integer");
  IElementType L_STRING = new TEALTokenType("l_string");
  IElementType MINUS = new TEALTokenType("-");
  IElementType MODULO = new TEALTokenType("%");
  IElementType MULW = new TEALTokenType("mulw");
  IElementType NAMED_INTEGER_CONSTANT = new TEALTokenType("NAMED_INTEGER_CONSTANT");
  IElementType NL = new TEALTokenType("NL");
  IElementType NOT = new TEALTokenType("!");
  IElementType OCTAL = new TEALTokenType("OCTAL");
  IElementType PLUS = new TEALTokenType("+");
  IElementType SHA256 = new TEALTokenType("sha256");
  IElementType SHA512_256 = new TEALTokenType("sha512_256");
  IElementType STATEACCESS_OP = new TEALTokenType("STATEACCESS_OP");
  IElementType SUBSTRING = new TEALTokenType("substring");
  IElementType SUBSTRING3 = new TEALTokenType("substring3");
  IElementType TIMES = new TEALTokenType("*");
  IElementType TXN_LOADING_OP = new TEALTokenType("TXN_LOADING_OP");
  IElementType TYPENUM_CONSTANT = new TEALTokenType("TYPENUM_CONSTANT");
  IElementType VAR_TMPL = new TEALTokenType("VAR_TMPL");
  IElementType VERSION = new TEALTokenType("version");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ADDR_PARAM) {
        return new TEALAddrParamImpl(node);
      }
      else if (type == ADDR_STATEMENT) {
        return new TEALAddrStatementImpl(node);
      }
      else if (type == ARG_OPERATION) {
        return new TEALArgOperationImpl(node);
      }
      else if (type == ASSET_HOLDING_GET_OP) {
        return new TEALAssetHoldingGetOPImpl(node);
      }
      else if (type == ASSET_HOLDING_GET_OPERATION) {
        return new TEALAssetHoldingGetOperationImpl(node);
      }
      else if (type == ASSET_PARAMS_GET_OP) {
        return new TEALAssetParamsGetOpImpl(node);
      }
      else if (type == ASSET_PARAMS_GET_OPERATION) {
        return new TEALAssetParamsGetOperationImpl(node);
      }
      else if (type == BRANCH) {
        return new TEALBranchImpl(node);
      }
      else if (type == BRANCH_OPERATION) {
        return new TEALBranchOperationImpl(node);
      }
      else if (type == BYTEC_OPERATION) {
        return new TEALBytecOperationImpl(node);
      }
      else if (type == BYTE_STATEMENT) {
        return new TEALByteStatementImpl(node);
      }
      else if (type == FLOW_CONTROL_OPERATION) {
        return new TEALFlowControlOperationImpl(node);
      }
      else if (type == GENERAL_OPERATION) {
        return new TEALGeneralOperationImpl(node);
      }
      else if (type == GLOBAL_OPERATION) {
        return new TEALGlobalOperationImpl(node);
      }
      else if (type == GLOBAL_OP_CODE) {
        return new TEALGlobalOpCodeImpl(node);
      }
      else if (type == GTXNA_LOADING_OPERATION) {
        return new TEALGtxnaLoadingOperationImpl(node);
      }
      else if (type == GTXNA_OPCODE) {
        return new TEALGtxnaOpcodeImpl(node);
      }
      else if (type == GTXN_LOADING_OPERATION) {
        return new TEALGtxnLoadingOperationImpl(node);
      }
      else if (type == GTXN_OPCODE) {
        return new TEALGtxnOpcodeImpl(node);
      }
      else if (type == INTC_OPERATION) {
        return new TEALIntcOperationImpl(node);
      }
      else if (type == INT_STATEMENT) {
        return new TEALIntStatementImpl(node);
      }
      else if (type == LOADING_OPERATION) {
        return new TEALLoadingOperationImpl(node);
      }
      else if (type == LOAD_OPERATION) {
        return new TEALLoadOperationImpl(node);
      }
      else if (type == PRAGMA) {
        return new TEALPragmaImpl(node);
      }
      else if (type == PRAGMA_VERSION) {
        return new TEALPragmaVersionImpl(node);
      }
      else if (type == PROGRAM) {
        return new TEALProgramImpl(node);
      }
      else if (type == PSEUDO_OP) {
        return new TEALPseudoOpImpl(node);
      }
      else if (type == STATEMENT) {
        return new TEALStatementImpl(node);
      }
      else if (type == STATE_ACCESS_OPERATION) {
        return new TEALStateAccessOperationImpl(node);
      }
      else if (type == STORE_OPERATION) {
        return new TEALStoreOperationImpl(node);
      }
      else if (type == SUBSTRING_OPERATION) {
        return new TEALSubstringOperationImpl(node);
      }
      else if (type == TXNA_LOADING_OPERATION) {
        return new TEALTxnaLoadingOperationImpl(node);
      }
      else if (type == TXNA_OPCODE) {
        return new TEALTxnaOpcodeImpl(node);
      }
      else if (type == TXN_FIELD_ARG) {
        return new TEALTxnFieldArgImpl(node);
      }
      else if (type == TXN_LOADING_OPERATION) {
        return new TEALTxnLoadingOperationImpl(node);
      }
      else if (type == TXN_OPCODE) {
        return new TEALTxnOpcodeImpl(node);
      }
      else if (type == UNSIGNED_INTEGER) {
        return new TEALUnsignedIntegerImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
