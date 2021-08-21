package com.bloxbean.algodea.idea.debugger;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DebuggerDialog extends DialogWrapper {
    private JPanel mainPanel;

    protected DebuggerDialog(@Nullable Project project) {
        super(project, true);
        init();
        setTitle("Debugger");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }
}
