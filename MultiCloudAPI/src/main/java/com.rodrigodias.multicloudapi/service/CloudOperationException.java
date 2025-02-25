package com.rodrigodias.multicloudapi.services;

public class CloudOperationException extends Exception {
    public CloudOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}