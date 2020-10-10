// This is a generated file. Not intended for manual editing.
package com.bloxbean.algodea.idea.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface TEALStatement extends PsiElement {

  @Nullable
  TEALFlowControlOperation getFlowControlOperation();

  @Nullable
  TEALGeneralOperation getGeneralOperation();

  @Nullable
  TEALLoadingOperation getLoadingOperation();

  @Nullable
  TEALStateAccessOperation getStateAccessOperation();

  @Nullable
  TEALBranch getBranch();

  @Nullable
  TEALPseudoOp getPseudoOp();

  @Nullable
  PsiElement getComment();

  @Nullable
  PsiElement getEof();

  @Nullable
  PsiElement getNl();

  @Nullable
  PsiElement getVarTmpl();

}
