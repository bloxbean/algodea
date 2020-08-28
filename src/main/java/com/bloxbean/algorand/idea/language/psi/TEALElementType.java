package com.bloxbean.algorand.idea.language.psi;

import com.bloxbean.algorand.idea.language.TEALLanguage;
import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TEALElementType extends IElementType {
    public TEALElementType(@NotNull String debugName) {
        super(debugName, TEALLanguage.INSTANCE);
    }
}
