package com.bloxbean.algodea.idea.compile.service;

import com.bloxbean.algodea.idea.stateless.model.LogicSigParams;

public interface CompileService {
    public void compile(String source, String destination, CompilationResultListener compilationResultListener);

    public void lsig(String source, String compileDestination, String lsigDestination, LogicSigParams logicSigParams, CompilationResultListener compilationResultListener);
}
