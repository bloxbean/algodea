package com.bloxbean.algorand.idea.toolwindow.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;

public class AlgoExplorer extends SimpleToolWindowPanel implements DataProvider, Disposable {

    public AlgoExplorer(Project project) {
        super(true, true);
    }

    @Override
    public void dispose() {

    }
}
