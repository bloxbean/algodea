package com.bloxbean.algorand.idea.language.psi;

import com.bloxbean.algorand.idea.language.TEALLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class TEALTokenType extends IElementType {

    private final String originalName;
    private final String representation;

    public TEALTokenType(@NotNull @NonNls String debugName) {
        super(debugName, TEALLanguage.INSTANCE);

        originalName = debugName;
        if (originalName.startsWith("L_")) {
            representation = originalName.substring(2);
        } else if (originalName.startsWith("O_")) {
            representation = originalName.substring(2);
        } else if (originalName.startsWith("F_")) {
            representation = originalName.substring(2);
        } else if (originalName.startsWith("S_")) {
            representation = originalName.substring(2);
        } else {
            representation = originalName;
        }
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getRepresentation() {
        return representation;
    }

    @Override
    public String toString() {
        return representation;
    }
}
