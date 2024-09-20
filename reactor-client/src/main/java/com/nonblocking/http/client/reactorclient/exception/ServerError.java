package com.nonblocking.http.client.reactorclient.exception;

public class ServerError extends RuntimeException {

    public ServerError() {
        super("Internal Server Error");
    }
}
