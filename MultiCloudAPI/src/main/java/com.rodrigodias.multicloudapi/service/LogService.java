package com.rodrigodias.multicloudapi.services;

import com.rodrigodias.multicloudapi.models.LogEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    public void saveLog(String operation, String provider, String fileName, String status, String message) {
        LogEntry log = new LogEntry(operation, provider, fileName, status, message);
        logRepository.save(log);
    }

    public Page<LogEntry> getLogs(int page, int size) {
        return logRepository.findAll(PageRequest.of(page, size));
    }
}