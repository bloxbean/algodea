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

public class TEALStatementImpl extends ASTWrapperPsiElement implements TEALStatement {

  public TEALStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TEALVisitor visitor) {
    visitor.visitStatement(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TEALVisitor) accept((TEALVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public TEALFlowControlOperation getFlowControlOperation() {
    return findChildByClass(TEALFlowControlOperation.class);
  }

  @Override
  @Nullable
  public TEALGeneralOperation getGeneralOperation() {
    return findChildByClass(TEALGeneralOperation.class);
  }

  @Override
  @Nullable
  public TEALLoadingOperation getLoadingOperation() {
    return findChildByClass(TEALLoadingOperation.class);
  }

  @Override
  @Nullable
  public TEALStateAccessOperation getStateAccessOperation() {
    return findChildByClass(TEALStateAccessOperation.class);
  }

  @Override
  @Nullable
  public TEALBranch getBranch() {
    return findChildByClass(TEALBranch.class);
  }

  @Override
  @Nullable
  public TEALPseudoOp getPseudoOp() {
    return findChildByClass(TEALPseudoOp.class);
  }

  @Override
  @Nullable
  public PsiElement getComment() {
    return findChildByType(COMMENT);
  }

  @Override
  @Nullable
  public PsiElement getEof() {
    return findChildByType(EOF);
  }

  @Override
  @Nullable
  public PsiElement getNl() {
    return findChildByType(NL);
  }

}
