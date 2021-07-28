package com.bloxbean.algodea.idea.debugger.service;

import com.intellij.execution.process.OSProcessHandler;

public interface DebugResultListener {
    default public void attachProcess(OSProcessHandler processHandler) {

    }

    public void error(String message);
    public void info(String message);
    public void warn(String msg);

    default public void error(String message, Throwable t) {
        error(message + " : " + t.getMessage());
    }
}
