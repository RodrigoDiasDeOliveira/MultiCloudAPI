package com.rodrigodias.multicloudapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
    private String fileName;         // Nome do arquivo
    private String provider;         // Provedor (AWS, Azure, OCI, GCP)
    private String url;              // URL de acesso ao arquivo
    private long size;               // Tamanho em bytes
    private LocalDateTime uploadTime; // Data/hora do upload
    private String contentType;      // Tipo MIME (ex.: "text/plain")
}