package com.rodrigodias.multicloudapi.services;

import java.util.concurrent.CompletableFuture;

public interface CloudService {
    String getProviderName();
    <T> CompletableFuture<T> executeOperation(String operation, Object... params) throws CloudOperationException;
}