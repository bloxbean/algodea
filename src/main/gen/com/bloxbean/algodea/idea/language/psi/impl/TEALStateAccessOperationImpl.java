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

public class TEALStateAccessOperationImpl extends ASTWrapperPsiElement implements TEALStateAccessOperation {

  public TEALStateAccessOperationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TEALVisitor visitor) {
    visitor.visitStateAccessOperation(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TEALVisitor) accept((TEALVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public TEALLogOpcode getLogOpcode() {
    return findChildByClass(TEALLogOpcode.class);
  }

  @Override
  @Nullable
  public TEALMinBalanceOpcode getMinBalanceOpcode() {
    return findChildByClass(TEALMinBalanceOpcode.class);
  }

  @Override
  @Nullable
  public TEALAppParamsGetOperation getAppParamsGetOperation() {
    return findChildByClass(TEALAppParamsGetOperation.class);
  }

  @Override
  @Nullable
  public TEALAssetHoldingGetOperation getAssetHoldingGetOperation() {
    return findChildByClass(TEALAssetHoldingGetOperation.class);
  }

  @Override
  @Nullable
  public TEALAssetParamsGetOperation getAssetParamsGetOperation() {
    return findChildByClass(TEALAssetParamsGetOperation.class);
  }

}
