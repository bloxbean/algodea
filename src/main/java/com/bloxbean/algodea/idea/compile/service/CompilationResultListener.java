package com.bloxbean.algodea.idea.compile.service;

import com.intellij.execution.process.OSProcessHandler;

public interface CompilationResultListener {
    default public void attachProcess(OSProcessHandler processHandler) {

    }

    public void error(String message);
    public void info(String message);
    public void warn(String msg);

    public void onSuccessful(String sourceFile, String outputFile);
    public void onFailure(String sourceFile);
}
