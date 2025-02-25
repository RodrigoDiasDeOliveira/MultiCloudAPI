package com.rodrigodias.multicloudapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {
    private String errorCode;     // Código do erro (ex.: "FILE_NOT_FOUND")
    private String description;   // Descrição (ex.: "Arquivo não encontrado no provedor")
    private String timestamp;     // Data/hora do erro
}