package com.rodrigodias.multicloudapi.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class LogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String operation;       // Ex.: "upload", "download", "delete", "list"
    private String provider;        // Ex.: "AWS", "Azure"
    private String fileName;        // Nome do arquivo envolvido (se aplicável)
    private String status;          // "success" ou "error"
    private String message;         // Detalhes da operação
    private LocalDateTime timestamp;// Data/hora da operação

    public LogEntry(String operation, String provider, String fileName, String status, String message) {
        this.operation = operation;
        this.provider = provider;
        this.fileName = fileName;
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}