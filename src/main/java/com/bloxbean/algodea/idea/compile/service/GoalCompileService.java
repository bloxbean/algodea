package com.bloxbean.algodea.idea.compile.service;

import com.bloxbean.algodea.idea.compile.CompileException;
import com.bloxbean.algodea.idea.configuration.model.AlgoLocalSDK;
import com.bloxbean.algodea.idea.core.exception.LocalSDKNotConfigured;
import com.bloxbean.algodea.idea.nodeint.AlgoServerConfigurationHelper;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.io.FileUtil;
import com.twelvemonkeys.lang.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GoalCompileService extends BaseCompileService {
    private AlgoLocalSDK localSDK;
    private String cwd;

    public GoalCompileService(@NotNull Project project) throws LocalSDKNotConfigured {
        this.cwd = project.getBasePath();

        localSDK = AlgoServerConfigurationHelper.getCompilerLocalSDK(project);
        if(localSDK == null) {
            throw new LocalSDKNotConfigured("Algorand Local SDK is not configured.");
        }
    }

    @Override
    public void compile(String sourceFilePath, String destination, CompilationResultListener listener) {
        if(!StringUtil.isEmpty(destination)) {
            FileUtil.createParentDirs(new File(destination));
        }

        List<String> cmd = new ArrayList<>();
        cmd.add(localSDK.getHome() + File.separator + "bin" + File.separator + "goal");
        cmd.add("clerk");
        cmd.add("compile");

        cmd.add(sourceFilePath);

        if(destination != null) {
            cmd.add("-o");
            cmd.add(destination);
        }

        OSProcessHandler handler;
        try {
            handler = new OSProcessHandler(
                    new GeneralCommandLine(cmd).withWorkDirectory(cwd)
            );

        } catch (ExecutionException ex) {
            ex.printStackTrace();
            failed(listener, sourceFilePath, "Compilation failed : " + ex.getMessage(), ex);
            return;
        }

        listener.info("Compiling TEAL file using \"goal\" ...");
        listener.attachProcess(handler);

        handler.addProcessListener(new ProcessListener() {
            @Override
            public void startNotified(@NotNull ProcessEvent event) {

            }

            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                if(event.getExitCode() == 0) {
                    listener.info("Compilation successful.");
                    listener.onSuccessful(sourceFilePath, destination);
                } else {
                    failed(listener, sourceFilePath, "Compilation failed.", new CompileException("Goal compilation process failed with error."));
                }
            }

            @Override
            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {

            }
        });


        return;
    }
}
