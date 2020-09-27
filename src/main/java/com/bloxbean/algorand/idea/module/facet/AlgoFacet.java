package com.bloxbean.algorand.idea.module.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AlgoFacet extends Facet<AlgoFacetConfiguration> {
  public AlgoFacet(@NotNull final FacetType facetType,
                   @NotNull final Module module,
                   final String name,
                   @NotNull final AlgoFacetConfiguration configuration,
                   Facet underlyingFacet) {
    super(facetType, module, name, configuration, underlyingFacet);
  }

  @Nullable
  public static AlgoFacet getInstance(Module module) {
    return FacetManager.getInstance(module).getFacetByType(AlgoFacetType.FACET_TYPE_ID);
  }
}

