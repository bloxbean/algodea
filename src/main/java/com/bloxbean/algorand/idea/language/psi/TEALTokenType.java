package com.bloxbean.algorand.idea.language.psi;

import com.bloxbean.algorand.idea.language.TEALLanguage;
import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TEALTokenType extends IElementType {
    public TEALTokenType(@NotNull String debugName) {
        super(debugName, TEALLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "TEALTokenType." + super.toString();
    }
}
