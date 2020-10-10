package com.bloxbean.algodea.idea.account.exception;

public class InvalidMnemonicException extends Exception {

    public InvalidMnemonicException(String message) {
        super(message);
    }

    public InvalidMnemonicException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
