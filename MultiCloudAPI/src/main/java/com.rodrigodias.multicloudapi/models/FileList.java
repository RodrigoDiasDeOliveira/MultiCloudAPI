package com.rodrigodias.multicloudapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileList {
    private String provider;             // Provedor da listagem (AWS, Azure, OCI, GCP)
    private List<FileMetadata> files;    // Lista de arquivos com metadados
    private long totalSize;              // Tamanho total dos arquivos em bytes
    private int fileCount;               // Número total de arquivos
}