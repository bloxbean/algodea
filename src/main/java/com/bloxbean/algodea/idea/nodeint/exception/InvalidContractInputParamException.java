package com.bloxbean.algodea.idea.nodeint.exception;

public class InvalidContractInputParamException extends Exception {
    public InvalidContractInputParamException(String message) {
        super(message);
    }

    public InvalidContractInputParamException(String message, Throwable t) {
        super(message, t);
    }
}
