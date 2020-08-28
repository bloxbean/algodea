package com.bloxbean.algorand.idea.language.psi;

import com.bloxbean.algorand.idea.language.TEALFileType;
import com.bloxbean.algorand.idea.language.TEALLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class TEALFile extends PsiFileBase {
    public TEALFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, TEALLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return TEALFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "TEAL File";
    }
}
