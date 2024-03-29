// This is a generated file. Not intended for manual editing.
package com.bloxbean.algodea.idea.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface TEALInnerTransactionOperation extends PsiElement {

  @Nullable
  TEALItxnBeginOpcode getItxnBeginOpcode();

  @Nullable
  TEALItxnNextOpcode getItxnNextOpcode();

  @Nullable
  TEALItxnSubmitOpcode getItxnSubmitOpcode();

  @Nullable
  TEALGitxnOperation getGitxnOperation();

  @Nullable
  TEALGitxnaOperation getGitxnaOperation();

  @Nullable
  TEALGitxnasOperation getGitxnasOperation();

  @Nullable
  TEALItxnFieldOperation getItxnFieldOperation();

  @Nullable
  TEALItxnOperation getItxnOperation();

  @Nullable
  TEALItxnaOperation getItxnaOperation();

  @Nullable
  TEALItxnasOperation getItxnasOperation();

}
