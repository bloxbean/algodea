package com.bloxbean.algodea.idea.pkg;

import com.bloxbean.algodea.idea.pkg.exception.PackageJsonException;
import com.bloxbean.algodea.idea.pkg.model.AlgoPackageJson;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import static com.bloxbean.algodea.idea.module.AlgoModuleConstant.ALGO_PACKAGE_JSON;

public class AlgoPkgJsonService {
    private final static Logger LOG = Logger.getInstance(AlgoPkgJsonService.class);

    private static ObjectMapper mapper;
    private Project project;
    private AlgoPackageJson packageJson;
    private boolean algoProject;
    private boolean isDirty;

    public static AlgoPkgJsonService getInstance(@NotNull Project project) {
        if(project == null)
            return null;

        return project.getService(AlgoPkgJsonService.class);
    }

    public AlgoPkgJsonService(@NotNull Project project) {
        mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.project = project;

        ApplicationManager.getApplication().invokeLater(() -> {
            final String basePath = project.getBasePath();
            VirtualFile pkgJson = VfsUtil.findFileByIoFile(new File(basePath, ALGO_PACKAGE_JSON), true);
            if(pkgJson != null && pkgJson.exists()) {
                algoProject = true;
                attachListener();
            }
        });
    }

    private void attachListener() {
//        project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
//            @Override
//            public void after(@NotNull List<? extends VFileEvent> events) {
//                // handle the events
//                for(VFileEvent evt: events) {
//                    VirtualFile file = evt.getFile();
//                    if(ALGO_PACKAGE_JSON.equals(file.getName())) {
//                        if(ProjectFileIndex.getInstance(project).isInContent(evt.getFile())) {
//                            markDirty();
//                            System.out.println("Found change.....>>>> ");
//                        }
//                    }
//                }
//            }
//        });
    }

    public boolean isAlgoProject() {
        return algoProject;
    }

    public AlgoPackageJson getPackageJson() throws PackageJsonException {
        if(project == null) return null;
        if(packageJson == null || isDirty)
            load();

        return packageJson;
    }

    public AlgoPackageJson.StatefulContract getStatefulContract(String name) throws PackageJsonException {
        AlgoPackageJson packageJson = getPackageJson();
        if(packageJson == null) return null;

        return packageJson.getStatefulContractByName(name);
    }

    public void setStatefulContract(AlgoPackageJson.StatefulContract contract) {
        if(packageJson == null)
            return;

        packageJson.addStatefulContract(contract);
    }

    public AlgoPackageJson.StatefulContract getFirstStatefulContract() {
        if(packageJson == null)
            return null;

        return packageJson.getFirstStatefulContract();
    }

    public AlgoPackageJson createPackageJson() throws PackageJsonException {
        if(project == null) return null;

        this.packageJson = new AlgoPackageJson();
        packageJson.setName(project.getName());
        packageJson.setVersion("1.0");

        save();

        return packageJson;
    }

    public void load() throws PackageJsonException {
        isDirty = false;
        if(project == null) return;

        String projectBasePath = project.getBasePath();
        String pkgJsonPath = projectBasePath + File.separator + ALGO_PACKAGE_JSON;

        if(!new File(pkgJsonPath).exists()) {
            throw new PackageJsonException(ALGO_PACKAGE_JSON + " file doesn't exist.");
        }

        try {
            packageJson = readPackageJson(pkgJsonPath);
        } catch (IOException e) {
            throw new PackageJsonException(String.format("Unable to read %s file at locatioin %s , Reason : %s", ALGO_PACKAGE_JSON, pkgJsonPath, e.getMessage()), e);
        }
    }

    public void save() throws PackageJsonException {
        String path = getPackageJsonPath();
        if(path == null)
            throw new PackageJsonException("Package json path cannot be null");

        File file = new File(path);
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, packageJson);
        } catch (IOException e) {
            throw new PackageJsonException(String.format("Unable to write %s file at locatioin %s", ALGO_PACKAGE_JSON, path));
        }

        try {
            //Refresh
            VfsUtil.findFileByIoFile(file, false).refresh(true, false);
        } catch (Exception e) {
            if(LOG.isDebugEnabled())
                LOG.warn(e);
        }
    }

    public void markDirty() {
        this.isDirty = true;
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    private AlgoPackageJson readPackageJson(String path) throws IOException {
        return mapper.readValue(new File(path), AlgoPackageJson.class);
    }

    private String getPackageJsonPath() {
        String projectBasePath = project.getBasePath();
        return projectBasePath + File.separator + ALGO_PACKAGE_JSON;
    }

}
