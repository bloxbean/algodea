package com.bloxbean.algodea.idea.module;

import com.bloxbean.algodea.idea.common.AlgoIcons;
import com.bloxbean.algodea.idea.pkg.AlgoPkgJsonService;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.ide.util.projectWizard.EmptyWebProjectTemplate;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableModelsProvider;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

public class AlgoDirectoryProjectGenerator extends EmptyWebProjectTemplate {
    private static final Logger LOG = Logger.getInstance(AlgoDirectoryProjectGenerator.class);

    @Override
    public @NotNull String getName() {
        return "Algorand";
    }

    @Override
    public @Nullable Icon getLogo() {
        return AlgoIcons.ALGO_ICON;
    }

    @Override
    public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir, @NotNull Object settings, @NotNull Module module) {

        if(project == null) {
            IdeaUtil.showNotification(project,
                    "Project creation",
                    "Project creation failed. Unexpected error.",
                    NotificationType.ERROR, null);
            return;
        }

        String basePath = project.getBasePath();

        if (project != null) {

            ApplicationManager.getApplication().runWriteAction(
                () -> {
                    try {
                        File src = new File(basePath + File.separator + "src");
                        src.mkdirs();

                        final ModifiableRootModel modifiableModel = ModifiableModelsProvider.SERVICE.getInstance().getModuleModifiableModel(module);

                        final VirtualFile moduleContentRoot =
                                LocalFileSystem.getInstance().refreshAndFindFileByPath(basePath.replace('\\', '/'));

                        final VirtualFile sourceRoot = LocalFileSystem.getInstance()
                                .refreshAndFindFileByPath(FileUtil.toSystemIndependentName(src.getAbsolutePath()));

                        ContentEntry contentEntry = modifiableModel.addContentEntry(moduleContentRoot);
                        contentEntry.addSourceFolder(sourceRoot, false);

                        modifiableModel.commit();
                    } catch (Exception e) {
                        IdeaUtil.showNotification(project,
                                "Project creation",
                                "Sources Root could not be created or marked properly. You can manually create and mark a folder as Sources Root",
                                NotificationType.WARNING, null);
                    }
                });

            //Create algo-package.json
            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    AlgoPkgJsonService.getInstance(project).createPackageJson();
                } catch (Exception e) {
                    IdeaUtil.showNotification(project,
                            "Project creation",
                            "algo-package.json could not be crated. Please create it " +
                                    "manually and restart the IDE.", NotificationType.WARNING, null);
                    if (LOG.isDebugEnabled()) {
                        LOG.error("Unable to create algo-package.json", e);
                    }
                }
            });
        }
    }

    @Override
    public String getDescription() {
        return "Algorand";
    }
}
