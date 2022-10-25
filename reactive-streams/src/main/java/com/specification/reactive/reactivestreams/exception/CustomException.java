package com.specification.reactive.reactivestreams.exception;

public class CustomException extends Throwable {
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CustomException(Throwable err) {
        this.message = err.getMessage();
    }
}
