package com.bloxbean.algodea.idea.pkg;

import com.bloxbean.algodea.idea.pkg.exception.PackageJsonException;
import com.bloxbean.algodea.idea.pkg.model.AlgoPackageJson;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import static com.bloxbean.algodea.idea.module.AlgoModuleConstant.ALGO_PACKAGE_JSON;

public class AlgoPkgJsonService {
    private static ObjectMapper mapper;
    private Project project;
    private boolean algoProject;

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
            }
        });
    }

    public boolean isAlgoProject() {
        return algoProject;
    }

    public AlgoPackageJson createPackageJson() throws PackageJsonException {
        if(project == null) return null;

        AlgoPackageJson packageJson = new AlgoPackageJson();
        packageJson.setName(project.getName());
        packageJson.setVersion("1.0");

        writeToPackageJson(packageJson);

        return packageJson;
    }

    public AlgoPackageJson loadPackageJson() throws PackageJsonException {
        if(project == null) return null;

        String projectBasePath = project.getBasePath();
        String pkgJsonPath = projectBasePath + File.separator + ALGO_PACKAGE_JSON;

        try {
            return readPackageJson(pkgJsonPath);
        } catch (IOException e) {
            throw new PackageJsonException(String.format("Unable to read %s file at locatioin %s", ALGO_PACKAGE_JSON, pkgJsonPath));
        }
    }

    public void writeToPackageJson(AlgoPackageJson packageJson) throws PackageJsonException {
        String path = getPackageJsonPath();
        if(path == null)
            throw new PackageJsonException("Package json path cannot be null");

        File file = new File(path);
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, packageJson);
        } catch (IOException e) {
            throw new PackageJsonException(String.format("Unable to write %s file at locatioin %s", ALGO_PACKAGE_JSON, path));
        }
    }

    private AlgoPackageJson readPackageJson(String path) throws IOException {
        return mapper.readValue(new File(path), AlgoPackageJson.class);
    }

    private String getPackageJsonPath() {
        String projectBasePath = project.getBasePath();
        return projectBasePath + File.separator + ALGO_PACKAGE_JSON;
    }

}
