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

public class TEALGlobalOperationImpl extends ASTWrapperPsiElement implements TEALGlobalOperation {

  public TEALGlobalOperationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TEALVisitor visitor) {
    visitor.visitGlobalOperation(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TEALVisitor) accept((TEALVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public TEALGlobalOpCode getGlobalOpCode() {
    return findNotNullChildByClass(TEALGlobalOpCode.class);
  }

  @Override
  @Nullable
  public TEALUnsignedInteger getUnsignedInteger() {
    return findChildByClass(TEALUnsignedInteger.class);
  }

  @Override
  @Nullable
  public PsiElement getGlobalField() {
    return findChildByType(GLOBAL_FIELD);
  }

  @Override
  @Nullable
  public PsiElement getVarTmpl() {
    return findChildByType(VAR_TMPL);
  }

}
