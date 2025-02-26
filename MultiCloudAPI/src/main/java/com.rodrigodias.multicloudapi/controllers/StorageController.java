package com.rodrigodias.multicloudapi.controllers;

import com.rodrigodias.multicloudapi.models.CloudResponse;
import com.rodrigodias.multicloudapi.models.FileList;
import com.rodrigodias.multicloudapi.models.FileMetadata;
import com.rodrigodias.multicloudapi.models.UserConfig;
import com.rodrigodias.multicloudapi.services.CloudStorageService;
import com.rodrigodias.multicloudapi.services.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController {

    private static final Logger logger = LoggerFactory.getLogger(StorageController.class);
    private final List<CloudStorageService> storageServices;
    private final LogService logService;

    private final UserConfig userConfig = new UserConfig(
            "user123", "AWS", Map.of("AWS", "custom-bucket", "Azure", "custom-container"), 10_000_000L
    );

    @PostMapping("/upload")
    @Operation(summary = "Upload de arquivo para um provedor de nuvem", responses = {
        @ApiResponse(responseCode = "200", description = "Arquivo salvo com sucesso"),
        @ApiResponse(responseCode = "400", description = "Provedor inválido")
    })
    public CompletableFuture<ResponseEntity<CloudResponse<FileMetadata>>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "provider", defaultValue = "") String provider) throws Exception {
        String effectiveProvider = provider.isEmpty() ? userConfig.getDefaultProvider() : provider;
        logger.info("Requisição para upload: arquivo={}, provedor={}", file.getOriginalFilename(), effectiveProvider);
        CloudStorageService service = getService(effectiveProvider);
        return service.uploadFile(file.getOriginalFilename(), file.getBytes())
                .thenApply(url -> {
                    logger.info("Upload concluído: {}", url);
                    logService.saveLog("upload", effectiveProvider, file.getOriginalFilename(), "success",
                            "Arquivo salvo em " + url);
                    FileMetadata metadata = new FileMetadata(
                            file.getOriginalFilename(), effectiveProvider, url, file.getSize(),
                            LocalDateTime.now(), file.getContentType());
                    CloudResponse<FileMetadata> response = new CloudResponse<>("success",
                            "Arquivo salvo em " + effectiveProvider, metadata);
                    return ResponseEntity.ok(response);
                }).exceptionally(throwable -> {
                    logService.saveLog("upload", effectiveProvider, file.getOriginalFilename(), "error",
                            "Erro: " + throwable.getMessage());
                    return ResponseEntity.badRequest().body(new CloudResponse<>("error", throwable.getMessage(), null));
                });
    }

    @GetMapping("/download")
    @Operation(summary = "Download de arquivo de um provedor de nuvem", responses = {
        @ApiResponse(responseCode = "200", description = "Arquivo baixado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Provedor inválido")
    })
    public CompletableFuture<ResponseEntity<CloudResponse<byte[]>>> downloadFile(
            @RequestParam("fileName") String fileName,
            @RequestParam("provider") String provider) throws Exception {
        logger.info("Requisição para download: arquivo={}, provedor={}", fileName, provider);
        CloudStorageService service = getService(provider);
        return service.downloadFile(fileName)
                .thenApply(bytes -> {
                    logger.info("Download concluído: arquivo={}", fileName);
                    logService.saveLog("download", provider, fileName, "success", "Arquivo baixado com sucesso");
                    CloudResponse<byte[]> response = new CloudResponse<>("success",
                            "Arquivo baixado de " + provider, bytes);
                    return ResponseEntity.ok(response);
                }).exceptionally(throwable -> {
                    logService.saveLog("download", provider, fileName, "error", "Erro: " + throwable.getMessage());
                    return ResponseEntity.badRequest().body(new CloudResponse<>("error", throwable.getMessage(), null));
                });
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Exclusão de arquivo de um provedor de nuvem", responses = {
        @ApiResponse(responseCode = "200", description = "Arquivo deletado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Provedor inválido")
    })
    public CompletableFuture<ResponseEntity<CloudResponse<String>>> deleteFile(
            @RequestParam("fileName") String fileName,
            @RequestParam("provider") String provider) throws Exception {
        logger.info("Requisição para exclusão: arquivo={}, provedor={}", fileName, provider);
        CloudStorageService service = getService(provider);
        return service.deleteFile(fileName)
                .thenApply(v -> {
                    logger.info("Exclusão concluída: arquivo={}", fileName);
                    logService.saveLog("delete", provider, fileName, "success", "Arquivo deletado com sucesso");
                    CloudResponse<String> response = new CloudResponse<>("success",
                            "Arquivo " + fileName + " deletado de " + provider, null);
                    return ResponseEntity.ok(response);
                }).exceptionally(throwable -> {
                    logService.saveLog("delete", provider, fileName, "error", "Erro: " + throwable.getMessage());
                    return ResponseEntity.badRequest().body(new CloudResponse<>("error", throwable.getMessage(), null));
                });
    }

    @GetMapping("/list")
    @Operation(summary = "Listagem de arquivos de um provedor de nuvem com paginação", responses = {
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Provedor inválido")
    })
    public CompletableFuture<ResponseEntity<CloudResponse<FileList>>> listFiles(
            @RequestParam("provider") String provider,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) throws Exception {
        logger.info("Requisição para listagem: provedor={}, página={}, tamanho={}", provider, page, size);
        CloudStorageService service = getService(provider);
        return service.listFiles(page, size)
                .thenApply(fileList -> {
                    logger.info("Listagem concluída: {} arquivos encontrados (página {})", fileList.getFileCount(), page);
                    logService.saveLog("list", provider, null, "success", "Listagem concluída: " + fileList.getFileCount() + " arquivos");
                    CloudResponse<FileList> response = new CloudResponse<>("success",
                            "Lista de arquivos de " + provider + " (página " + page + ")", fileList);
                    return ResponseEntity.ok(response);
                }).exceptionally(throwable -> {
                    logService.saveLog("list", provider, null, "error", "Erro: " + throwable.getMessage());
                    return ResponseEntity.badRequest().body(new CloudResponse<>("error", throwable.getMessage(), null));
                });
    }

    @GetMapping("/logs")
    @Operation(summary = "Listagem de logs de operações com paginação", responses = {
        @ApiResponse(responseCode = "200", description = "Logs retornados com sucesso")
    })
    public ResponseEntity<Page<LogEntry>> getLogs(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        logger.info("Requisição para listar logs: página={}, tamanho={}", page, size);
        Page<LogEntry> logs = logService.getLogs(page, size);
        return ResponseEntity.ok(logs);
    }

    private CloudStorageService getService(String provider) {
        return storageServices.stream()
                .filter(s -> s.getProviderName().equalsIgnoreCase(provider))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Provedor não suportado: " + provider));
    }
}