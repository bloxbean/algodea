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

public class TEALBytesliceOperationImpl extends ASTWrapperPsiElement implements TEALBytesliceOperation {

  public TEALBytesliceOperationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TEALVisitor visitor) {
    visitor.visitBytesliceOperation(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TEALVisitor) accept((TEALVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public TEALBBitwiseAndOpcode getBBitwiseAndOpcode() {
    return findChildByClass(TEALBBitwiseAndOpcode.class);
  }

  @Override
  @Nullable
  public TEALBBitwiseOrOpcode getBBitwiseOrOpcode() {
    return findChildByClass(TEALBBitwiseOrOpcode.class);
  }

  @Override
  @Nullable
  public TEALBBitwiseXorOpcode getBBitwiseXorOpcode() {
    return findChildByClass(TEALBBitwiseXorOpcode.class);
  }

  @Override
  @Nullable
  public TEALBDivOpcode getBDivOpcode() {
    return findChildByClass(TEALBDivOpcode.class);
  }

  @Override
  @Nullable
  public TEALBEqualOpcode getBEqualOpcode() {
    return findChildByClass(TEALBEqualOpcode.class);
  }

  @Override
  @Nullable
  public TEALBGreaterThanEqOpcode getBGreaterThanEqOpcode() {
    return findChildByClass(TEALBGreaterThanEqOpcode.class);
  }

  @Override
  @Nullable
  public TEALBGreaterThanOpcode getBGreaterThanOpcode() {
    return findChildByClass(TEALBGreaterThanOpcode.class);
  }

  @Override
  @Nullable
  public TEALBInvertOpcode getBInvertOpcode() {
    return findChildByClass(TEALBInvertOpcode.class);
  }

  @Override
  @Nullable
  public TEALBLessThanEqOpcode getBLessThanEqOpcode() {
    return findChildByClass(TEALBLessThanEqOpcode.class);
  }

  @Override
  @Nullable
  public TEALBLessThanOpcode getBLessThanOpcode() {
    return findChildByClass(TEALBLessThanOpcode.class);
  }

  @Override
  @Nullable
  public TEALBMinusOpcode getBMinusOpcode() {
    return findChildByClass(TEALBMinusOpcode.class);
  }

  @Override
  @Nullable
  public TEALBModuloOpcode getBModuloOpcode() {
    return findChildByClass(TEALBModuloOpcode.class);
  }

  @Override
  @Nullable
  public TEALBNotEqualOpcode getBNotEqualOpcode() {
    return findChildByClass(TEALBNotEqualOpcode.class);
  }

  @Override
  @Nullable
  public TEALBPlusOpcode getBPlusOpcode() {
    return findChildByClass(TEALBPlusOpcode.class);
  }

  @Override
  @Nullable
  public TEALBTimesOpcode getBTimesOpcode() {
    return findChildByClass(TEALBTimesOpcode.class);
  }

  @Override
  @Nullable
  public TEALBZeroOpcode getBZeroOpcode() {
    return findChildByClass(TEALBZeroOpcode.class);
  }

}
