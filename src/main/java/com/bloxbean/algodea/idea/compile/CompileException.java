package com.bloxbean.algodea.idea.compile;

public class CompileException extends Exception {
    public CompileException(String messge) {
        super(messge);
    }

    public CompileException(String message, Throwable t) {
        super(message, t);
    }
}
