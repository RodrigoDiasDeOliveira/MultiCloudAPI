package com.rodrigodias.multicloudapi.services;

import com.rodrigodias.multicloudapi.models.FileList;

import java.util.concurrent.CompletableFuture;

public interface CloudStorageService extends CloudService {
    CompletableFuture<String> uploadFile(String fileName, byte[] fileContent) throws CloudOperationException;
    CompletableFuture<byte[]> downloadFile(String fileName) throws CloudOperationException;
    CompletableFuture<Void> deleteFile(String fileName) throws CloudOperationException;
    CompletableFuture<FileList> listFiles(int page, int size) throws CloudOperationException;

    @Override
    default <T> CompletableFuture<T> executeOperation(String operation, Object... params) throws CloudOperationException {
        return switch (operation) {
            case "upload" -> (CompletableFuture<T>) uploadFile((String) params[0], (byte[]) params[1]);
            case "download" -> (CompletableFuture<T>) downloadFile((String) params[0]);
            case "delete" -> (CompletableFuture<T>) deleteFile((String) params[0]);
            case "list" -> (CompletableFuture<T>) listFiles((int) params[0], (int) params[1]);
            default -> throw new CloudOperationException("Operação não suportada: " + operation, null);
        };
    }
}