package com.bloxbean.algodea.idea.module.detector;

import com.intellij.ide.util.projectWizard.importSources.DetectedSourceRoot;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class AlgoDetectedSourceRoot extends DetectedSourceRoot {
  public AlgoDetectedSourceRoot(File root) {
    super(root, "");
  }

  @NotNull
  @Override
  public String getRootTypeName() {
    return "Algorand";
  }
}
