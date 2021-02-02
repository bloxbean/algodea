package com.bloxbean.algodea.idea.module;

import com.bloxbean.algodea.idea.pkg.AlgoPkgJsonService;
import com.bloxbean.algodea.idea.pkg.exception.PackageJsonException;
import com.bloxbean.algodea.idea.pkg.model.AlgoPackageJson;
import com.bloxbean.algodea.idea.util.IdeaUtil;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

public class ProjectGeneratorUtil {
    private final static Logger LOG = Logger.getInstance(ProjectGeneratorUtil.class);

    public static void createStatefulContractFiles(Project project, VirtualFile srcRoot, String contractName, String approvalProgramName, String clearStateProgramName) {
        if (srcRoot == null)
            return;

            try {
                createFile(approvalProgramName, srcRoot, "_Algo.ApprovalProgram");
                createFile(clearStateProgramName, srcRoot, "_Algo.ClearStateProgram");
            } catch (Exception e) {
                if(LOG.isDebugEnabled()) {
                    LOG.error(e);
                }
                IdeaUtil.showNotification(project, "Project create",
                        "Stateful contract file generation failed : " + e.getMessage(), NotificationType.ERROR, null);
                return;
            }
            try {
                AlgoPkgJsonService pkgJsonService = AlgoPkgJsonService.getInstance(project);
                if(pkgJsonService == null) return;

                AlgoPackageJson packageJson = pkgJsonService.getPackageJson();

                if(packageJson == null)
                    packageJson = pkgJsonService.createPackageJson();

                AlgoPackageJson.StatefulContract statefulContract = new AlgoPackageJson.StatefulContract();
                statefulContract.setApprovalProgram("src/" + approvalProgramName);
                statefulContract.setClearStateProgram("src/" + clearStateProgramName);
                statefulContract.setName(contractName);

                packageJson.addStatefulContract(statefulContract);

                pkgJsonService.save();
            } catch (PackageJsonException e) {
                IdeaUtil.showNotification(project, "Project create",
                        "algo-package.json could not be created or found", NotificationType.ERROR, null);
            }
    }

    private static boolean createFile(String file, VirtualFile folder, final String templateName)
            throws Exception {
        final FileTemplate template = FileTemplateManager.getDefaultInstance().getInternalTemplate(templateName);

        VirtualFile srcFile = folder.createChildData(folder, file);
        VfsUtil.saveText(srcFile, template.getText());

        return true;
    }
}
