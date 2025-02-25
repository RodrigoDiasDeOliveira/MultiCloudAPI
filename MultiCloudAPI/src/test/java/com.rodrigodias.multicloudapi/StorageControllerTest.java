package com.rodrigodias.multicloudapi.controllers;

import com.rodrigodias.multicloudapi.models.CloudResponse;
import com.rodrigodias.multicloudapi.models.FileList;
import com.rodrigodias.multicloudapi.models.FileMetadata;
import com.rodrigodias.multicloudapi.services.CloudStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StorageControllerTest {

    @Mock
    private CloudStorageService awsService;

    @InjectMocks
    private StorageController storageController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(awsService.getProviderName()).thenReturn("AWS");
        storageController = new StorageController(Arrays.asList(awsService));
        mockMvc = MockMvcBuilders.standaloneSetup(storageController).build();
    }

    @Test
    void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "teste".getBytes());
        when(awsService.uploadFile("test.txt", "teste".getBytes()))
                .thenReturn(CompletableFuture.completedFuture("https://s3.amazonaws.com/test-bucket/test.txt"));

        mockMvc.perform(multipart("/api/storage/upload")
                .file(file)
                .param("provider", "AWS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Arquivo salvo em AWS"))
                .andExpect(jsonPath("$.data.fileName").value("test.txt"));
    }

    @Test
    void testDownloadFile() throws Exception {
        when(awsService.downloadFile("test.txt"))
                .thenReturn(CompletableFuture.completedFuture("teste".getBytes()));

        mockMvc.perform(get("/api/storage/download")
                .param("fileName", "test.txt")
                .param("provider", "AWS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Arquivo baixado de AWS"));
    }

    @Test
    void testDeleteFile() throws Exception {
        when(awsService.deleteFile("test.txt"))
                .thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(delete("/api/storage/delete")
                .param("fileName", "test.txt")
                .param("provider", "AWS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Arquivo test.txt deletado de AWS"));
    }

    @Test
    void testListFilesWithPagination() throws Exception {
        FileMetadata file = new FileMetadata("test.txt", "AWS", "https://s3.amazonaws.com/test-bucket/test.txt",
                5L, LocalDateTime.now(), "text/plain");
        FileList fileList = new FileList("AWS", Collections.singletonList(file), 5L, 1);
        when(awsService.listFiles(0, 10))
                .thenReturn(CompletableFuture.completedFuture(fileList));

        mockMvc.perform(get("/api/storage/list")
                .param("provider", "AWS")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Lista de arquivos de AWS (página 0)"))
                .andExpect(jsonPath("$.data.provider").value("AWS"))
                .andExpect(jsonPath("$.data.fileCount").value(1))
                .andExpect(jsonPath("$.data.files[0].fileName").value("test.txt"));
    }
}