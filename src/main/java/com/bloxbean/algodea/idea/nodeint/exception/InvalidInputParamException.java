package com.bloxbean.algodea.idea.nodeint.exception;

public class InvalidInputParamException extends Exception {
    public InvalidInputParamException(String message) {
        super(message);
    }

    public InvalidInputParamException(String message, Throwable t) {
        super(message, t);
    }
}
