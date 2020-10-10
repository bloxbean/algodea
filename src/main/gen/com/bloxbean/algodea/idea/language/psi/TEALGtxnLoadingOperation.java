// This is a generated file. Not intended for manual editing.
package com.bloxbean.algodea.idea.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface TEALGtxnLoadingOperation extends PsiElement {

  @NotNull
  TEALGtxnOpcode getGtxnOpcode();

  @Nullable
  TEALTxnFieldArg getTxnFieldArg();

  @NotNull
  List<TEALUnsignedInteger> getUnsignedIntegerList();

}
