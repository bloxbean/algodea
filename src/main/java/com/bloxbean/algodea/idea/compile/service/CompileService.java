package com.bloxbean.algodea.idea.compile.service;

import com.bloxbean.algodea.idea.nodeint.service.LogListener;

public interface CompileService {
    public void compile(String source, String destination, CompilationResultListener compilationResultListener);
}
