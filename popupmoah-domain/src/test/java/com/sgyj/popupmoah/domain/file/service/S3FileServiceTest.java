package com.sgyj.popupmoah.domain.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * S3FileService 테스트
 */
@ExtendWith(MockitoExtension.class)
class S3FileServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3FileService s3FileService;

    private static final String BUCKET_NAME = "test-bucket";
    private static final String REGION = "ap-northeast-2";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3FileService, "bucketName", BUCKET_NAME);
        ReflectionTestUtils.setField(s3FileService, "region", REGION);
    }

    @Test
    void uploadFile_성공() throws IOException {
        // given
        String filePath = "uploads/test.jpg";
        String contentType = "image/jpeg";
        byte[] fileData = "test file content".getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);

        // when
        s3FileService.uploadFile(inputStream, filePath, contentType);

        // then
        verify(amazonS3).putObject(any(PutObjectRequest.class));
    }

    @Test
    void uploadFile_바이트배열_성공() {
        // given
        String filePath = "uploads/test.jpg";
        String contentType = "image/jpeg";
        byte[] fileData = "test file content".getBytes();

        // when
        s3FileService.uploadFile(fileData, filePath, contentType);

        // then
        verify(amazonS3).putObject(any(PutObjectRequest.class));
    }

    @Test
    void uploadFile_실패시_예외발생() {
        // given
        String filePath = "uploads/test.jpg";
        String contentType = "image/jpeg";
        byte[] fileData = "test file content".getBytes();

        doThrow(new RuntimeException("S3 upload failed"))
                .when(amazonS3).putObject(any(PutObjectRequest.class));

        // when & then
        assertThatThrownBy(() -> 
            s3FileService.uploadFile(fileData, filePath, contentType)
        ).isInstanceOf(RuntimeException.class)
         .hasMessageContaining("파일 업로드에 실패했습니다");
    }

    @Test
    void downloadFile_성공() throws IOException {
        // given
        String filePath = "uploads/test.jpg";
        byte[] expectedData = "test file content".getBytes();
        
        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream inputStream = new S3ObjectInputStream(
                new ByteArrayInputStream(expectedData), null);
        
        when(amazonS3.getObject(BUCKET_NAME, filePath)).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(inputStream);

        // when
        byte[] result = s3FileService.downloadFile(filePath);

        // then
        assertThat(result).isEqualTo(expectedData);
        verify(amazonS3).getObject(BUCKET_NAME, filePath);
    }

    @Test
    void downloadFile_실패시_예외발생() {
        // given
        String filePath = "uploads/test.jpg";
        
        doThrow(new RuntimeException("S3 download failed"))
                .when(amazonS3).getObject(BUCKET_NAME, filePath);

        // when & then
        assertThatThrownBy(() -> 
            s3FileService.downloadFile(filePath)
        ).isInstanceOf(RuntimeException.class)
         .hasMessageContaining("파일 다운로드에 실패했습니다");
    }

    @Test
    void deleteFile_성공() {
        // given
        String filePath = "uploads/test.jpg";

        // when
        s3FileService.deleteFile(filePath);

        // then
        verify(amazonS3).deleteObject(BUCKET_NAME, filePath);
    }

    @Test
    void deleteFile_실패시_예외발생() {
        // given
        String filePath = "uploads/test.jpg";
        
        doThrow(new RuntimeException("S3 delete failed"))
                .when(amazonS3).deleteObject(BUCKET_NAME, filePath);

        // when & then
        assertThatThrownBy(() -> 
            s3FileService.deleteFile(filePath)
        ).isInstanceOf(RuntimeException.class)
         .hasMessageContaining("파일 삭제에 실패했습니다");
    }

    @Test
    void deleteFiles_성공() {
        // given
        String[] filePaths = {"uploads/test1.jpg", "uploads/test2.jpg"};

        // when
        s3FileService.deleteFiles(filePaths);

        // then
        verify(amazonS3).deleteObjects(any(DeleteObjectsRequest.class));
    }

    @Test
    void generateFileUrl_성공() {
        // given
        String filePath = "uploads/test.jpg";
        String expectedUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", BUCKET_NAME, REGION, filePath);

        // when
        String result = s3FileService.generateFileUrl(filePath);

        // then
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    void generateDownloadUrl_성공() {
        // given
        String filePath = "uploads/test.jpg";
        String fileName = "test.jpg";
        String expectedUrl = "https://s3.amazonaws.com/bucket/download/test.jpg";
        
        when(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenReturn(new URL(expectedUrl));

        // when
        String result = s3FileService.generateDownloadUrl(filePath, fileName);

        // then
        assertThat(result).isEqualTo(expectedUrl);
        verify(amazonS3).generatePresignedUrl(any(GeneratePresignedUrlRequest.class));
    }

    @Test
    void generateDownloadUrl_실패시_예외발생() {
        // given
        String filePath = "uploads/test.jpg";
        String fileName = "test.jpg";
        
        doThrow(new RuntimeException("URL generation failed"))
                .when(amazonS3).generatePresignedUrl(any(GeneratePresignedUrlRequest.class));

        // when & then
        assertThatThrownBy(() -> 
            s3FileService.generateDownloadUrl(filePath, fileName)
        ).isInstanceOf(RuntimeException.class)
         .hasMessageContaining("다운로드 URL 생성에 실패했습니다");
    }

    @Test
    void fileExists_존재하는파일() {
        // given
        String filePath = "uploads/test.jpg";
        when(amazonS3.doesObjectExist(BUCKET_NAME, filePath)).thenReturn(true);

        // when
        boolean result = s3FileService.fileExists(filePath);

        // then
        assertThat(result).isTrue();
        verify(amazonS3).doesObjectExist(BUCKET_NAME, filePath);
    }

    @Test
    void fileExists_존재하지않는파일() {
        // given
        String filePath = "uploads/test.jpg";
        when(amazonS3.doesObjectExist(BUCKET_NAME, filePath)).thenReturn(false);

        // when
        boolean result = s3FileService.fileExists(filePath);

        // then
        assertThat(result).isFalse();
        verify(amazonS3).doesObjectExist(BUCKET_NAME, filePath);
    }

    @Test
    void copyFile_성공() {
        // given
        String sourcePath = "uploads/source.jpg";
        String destinationPath = "uploads/destination.jpg";

        // when
        s3FileService.copyFile(sourcePath, destinationPath);

        // then
        verify(amazonS3).copyObject(BUCKET_NAME, sourcePath, BUCKET_NAME, destinationPath);
    }

    @Test
    void copyFile_실패시_예외발생() {
        // given
        String sourcePath = "uploads/source.jpg";
        String destinationPath = "uploads/destination.jpg";
        
        doThrow(new RuntimeException("S3 copy failed"))
                .when(amazonS3).copyObject(BUCKET_NAME, sourcePath, BUCKET_NAME, destinationPath);

        // when & then
        assertThatThrownBy(() -> 
            s3FileService.copyFile(sourcePath, destinationPath)
        ).isInstanceOf(RuntimeException.class)
         .hasMessageContaining("파일 복사에 실패했습니다");
    }
} 