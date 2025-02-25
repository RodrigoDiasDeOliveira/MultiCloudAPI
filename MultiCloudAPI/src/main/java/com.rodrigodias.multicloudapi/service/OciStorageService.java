package com.rodrigodias.multicloudapi.services;

import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.ListObjectsRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
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
public class OciStorageService implements CloudStorageService {

    private static final Logger logger = LoggerFactory.getLogger(OciStorageService.class);
    private final ObjectStorageClient objectStorageClient;

    @Value("${oci.objectstorage.bucket}")
    private String bucketName;

    @Value("${oci.objectstorage.namespace}")
    private String namespace;

    @Override
    public CompletableFuture<String> uploadFile(String fileName, byte[] fileContent) throws CloudOperationException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Subindo arquivo {} para OCI Object Storage", fileName);
                PutObjectRequest request = PutObjectRequest.builder()
                        .namespaceName(namespace)
                        .bucketName(bucketName)
                        .objectName(fileName)
                        .contentLength((long) fileContent.length)
                        .putObjectBody(new ByteArrayInputStream(fileContent))
                        .build();
                objectStorageClient.putObject(request);
                String url = String.format("https://objectstorage.%s.oraclecloud.com/n/%s/b/%s/o/%s",
                        objectStorageClient.getRegion().getRegionId(), namespace, bucketName, fileName);
                logger.info("Arquivo {} salvo com sucesso em {}", fileName, url);
                return url;
            } catch (Exception e) {
                logger.error("Erro ao subir arquivo para OCI Object Storage: {}", e.getMessage());
                throw new CloudOperationException("Erro ao subir arquivo para OCI Object Storage", e);
            }
        });
    }

    @Override
    public CompletableFuture<byte[]> downloadFile(String fileName) throws CloudOperationException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Baixando arquivo {} do OCI Object Storage", fileName);
                GetObjectRequest request = GetObjectRequest.builder()
                        .namespaceName(namespace)
                        .bucketName(bucketName)
                        .objectName(fileName)
                        .build();
                GetObjectResponse response = objectStorageClient.getObject(request);
                byte[] content = response.getInputStream().readAllBytes();
                logger.info("Arquivo {} baixado com sucesso", fileName);
                return content;
            } catch (Exception e) {
                logger.error("Erro ao baixar arquivo do OCI Object Storage: {}", e.getMessage());
                throw new CloudOperationException("Erro ao baixar arquivo do OCI Object Storage", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteFile(String fileName) throws CloudOperationException {
        return CompletableFuture.runAsync(() -> {
            try {
                logger.info("Deletando arquivo {} do OCI Object Storage", fileName);
                DeleteObjectRequest request = DeleteObjectRequest.builder()
                        .namespaceName(namespace)
                        .bucketName(bucketName)
                        .objectName(fileName)
                        .build();
                objectStorageClient.deleteObject(request);
                logger.info("Arquivo {} deletado com sucesso", fileName);
            } catch (Exception e) {
                logger.error("Erro ao deletar arquivo do OCI Object Storage: {}", e.getMessage());
                throw new CloudOperationException("Erro ao deletar arquivo do OCI Object Storage", e);
            }
        });
    }

    @Override
    public CompletableFuture<FileList> listFiles(int page, int size) throws CloudOperationException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Listando arquivos do OCI Object Storage no bucket {}, página {}, tamanho {}", bucketName, page, size);
                ListObjectsRequest request = ListObjectsRequest.builder()
                        .namespaceName(namespace)
                        .bucketName(bucketName)
                        .limit(size)
                        .startAfter(page > 0 ? getLastKeyFromPreviousPage(page, size) : null)
                        .build();
                var response = objectStorageClient.listObjects(request);
                List<FileMetadata> files = response.getListObjects().getObjects().stream()
                        .map(obj -> new FileMetadata(
                                obj.getName(),
                                "OCI",
                                String.format("https://objectstorage.%s.oraclecloud.com/n/%s/b/%s/o/%s",
                                        objectStorageClient.getRegion().getRegionId(), namespace, bucketName, obj.getName()),
                                obj.getSize(),
                                LocalDateTime.now(),
                                "application/octet-stream"
                        ))
                        .collect(Collectors.toList());
                long totalSize = files.stream().mapToLong(FileMetadata::getSize).sum();
                FileList fileList = new FileList("OCI", files, totalSize, files.size());
                logger.info("Listagem concluída: {} arquivos retornados (página {})", files.size(), page);
                return fileList;
            } catch (Exception e) {
                logger.error("Erro ao listar arquivos do OCI Object Storage: {}", e.getMessage());
                throw new CloudOperationException("Erro ao listar arquivos do OCI Object Storage", e);
            }
        });
    }

    private String getLastKeyFromPreviousPage(int page, int size) {
        // Simulação simples; em produção, usar um marcador de página real
        return null;
    }

    @Override
    public String getProviderName() {
        return "OCI";
    }
}