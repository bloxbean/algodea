package com.bloxbean.algodea.idea.module.detector;

import com.bloxbean.algodea.idea.module.AlgorandModuleType;
import com.intellij.ide.util.importProject.ModuleDescriptor;
import com.intellij.ide.util.importProject.ProjectDescriptor;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.importSources.DetectedProjectRoot;
import com.intellij.ide.util.projectWizard.importSources.ProjectFromSourcesBuilder;
import com.intellij.ide.util.projectWizard.importSources.ProjectStructureDetector;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
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
    if (modules.isEmpty())
    {
      modules = new ArrayList<>();
      for (DetectedProjectRoot root : roots)
      {
        modules.add(
                new ModuleDescriptor(root.getDirectory(), AlgorandModuleType.getInstance(),
                        ContainerUtil.emptyList()));
      }
      projectDescriptor.setModules(modules);
    }

//    if (!roots.isEmpty() && !builder.hasRootsFromOtherDetectors(this)) {
//      List<ModuleDescriptor> modules = projectDescriptor.getModules();
//      if (modules.isEmpty()) {
//        modules = new ArrayList<ModuleDescriptor>();
//
//        for (DetectedProjectRoot root : roots) {
//          modules.add(new ModuleDescriptor(new File(builder.getBaseProjectPath()), AlgorandModuleType.getInstance(), ContainerUtil.emptyList()){
//
//            @Override
//            public void updateModuleConfiguration(Module module, ModifiableRootModel rootModel) {
//              super.updateModuleConfiguration(module, rootModel);
//
//              //TOOD
////              for (ModuleBuilder moduleBuilder : builder.getContext().getAllBuilders()) {
////                if (moduleBuilder instanceof AlgorandModuleBuilder) {
////                  ((AlgorandModuleBuilder) moduleBuilder).moduleCreated(module);
////                  return;
////                }
////              }
//            }
//          });
//        }
//        projectDescriptor.setModules(modules);
//      }
//    }
  }

  @Override
  public List<ModuleWizardStep> createWizardSteps(ProjectFromSourcesBuilder builder, ProjectDescriptor projectDescriptor, Icon stepIcon) {

//    //TODO
//    for (ModuleBuilder moduleBuilder : builder.getContext().getAllBuilders()) {
//      if (moduleBuilder instanceof AlgorandModuleBuilder) {
//        ArrayList<ModuleWizardStep> steps = new ArrayList<ModuleWizardStep>();
//        steps.add(ProjectWizardStepFactory.getInstance().createProjectJdkStep(builder.getContext()));
//        steps.add(new AlgorandModuleWizardStep((AlgorandModuleBuilder) moduleBuilder));
//        return steps;
//      }
//    }
    return Collections.emptyList();
  }

  @Override
  public String getDetectorId() {
    return "Algorand";
  }
}
