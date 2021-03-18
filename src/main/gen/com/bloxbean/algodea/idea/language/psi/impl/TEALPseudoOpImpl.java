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

public class TEALPseudoOpImpl extends ASTWrapperPsiElement implements TEALPseudoOp {

  public TEALPseudoOpImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TEALVisitor visitor) {
    visitor.visitPseudoOp(this);
  }

  @Override
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
  public TEALIntStatement getIntStatement() {
    return findChildByClass(TEALIntStatement.class);
  }

}
