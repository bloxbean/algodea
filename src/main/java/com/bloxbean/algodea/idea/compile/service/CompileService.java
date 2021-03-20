package com.bloxbean.algodea.idea.compile.service;

import com.bloxbean.algodea.idea.stateless.model.LogicSigParams;

public interface CompileService {
    public void compile(String source, String destination, CompilationResultListener compilationResultListener);

    //Compile and generate logic sig
    public void lsig(String source, String compileDestination, String lsigDestination, LogicSigParams logicSigParams, CompilationResultListener compilationResultListener);

    //Generate logic sig from compiled file
    public void generateLogicSig(String sourceFile, String compiledFile, String lsigDestination, LogicSigParams logicSigParams, CompilationResultListener listener);
}
