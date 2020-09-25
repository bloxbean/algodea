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

public class TEALGtxnLoadingOperationImpl extends ASTWrapperPsiElement implements TEALGtxnLoadingOperation {

  public TEALGtxnLoadingOperationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TEALVisitor visitor) {
    visitor.visitGtxnLoadingOperation(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TEALVisitor) accept((TEALVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public TEALGtxnOpcode getGtxnOpcode() {
    return findNotNullChildByClass(TEALGtxnOpcode.class);
  }

  @Override
  @Nullable
  public TEALTxnFieldArg getTxnFieldArg() {
    return findChildByClass(TEALTxnFieldArg.class);
  }

  @Override
  @NotNull
  public List<TEALUnsignedInteger> getUnsignedIntegerList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, TEALUnsignedInteger.class);
  }

}