// This is a generated file. Not intended for manual editing.
package com.bloxbean.algorand.idea.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface TEALGtxnaLoadingOperation extends PsiElement {

  @NotNull
  TEALGtxnaOpcode getGtxnaOpcode();

  @Nullable
  TEALTxnFieldArg getTxnFieldArg();

  @NotNull
  List<TEALUnsignedInteger> getUnsignedIntegerList();

}
