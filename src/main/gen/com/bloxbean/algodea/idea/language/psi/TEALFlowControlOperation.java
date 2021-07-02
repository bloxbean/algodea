// This is a generated file. Not intended for manual editing.
package com.bloxbean.algodea.idea.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface TEALFlowControlOperation extends PsiElement {

  @Nullable
  TEALAssertOpcode getAssertOpcode();

  @Nullable
  TEALSelectOpcode getSelectOpcode();

  @Nullable
  TEALSwapOpcode getSwapOpcode();

  @Nullable
  TEALBranchOperation getBranchOperation();

  @Nullable
  TEALDigOperation getDigOperation();

}
