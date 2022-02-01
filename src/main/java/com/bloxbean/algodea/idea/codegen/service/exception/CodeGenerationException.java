package com.bloxbean.algodea.idea.codegen.service.exception;

public class CodeGenerationException extends Exception {

    public CodeGenerationException(String message, Exception exception) {
        super(message, exception);
    }

    public CodeGenerationException(String message) {
        super(message);
    }
}
