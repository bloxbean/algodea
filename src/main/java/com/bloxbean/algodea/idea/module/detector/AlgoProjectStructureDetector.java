package com.bloxbean.algodea.idea.module.detector;

import com.bloxbean.algodea.idea.module.AlgorandModuleBuilder;
import com.bloxbean.algodea.idea.module.AlgorandModuleType;
import com.intellij.ide.util.importProject.ModuleDescriptor;
import com.intellij.ide.util.importProject.ProjectDescriptor;
import com.intellij.ide.util.projectWizard.importSources.DetectedProjectRoot;
import com.intellij.ide.util.projectWizard.importSources.DetectedSourceRoot;
import com.intellij.ide.util.projectWizard.importSources.ProjectFromSourcesBuilder;
import com.intellij.ide.util.projectWizard.importSources.ProjectStructureDetector;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class AlgoProjectStructureDetector extends ProjectStructureDetector {

  @NotNull
  @Override
  public DirectoryProcessingResult detectRoots(@NotNull File dir,
                                               @NotNull File[] children,
                                               @NotNull File base,
                                               @NotNull List<DetectedProjectRoot> result) {
    for (File child : children) {
      if (FileUtilRt.extensionEquals(child.getName(), "teal")) {
        File root;
        for (String pattern : new String[]{"src/main/teal", "src/test/teal", "src"}) {
          if ((root = findParentLike(pattern, dir, base)) != null) {
            result.add(new AlgoDetectedSourceRoot(root));
            return DirectoryProcessingResult.SKIP_CHILDREN;
          }
        }
      }
    }
    return DirectoryProcessingResult.PROCESS_CHILDREN;
  }

  private File findParentLike(String pattern, File dir, File limit) {
    String[] names = pattern.split("/");
    Collections.reverse(Arrays.asList(names));

    while (dir != null && dir.getPath().startsWith(limit.getPath())) {
      if (names[0].equals(dir.getName())) {
        if (checkParents(dir, names)) {
          return dir;
        }
      }

      dir = dir.getParentFile();
    }
    return null;
  }

  private boolean checkParents(File dir, String[] names) {
    for (String name : names) {
      if (dir.getName().equals(name)) {
        dir = dir.getParentFile();
      } else {
        return false;
      }
    }
    return true;
  }

  @Override
  public void setupProjectStructure(@NotNull Collection<DetectedProjectRoot> roots,
                                    @NotNull ProjectDescriptor projectDescriptor,
                                    @NotNull final ProjectFromSourcesBuilder builder) {

    List<ModuleDescriptor> modules = projectDescriptor.getModules();
    if (modules.isEmpty()) {
      modules = new ArrayList<>();
      for (DetectedProjectRoot root : roots) {
        ModuleDescriptor moduleDescriptor = new ModuleDescriptor(new File(builder.getBaseProjectPath()), AlgorandModuleType.getInstance(), ContainerUtil.emptyList()) {
          @Override
          public void updateModuleConfiguration(Module module, ModifiableRootModel rootModel) {
            super.updateModuleConfiguration(module, rootModel);
            AlgorandModuleBuilder moduleBuilder = AlgorandModuleType.getInstance().createModuleBuilder();
            moduleBuilder.moduleCreated(module);
          }
        };

        moduleDescriptor.addSourceRoot(new File(builder.getBaseProjectPath()), (DetectedSourceRoot) root);

        modules.add(moduleDescriptor);
      }
      projectDescriptor.setModules(modules);
    }
  }

  @Override
  public String getDetectorId() {
    return "Algorand";
  }
}
