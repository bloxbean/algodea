package com.bloxbean.algodea.idea.compile.service;

import com.bloxbean.algodea.idea.compile.CompileException;
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

public class PyTealCompileService {

    private String cwd;

    public PyTealCompileService(@NotNull Project project)  {
        this.cwd = project.getBasePath();
    }

    public String compile(String python, String source, String destination, CompilationResultListener listener) throws CompileException {
        if(!StringUtil.isEmpty(destination)) {
            FileUtil.createParentDirs(new File(destination));
        }

        File generatedFolder = new File(destination).getParentFile();

        if(generatedFolder != null && generatedFolder.exists()) {
            this.cwd = generatedFolder.getAbsolutePath();
        }

        List<String> cmd = new ArrayList<>();
        cmd.add(python);

        cmd.add(source);

        OSProcessHandler handler;
        try {
            handler = new OSProcessHandler(
                    new GeneralCommandLine(cmd).withWorkDirectory(cwd)
            );

        } catch (ExecutionException ex) {
            failed(listener, source, "PyTeal compilation failed : " + ex.getMessage(), ex);
            return null;
        }

        listener.info("Compiling PyTeal file using \"python\" ...");
        listener.attachProcess(handler);

        StringBuffer output = new StringBuffer();
        handler.addProcessListener(new ProcessListener() {
            @Override
            public void startNotified(@NotNull ProcessEvent event) {

            }

            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                if(event.getExitCode() == 0) {
                    listener.info("PyTeal Compilation successful.");
                    listener.onSuccessful(source, destination);
                } else { //Error
                    failed(listener, source, "", new CompileException("PyTeal compilation error"));
                    output.delete(0, output.length()); //delete
                }
            }

            @Override
            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                if(output.length() == 0
                        && event.getText() != null
                        && event.getText().contains("python")) {
                    return;
                }

                if(event.getText() != null) {
                    output.append(event.getText());
                }
            }
        });

        handler.waitFor();

        if(handler.getExitCode() != 0) {
            throw new CompileException("PyTeal file compilation failed");
        }

        return output.toString();
    }

    protected void failed(CompilationResultListener resultListener, String source, String message, Throwable t) {
        resultListener.error(message);
        resultListener.onFailure(source, t);
    }
}
