// This is a generated file. Not intended for manual editing.
package com.bloxbean.algorand.idea.language.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.bloxbean.algorand.idea.language.psi.impl.*;

public interface TEALTypes {

  IElementType PROPERTY = new TEALElementType("PROPERTY");

  IElementType COMMENT = new TEALTokenType("COMMENT");
  IElementType CRLF = new TEALTokenType("CRLF");
  IElementType KEY = new TEALTokenType("KEY");
  IElementType SEPARATOR = new TEALTokenType("SEPARATOR");
  IElementType VALUE = new TEALTokenType("VALUE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == PROPERTY) {
        return new TEALPropertyImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
