package com.rodrigodias.multicloudapi.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.rodrigodias.multicloudapi.models.FileMetadata;
import com.rodrigodias.multicloudapi.models.FileList;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AwsStorageService implements CloudStorageService {

    private static final Logger logger = LoggerFactory.getLogger(AwsStorageService.class);
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    public CompletableFuture<String> uploadFile(String fileName, byte[] fileContent) throws CloudOperationException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Subindo arquivo {} para AWS S3", fileName);
                amazonS3.putObject(bucketName, fileName, new ByteArrayInputStream(fileContent), null);
                String url = amazonS3.getUrl(bucketName, fileName).toString();
                logger.info("Arquivo {} salvo com sucesso em {}", fileName, url);
                return url;
            } catch (Exception e) {
                logger.error("Erro ao subir arquivo para AWS S3: {}", e.getMessage());
                throw new CloudOperationException("Erro ao subir arquivo para AWS S3", e);
            }
        });
    }

    @Override
    public CompletableFuture<byte[]> downloadFile(String fileName) throws CloudOperationException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Baixando arquivo {} do AWS S3", fileName);
                S3Object object = amazonS3.getObject(bucketName, fileName);
                byte[] content = object.getObjectContent().readAllBytes();
                logger.info("Arquivo {} baixado com sucesso", fileName);
                return content;
            } catch (Exception e) {
                logger.error("Erro ao baixar arquivo do AWS S3: {}", e.getMessage());
                throw new CloudOperationException("Erro ao baixar arquivo do AWS S3", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteFile(String fileName) throws CloudOperationException {
        return CompletableFuture.runAsync(() -> {
            try {
                logger.info("Deletando arquivo {} do AWS S3", fileName);
                amazonS3.deleteObject(bucketName, fileName);
                logger.info("Arquivo {} deletado com sucesso", fileName);
            } catch (Exception e) {
                logger.error("Erro ao deletar arquivo do AWS S3: {}", e.getMessage());
                throw new CloudOperationException("Erro ao deletar arquivo do AWS S3", e);
            }
        });
    }

    @Override
    public CompletableFuture<FileList> listFiles(int page, int size) throws CloudOperationException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Listando arquivos do AWS S3 no bucket {}, página {}, tamanho {}", bucketName, page, size);
                var result = amazonS3.listObjects(bucketName);
                var summaries = result.getObjectSummaries();
                int start = page * size;
                int end = Math.min(start + size, summaries.size());
                List<FileMetadata> files = summaries.subList(start, end).stream()
                        .map(summary -> new FileMetadata(
                                summary.getKey(),
                                "AWS",
                                amazonS3.getUrl(bucketName, summary.getKey()).toString(),
                                summary.getSize(),
                                summary.getLastModified().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime(),
                                "application/octet-stream"
                        ))
                        .collect(Collectors.toList());
                long totalSize = files.stream().mapToLong(FileMetadata::getSize).sum();
                FileList fileList = new FileList("AWS", files, totalSize, files.size());
                logger.info("Listagem concluída: {} arquivos retornados (página {})", files.size(), page);
                return fileList;
            } catch (Exception e) {
                logger.error("Erro ao listar arquivos do AWS S3: {}", e.getMessage());
                throw new CloudOperationException("Erro ao listar arquivos do AWS S3", e);
            }
        });
    }

    @Override
    public String getProviderName() {
        return "AWS";
    }
}