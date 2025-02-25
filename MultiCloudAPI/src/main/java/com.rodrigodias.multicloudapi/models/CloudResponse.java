package com.rodrigodias.multicloudapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudResponse<T> {
    private String status;     // "success" ou "error"
    private String message;    // Mensagem descritiva (ex.: "Arquivo salvo com sucesso")
    private T data;            // Dados da operação (ex.: FileMetadata ou byte[])
}