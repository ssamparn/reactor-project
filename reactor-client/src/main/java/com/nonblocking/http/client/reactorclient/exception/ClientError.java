package com.nonblocking.http.client.reactorclient.exception;

public class ClientError extends RuntimeException {

    public ClientError() {
        super("Bad Request");
    }
}
