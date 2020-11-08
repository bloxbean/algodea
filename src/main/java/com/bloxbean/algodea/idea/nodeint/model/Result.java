package com.bloxbean.algodea.idea.nodeint.model;

public class Result<T> {
    boolean successful;
    String response;
    T value;

    private Result(boolean successful) {
        this.successful = successful;
    }

    private Result(boolean successful, String response) {
        this.successful = successful;
        this.response = response;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public T getValue() {
        return value;
    }

    public Result withValue(T value) {
        this.value = value;
        return this;
    }

    public static Result error() {
        return new Result(false);
    }

    public static Result error(String response) {
        return new Result(false, response);
    }

    public static Result create(boolean status, String response) {
        return new Result(status, response);
    }

    public static Result success(String response) {
        return new Result(true, response);
    }
}
