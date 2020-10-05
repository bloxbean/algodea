package com.bloxbean.algorand.idea.nodeint.exception;

public class ApiCallException extends Exception {
    public ApiCallException(String messge) {
        super(messge);
    }

    public ApiCallException(String message, Exception e) {
        super(message, e);
    }
}
