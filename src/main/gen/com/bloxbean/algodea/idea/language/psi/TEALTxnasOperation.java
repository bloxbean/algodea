// This is a generated file. Not intended for manual editing.
package com.bloxbean.algodea.idea.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface TEALTxnasOperation extends PsiElement {

  @NotNull
  TEALTxnasOpcode getTxnasOpcode();

  @Nullable
  TEALTxnFieldArg getTxnFieldArg();

  @Nullable
  TEALUnsignedInteger getUnsignedInteger();

  @Nullable
  PsiElement getVarTmpl();

}
