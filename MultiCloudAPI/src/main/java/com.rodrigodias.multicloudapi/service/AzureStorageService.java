package com.rodrigodias.multicloudapi.services;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.rodrigodias.multicloudapi.models.FileMetadata;
import com.rodrigodias.multicloudapi.models.FileList;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AzureStorageService implements CloudStorageService {

    private static final Logger logger = LoggerFactory.getLogger(AzureStorageService.class);
    private final BlobServiceClient blobServiceClient;

    @Value("${azure.storage.container}")
    private String containerName;

    @Override
    public CompletableFuture<String> uploadFile(String fileName, byte[] fileContent) throws CloudOperationException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Subindo arquivo {} para Azure Blob", fileName);
                BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
                containerClient.getBlobClient(fileName).upload(new ByteArrayInputStream(fileContent), fileContent.length, true);
                String url = containerClient.getBlobClient(fileName).getBlobUrl();
                logger.info("Arquivo {} salvo com sucesso em {}", fileName, url);
                return url;
            } catch (Exception e) {
                logger.error("Erro ao subir arquivo para Azure Blob: {}", e.getMessage());
                throw new CloudOperationException("Erro ao subir arquivo para Azure Blob", e);
            }
        });
    }

    @Override
    public CompletableFuture<byte[]> downloadFile(String fileName) throws CloudOperationException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Baixando arquivo {} do Azure Blob", fileName);
                BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
                byte[] content = containerClient.getBlobClient(fileName).downloadContent().toBytes();
                logger.info("Arquivo {} baixado com sucesso", fileName);
                return content;
            } catch (Exception e) {
                logger.error("Erro ao baixar arquivo do Azure Blob: {}", e.getMessage());
                throw new CloudOperationException("Erro ao baixar arquivo do Azure Blob", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteFile(String fileName) throws CloudOperationException {
        return CompletableFuture.runAsync(() -> {
            try {
                logger.info("Deletando arquivo {} do Azure Blob", fileName);
                BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
                containerClient.getBlobClient(fileName).delete();
                logger.info("Arquivo {} deletado com sucesso", fileName);
            } catch (Exception e) {
                logger.error("Erro ao deletar arquivo do Azure Blob: {}", e.getMessage());
                throw new CloudOperationException("Erro ao deletar arquivo do Azure Blob", e);
            }
        });
    }

    @Override
    public CompletableFuture<FileList> listFiles(int page, int size) throws CloudOperationException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Listando arquivos do Azure Blob no container {}, página {}, tamanho {}", containerName, page, size);
                BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
                var blobs = containerClient.listBlobs().stream().collect(Collectors.toList());
                int start = page * size;
                int end = Math.min(start + size, blobs.size());
                List<FileMetadata> files = blobs.subList(start, end).stream()
                        .map(blob -> new FileMetadata(
                                blob.getName(),
                                "Azure",
                                containerClient.getBlobClient(blob.getName()).getBlobUrl(),
                                blob.getProperties().getContentLength(),
                                LocalDateTime.now(),
                                blob.getProperties().getContentType() != null ? blob.getProperties().getContentType() : "application/octet-stream"
                        ))
                        .collect(Collectors.toList());
                long totalSize = files.stream().mapToLong(FileMetadata::getSize).sum();
                FileList fileList = new FileList("Azure", files, totalSize, files.size());
                logger.info("Listagem concluída: {} arquivos retornados (página {})", files.size(), page);
                return fileList;
            } catch (Exception e) {
                logger.error("Erro ao listar arquivos do Azure Blob: {}", e.getMessage());
                throw new CloudOperationException("Erro ao listar arquivos do Azure Blob", e);
            }
        });
    }

    @Override
    public String getProviderName() {
        return "Azure";
    }
}