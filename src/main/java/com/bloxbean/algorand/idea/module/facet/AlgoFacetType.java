package com.bloxbean.algorand.idea.module.facet;

import com.bloxbean.algorand.idea.common.AlgoIcons;
import com.bloxbean.algorand.idea.module.AlgorandModuleType;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AlgoFacetType extends FacetType<AlgoFacet, AlgoFacetConfiguration> {
  public final static FacetTypeId<AlgoFacet> FACET_TYPE_ID = new FacetTypeId<AlgoFacet>("Algorand");

  public AlgoFacetType() {
    super(FACET_TYPE_ID, AlgoModuleSettings.FACET_ID, AlgoModuleSettings.FACET_NAME);
  }

  @Override
  public AlgoFacetConfiguration createDefaultConfiguration() {
    return new AlgoFacetConfiguration();
  }

  @Override
  public AlgoFacet createFacet(@NotNull final Module module,
                               final String name,
                               @NotNull final AlgoFacetConfiguration configuration,
                               @Nullable final Facet underlyingFacet) {
    return new AlgoFacet(this, module, name, configuration, underlyingFacet);
  }

  @Override
  public boolean isSuitableModuleType(final ModuleType moduleType) {
    //return moduleType instanceof JavaModuleType;
    return moduleType instanceof AlgorandModuleType;
  }

  @Override
  public Icon getIcon() {
    return AlgoIcons.ALGO_ICON;
  }

  public static AlgoFacetType getInstance() {
    return findInstance(AlgoFacetType.class);
  }

}
