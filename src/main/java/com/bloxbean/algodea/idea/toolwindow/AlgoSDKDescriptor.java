package com.bloxbean.algodea.idea.toolwindow;

import com.bloxbean.algodea.idea.configuration.model.AlgoLocalSDK;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;

public class AlgoSDKDescriptor extends NodeDescriptor {

    private AlgoLocalSDK sdk;

    public AlgoSDKDescriptor(final Project project, final NodeDescriptor parentDescriptor, AlgoLocalSDK sdk) {
        super(project, parentDescriptor);
        this.sdk = sdk;
        myName = sdk.getName() + " [" + sdk.getVersion() + "]";
        myColor = JBColor.GREEN;
        myClosedIcon = AllIcons.General.Settings;
    }

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public Object getElement() {
        return sdk;
    }

    public AlgoLocalSDK getSdk() {
        return sdk;
    }

}
