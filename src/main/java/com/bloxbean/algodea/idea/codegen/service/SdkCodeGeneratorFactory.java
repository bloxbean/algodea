package com.bloxbean.algodea.idea.codegen.service;

import com.bloxbean.algodea.idea.codegen.CodeGenLang;

public class SdkCodeGeneratorFactory {
    private JSSdkCodeGenerator jsSdkCodeGenerator;
    private PythonSdkCodeGenerator pythonSdkCodeGenerator;

    public SdkCodeGeneratorFactory() {
        jsSdkCodeGenerator = new JSSdkCodeGenerator();
        pythonSdkCodeGenerator = new PythonSdkCodeGenerator();
    }

    public SdkCodeGenerator getSdkCodeGenerator(CodeGenLang lang) {
        if (lang == CodeGenLang.JS) {
            return jsSdkCodeGenerator;
        } else if (lang == CodeGenLang.PYTHON) {
            return pythonSdkCodeGenerator;
        } else
            return null;
    }
}
