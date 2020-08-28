package com.bloxbean.algorand.idea.language;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TEALFileType extends LanguageFileType {
    public static final TEALFileType INSTANCE = new TEALFileType();

    private TEALFileType() {
        super(TEALLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "TEAL File";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "TEAL language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "teal";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return TEALIcons.FILE;
    }
}
