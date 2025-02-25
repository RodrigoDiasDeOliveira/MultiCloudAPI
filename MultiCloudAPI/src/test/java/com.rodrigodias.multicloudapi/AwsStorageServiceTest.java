package com.rodrigodias.multicloudapi.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AwsStorageServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private AwsStorageService awsStorageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        awsStorageService = new AwsStorageService(amazonS3);
        try {
            var field = AwsStorageService.class.getDeclaredField("bucketName");
            field.setAccessible(true);
            field.set(awsStorageService, "test-bucket");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testUploadFile() throws Exception {
        byte[] content = "teste".getBytes();
        when(amazonS3.putObject(anyString(), anyString(), any(ByteArrayInputStream.class), any())).thenReturn(null);
        when(amazonS3.getUrl("test-bucket", "test.txt")).thenReturn(new java.net.URL("https://s3.amazonaws.com/test-bucket/test.txt"));

        String url = awsStorageService.uploadFile("test.txt", content).join();
        assertEquals("https://s3.amazonaws.com/test-bucket/test.txt", url);
    }

    @Test
    void testDownloadFile() throws Exception {
        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream inputStream = new S3ObjectInputStream(new ByteArrayInputStream("teste".getBytes()), null);
        when(amazonS3.getObject("test-bucket", "test.txt")).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(inputStream);

        byte[] content = awsStorageService.downloadFile("test.txt").join();
        assertEquals("teste", new String(content));
    }

    @Test
    void testDeleteFile() throws Exception {
        doNothing().when(amazonS3).deleteObject("test-bucket", "test.txt");
        awsStorageService.deleteFile("test.txt").join();
        verify(amazonS3, times(1)).deleteObject("test-bucket", "test.txt");
    }
}