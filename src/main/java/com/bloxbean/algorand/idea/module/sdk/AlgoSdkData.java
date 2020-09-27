package com.bloxbean.algorand.idea.module.sdk;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.util.xmlb.XmlSerializerUtil;

@SuppressWarnings("UnusedDeclaration")
public class AlgoSdkData implements SdkAdditionalData, PersistentStateComponent<AlgoSdkData> {
  private String homePath = "";
  private String version = "";
  private boolean isLocal = true;

  public AlgoSdkData() {
  }

  public AlgoSdkData(String homePath, String version) {
    this.homePath = homePath;
    this.version = version;
  }

  public String getHomePath() {
    return homePath;
  }

  public String getVersion() {
    return version;
  }

  @SuppressWarnings({"CloneDoesntCallSuperClone"})
  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public AlgoSdkData getState() {
    return this;
  }

  public void loadState(AlgoSdkData state) {
    XmlSerializerUtil.copyBean(state, this);
  }
}
