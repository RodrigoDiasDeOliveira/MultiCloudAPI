package com.rodrigodias.multicloudapi.services;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
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
public class GcpStorageService implements CloudStorageService {

    private static final Logger logger = LoggerFactory.getLogger(GcpStorageService.class);
    private final Storage storage;

    @Value("${gcp.storage.bucket}")
    private String bucketName;

    @Override
    public CompletableFuture<String> uploadFile(String fileName, byte[] fileContent) throws CloudOperationException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Subindo arquivo {} para GCP Storage", fileName);
                Blob blob = storage.create(
                        Blob.newBuilder(bucketName, fileName).build(),
                        new ByteArrayInputStream(fileContent));
                String url = String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
                logger.info("Arquivo {} salvo com sucesso em {}", fileName, url);
                return url;
            } catch (Exception e) {
                logger.error("Erro ao subir arquivo para GCP Storage: {}", e.getMessage());
                throw new CloudOperationException("Erro ao subir arquivo para GCP Storage", e);
            }
        });
    }

    @Override
    public CompletableFuture<byte[]> downloadFile(String fileName) throws CloudOperationException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Baixando arquivo {} do GCP Storage", fileName);
                Blob blob = storage.get(BlobId.of(bucketName, fileName));
                byte[] content = blob.getContent();
                logger.info("Arquivo {} baixado com sucesso", fileName);
                return content;
            } catch (Exception e) {
                logger.error("Erro ao baixar arquivo do GCP Storage: {}", e.getMessage());
                throw new CloudOperationException("Erro ao baixar arquivo do GCP Storage", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteFile(String fileName) throws CloudOperationException {
        return CompletableFuture.runAsync(() -> {
            try {
                logger.info("Deletando arquivo {} do GCP Storage", fileName);
                storage.delete(BlobId.of(bucketName, fileName));
                logger.info("Arquivo {} deletado com sucesso", fileName);
            } catch (Exception e) {
                logger.error("Erro ao deletar arquivo do GCP Storage: {}", e.getMessage());
                throw new CloudOperationException("Erro ao deletar arquivo do GCP Storage", e);
            }
        });
    }

    @Override
    public CompletableFuture<FileList> listFiles(int page, int size) throws CloudOperationException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Listando arquivos do GCP Storage no bucket {}, página {}, tamanho {}", bucketName, page, size);
                var options = Storage.BlobListOption.pageSize(size);
                if (page > 0) options = Storage.BlobListOption.currentPage(page);
                var blobs = storage.list(bucketName, options).iterateAll();
                List<FileMetadata> files = blobs.stream()
                        .map(blob -> new FileMetadata(
                                blob.getName(),
                                "GCP",
                                String.format("https://storage.googleapis.com/%s/%s", bucketName, blob.getName()),
                                blob.getSize(),
                                LocalDateTime.ofInstant(
                                        java.time.Instant.ofEpochMilli(blob.getUpdateTime()),
                                        java.time.ZoneId.systemDefault()),
                                blob.getContentType() != null ? blob.getContentType() : "application/octet-stream"
                        ))
                        .limit(size)
                        .collect(Collectors.toList());
                long totalSize = files.stream().mapToLong(FileMetadata::getSize).sum();
                FileList fileList = new FileList("GCP", files, totalSize, files.size());
                logger.info("Listagem concluída: {} arquivos retornados (página {})", files.size(), page);
                return fileList;
            } catch (Exception e) {
                logger.error("Erro ao listar arquivos do GCP Storage: {}", e.getMessage());
                throw new CloudOperationException("Erro ao listar arquivos do GCP Storage", e);
            }
        });
    }

    @Override
    public String getProviderName() {
        return "GCP";
    }
}