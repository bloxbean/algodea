// This is a generated file. Not intended for manual editing.
package com.bloxbean.algodea.idea.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.bloxbean.algodea.idea.language.psi.TEALTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.bloxbean.algodea.idea.language.psi.*;

public class TEALFlowControlOperationImpl extends ASTWrapperPsiElement implements TEALFlowControlOperation {

  public TEALFlowControlOperationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TEALVisitor visitor) {
    visitor.visitFlowControlOperation(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TEALVisitor) accept((TEALVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public TEALAssertOpcode getAssertOpcode() {
    return findChildByClass(TEALAssertOpcode.class);
  }

  @Override
  @Nullable
  public TEALRetsubOpcode getRetsubOpcode() {
    return findChildByClass(TEALRetsubOpcode.class);
  }

  @Override
  @Nullable
  public TEALSelectOpcode getSelectOpcode() {
    return findChildByClass(TEALSelectOpcode.class);
  }

  @Override
  @Nullable
  public TEALSwapOpcode getSwapOpcode() {
    return findChildByClass(TEALSwapOpcode.class);
  }

  @Override
  @Nullable
  public TEALBranchOperation getBranchOperation() {
    return findChildByClass(TEALBranchOperation.class);
  }

  @Override
  @Nullable
  public TEALCallSubroutineOperation getCallSubroutineOperation() {
    return findChildByClass(TEALCallSubroutineOperation.class);
  }

  @Override
  @Nullable
  public TEALCoverOperation getCoverOperation() {
    return findChildByClass(TEALCoverOperation.class);
  }

  @Override
  @Nullable
  public TEALDigOperation getDigOperation() {
    return findChildByClass(TEALDigOperation.class);
  }

  @Override
  @Nullable
  public TEALUncoverOperation getUncoverOperation() {
    return findChildByClass(TEALUncoverOperation.class);
  }

}
