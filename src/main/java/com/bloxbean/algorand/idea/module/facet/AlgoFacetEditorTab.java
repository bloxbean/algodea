package com.bloxbean.algorand.idea.module.facet;

import com.bloxbean.algorand.idea.module.AlgoSdkPanel;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AlgoFacetEditorTab extends FacetEditorTab {

  private final AlgoSdkPanel mySdkPanel;
  private final AlgoModuleSettings mySettings;

  public AlgoFacetEditorTab(AlgoModuleSettings settings) {
    mySettings = settings;
    mySdkPanel = new AlgoSdkPanel();
  }

  @Nls
  @Override
  public String getDisplayName() {
    return "Algorand Facet";
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    return mySdkPanel;
  }

  @Override
  public boolean isModified() {
    return !StringUtil.equals(mySettings.algorandSdkName, mySdkPanel.getSdkName());
  }

  @Override
  public void apply() throws ConfigurationException {
    mySettings.algorandSdkName = mySdkPanel.getSdkName();
  }

  @Override
  public void reset() {
    final Sdk sdk = ProjectJdkTable.getInstance().findJdk(mySettings.algorandSdkName);
    if (sdk != null) {
      mySdkPanel.setSdk(sdk);
    }
  }

  @Override
  public void disposeUIResources() {
  }
}