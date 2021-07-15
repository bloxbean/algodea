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

public class TEALGeneralOperationImpl extends ASTWrapperPsiElement implements TEALGeneralOperation {

  public TEALGeneralOperationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TEALVisitor visitor) {
    visitor.visitGeneralOperation(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TEALVisitor) accept((TEALVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public TEALBitlenOpcode getBitlenOpcode() {
    return findChildByClass(TEALBitlenOpcode.class);
  }

  @Override
  @Nullable
  public TEALDivmodwOpcode getDivmodwOpcode() {
    return findChildByClass(TEALDivmodwOpcode.class);
  }

  @Override
  @Nullable
  public TEALExpwOpcode getExpwOpcode() {
    return findChildByClass(TEALExpwOpcode.class);
  }

  @Override
  @Nullable
  public TEALExpOpcode getExpOpcode() {
    return findChildByClass(TEALExpOpcode.class);
  }

  @Override
  @Nullable
  public TEALGetbitOpcode getGetbitOpcode() {
    return findChildByClass(TEALGetbitOpcode.class);
  }

  @Override
  @Nullable
  public TEALGetbyteOpcode getGetbyteOpcode() {
    return findChildByClass(TEALGetbyteOpcode.class);
  }

  @Override
  @Nullable
  public TEALSetbitOpcode getSetbitOpcode() {
    return findChildByClass(TEALSetbitOpcode.class);
  }

  @Override
  @Nullable
  public TEALSetbyteOpcode getSetbyteOpcode() {
    return findChildByClass(TEALSetbyteOpcode.class);
  }

  @Override
  @Nullable
  public TEALShlOpcode getShlOpcode() {
    return findChildByClass(TEALShlOpcode.class);
  }

  @Override
  @Nullable
  public TEALShrOpcode getShrOpcode() {
    return findChildByClass(TEALShrOpcode.class);
  }

  @Override
  @Nullable
  public TEALSqrtOpcode getSqrtOpcode() {
    return findChildByClass(TEALSqrtOpcode.class);
  }

  @Override
  @Nullable
  public TEALSubstringOperation getSubstringOperation() {
    return findChildByClass(TEALSubstringOperation.class);
  }

}
