package com.rodrigodias.multicloudapi.services;

import com.rodrigodias.multicloudapi.models.LogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogEntry, Long> {
    Page<LogEntry> findAll(Pageable pageable);
}