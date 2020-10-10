package com.bloxbean.algodea.idea.configuration;

import com.bloxbean.algodea.idea.configuration.service.NodeConfigState;
import com.bloxbean.algodea.idea.configuration.ui.NodeConfigurableComponent;
import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class NodeSettingsConfigurable implements Configurable {

    private NodeConfigurableComponent mySettingsComponent;

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Algorand Nodes";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPanel();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new NodeConfigurableComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
//        AppSettingsState settings = AppSettingsState.getInstance();
//        boolean modified = !mySettingsComponent.getUserNameText().equals(settings.userId);
//        modified |= mySettingsComponent.getIdeaUserStatus() != settings.ideaStatus;
//        return modified;
        return true;
    }

    @Override
    public void apply() {
        NodeConfigState settings = NodeConfigState.getInstance();

    }

    @Override
    public void reset() {
        NodeConfigState settings = NodeConfigState.getInstance();
//        mySettingsComponent.setNodeId(settings.get);
//        mySettingsComponent.setIdeaUserStatus(settings.ideaStatus);


    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}
