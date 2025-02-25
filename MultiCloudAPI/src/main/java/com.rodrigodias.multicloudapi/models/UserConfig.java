package com.rodrigodias.multicloudapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserConfig {
    private String userId;                        // Identificador único do usuário
    private String defaultProvider;               // Provedor padrão (ex.: "AWS")
    private Map<String, String> providerConfigs;  // Configurações específicas por provedor (ex.: bucket customizado)
    private long maxStorageLimit;                 // Limite máximo de armazenamento em bytes
}