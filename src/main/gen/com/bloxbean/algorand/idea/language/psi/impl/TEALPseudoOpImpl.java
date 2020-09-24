// This is a generated file. Not intended for manual editing.
package com.bloxbean.algorand.idea.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.bloxbean.algorand.idea.language.psi.TEALTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.bloxbean.algorand.idea.language.psi.*;

public class TEALPseudoOpImpl extends ASTWrapperPsiElement implements TEALPseudoOp {

  public TEALPseudoOpImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TEALVisitor visitor) {
    visitor.visitPseudoOp(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TEALVisitor) accept((TEALVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public TEALAddrStatement getAddrStatement() {
    return findChildByClass(TEALAddrStatement.class);
  }

  @Override
  @Nullable
  public TEALByteStatement getByteStatement() {
    return findChildByClass(TEALByteStatement.class);
  }

  @Override
  @Nullable
  public TEALUnsignedInteger getUnsignedInteger() {
    return findChildByClass(TEALUnsignedInteger.class);
  }

  @Override
  @Nullable
  public PsiElement getHex() {
    return findChildByType(HEX);
  }

  @Override
  @Nullable
  public PsiElement getNamedIntegerConstant() {
    return findChildByType(NAMED_INTEGER_CONSTANT);
  }

  @Override
  @Nullable
  public PsiElement getOctal() {
    return findChildByType(OCTAL);
  }

  @Override
  @Nullable
  public PsiElement getTypenumConstant() {
    return findChildByType(TYPENUM_CONSTANT);
  }

  @Override
  @Nullable
  public PsiElement getVarTmpl() {
    return findChildByType(VAR_TMPL);
  }

}
