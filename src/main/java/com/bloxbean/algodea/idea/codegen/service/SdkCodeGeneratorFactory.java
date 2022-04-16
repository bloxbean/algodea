package com.bloxbean.algodea.idea.codegen.service;

import com.bloxbean.algodea.idea.codegen.CodeGenLang;
import com.bloxbean.algodea.idea.codegen.service.impl.JSSdkCodeGenerator;
import com.bloxbean.algodea.idea.codegen.service.impl.PythonSdkCodeGenerator;

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
