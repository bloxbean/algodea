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

public class TEALInnerTransactionOperationImpl extends ASTWrapperPsiElement implements TEALInnerTransactionOperation {

  public TEALInnerTransactionOperationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TEALVisitor visitor) {
    visitor.visitInnerTransactionOperation(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TEALVisitor) accept((TEALVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public TEALItxnBeginOpcode getItxnBeginOpcode() {
    return findChildByClass(TEALItxnBeginOpcode.class);
  }

  @Override
  @Nullable
  public TEALItxnNextOpcode getItxnNextOpcode() {
    return findChildByClass(TEALItxnNextOpcode.class);
  }

  @Override
  @Nullable
  public TEALItxnSubmitOpcode getItxnSubmitOpcode() {
    return findChildByClass(TEALItxnSubmitOpcode.class);
  }

  @Override
  @Nullable
  public TEALGitxnOperation getGitxnOperation() {
    return findChildByClass(TEALGitxnOperation.class);
  }

  @Override
  @Nullable
  public TEALGitxnaOperation getGitxnaOperation() {
    return findChildByClass(TEALGitxnaOperation.class);
  }

  @Override
  @Nullable
  public TEALGitxnasOperation getGitxnasOperation() {
    return findChildByClass(TEALGitxnasOperation.class);
  }

  @Override
  @Nullable
  public TEALItxnFieldOperation getItxnFieldOperation() {
    return findChildByClass(TEALItxnFieldOperation.class);
  }

  @Override
  @Nullable
  public TEALItxnOperation getItxnOperation() {
    return findChildByClass(TEALItxnOperation.class);
  }

  @Override
  @Nullable
  public TEALItxnaOperation getItxnaOperation() {
    return findChildByClass(TEALItxnaOperation.class);
  }

  @Override
  @Nullable
  public TEALItxnasOperation getItxnasOperation() {
    return findChildByClass(TEALItxnasOperation.class);
  }

}
