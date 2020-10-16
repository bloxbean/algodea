package com.bloxbean.algodea.idea.pkg.exception;

public class PackageJsonException extends Exception {
    public PackageJsonException(String message) {
        super(message);
    }

    public PackageJsonException(String msg, Throwable t) {
        super(msg, t);
    }
}
