package com.sgyj.popupmoah.domain.file.service;

import com.sgyj.popupmoah.domain.file.entity.FileUpload;
import com.sgyj.popupmoah.domain.file.port.FileUploadRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * FileUploadDomainService 테스트
 */
@ExtendWith(MockitoExtension.class)
class FileUploadDomainServiceTest {

    @Mock
    private FileUploadRepositoryPort fileUploadRepositoryPort;

    @Mock
    private S3FileService s3FileService;

    @Mock
    private ImageOptimizationService imageOptimizationService;

    @InjectMocks
    private FileUploadDomainService fileUploadDomainService;

    private FileUpload testFileUpload;
    private MockMultipartFile testMultipartFile;

    @BeforeEach
    void setUp() {
        testFileUpload = FileUpload.builder()
                .id(1L)
                .originalFileName("test-image.jpg")
                .storedFileName("test-image-123.jpg")
                .filePath("uploads/images/test-image-123.jpg")
                .fileUrl("https://s3.amazonaws.com/bucket/uploads/images/test-image-123.jpg")
                .contentType("image/jpeg")
                .fileSize(1024L)
                .fileExtension("jpg")
                .active(true)
                .uploadType(FileUpload.UploadType.IMAGE)
                .referenceId(1L)
                .referenceType(FileUpload.ReferenceType.POPUP_STORE)
                .thumbnailUrl("https://s3.amazonaws.com/bucket/thumbnails/test-image-123.jpg")
                .optimizedUrl("https://s3.amazonaws.com/bucket/optimized/test-image-123.jpg")
                .imageWidth(800)
                .imageHeight(600)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testMultipartFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
    }

    @Test
    void uploadFile_성공() throws IOException {
        // given
        when(fileUploadRepositoryPort.save(any(FileUpload.class))).thenReturn(testFileUpload);
        when(s3FileService.generateFileUrl(anyString())).thenReturn(testFileUpload.getFileUrl());

        // when
        FileUpload result = fileUploadDomainService.uploadFile(
                testMultipartFile,
                FileUpload.UploadType.IMAGE,
                FileUpload.ReferenceType.POPUP_STORE,
                1L
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getOriginalFileName()).isEqualTo("test-image.jpg");
        assertThat(result.getUploadType()).isEqualTo(FileUpload.UploadType.IMAGE);
        assertThat(result.getReferenceType()).isEqualTo(FileUpload.ReferenceType.POPUP_STORE);
        assertThat(result.getReferenceId()).isEqualTo(1L);

        verify(s3FileService).uploadFile(any(), anyString(), eq("image/jpeg"));
        verify(fileUploadRepositoryPort).save(any(FileUpload.class));
    }

    @Test
    void uploadAndOptimizeImage_성공() throws IOException {
        // given
        byte[] imageData = "test image content".getBytes();
        byte[] thumbnailData = "thumbnail content".getBytes();
        byte[] optimizedData = "optimized content".getBytes();
        int[] dimensions = {800, 600};

        when(fileUploadRepositoryPort.save(any(FileUpload.class))).thenReturn(testFileUpload);
        when(s3FileService.generateFileUrl(anyString())).thenReturn(testFileUpload.getFileUrl());
        when(imageOptimizationService.getImageDimensions(imageData)).thenReturn(dimensions);
        when(imageOptimizationService.generateThumbnail(imageData, 300, 300)).thenReturn(thumbnailData);
        when(imageOptimizationService.optimizeImage(imageData, 0.8f)).thenReturn(optimizedData);

        // when
        FileUpload result = fileUploadDomainService.uploadAndOptimizeImage(
                testMultipartFile,
                FileUpload.ReferenceType.POPUP_STORE,
                1L
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getImageWidth()).isEqualTo(800);
        assertThat(result.getImageHeight()).isEqualTo(600);
        assertThat(result.getThumbnailUrl()).isNotNull();
        assertThat(result.getOptimizedUrl()).isNotNull();

        verify(s3FileService, times(3)).uploadFile(any(), anyString(), anyString());
        verify(imageOptimizationService).getImageDimensions(imageData);
        verify(imageOptimizationService).generateThumbnail(imageData, 300, 300);
        verify(imageOptimizationService).optimizeImage(imageData, 0.8f);
        verify(fileUploadRepositoryPort, times(2)).save(any(FileUpload.class));
    }

    @Test
    void getFileById_성공() {
        // given
        when(fileUploadRepositoryPort.findById(1L)).thenReturn(Optional.of(testFileUpload));

        // when
        Optional<FileUpload> result = fileUploadDomainService.getFileById(1L);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getOriginalFileName()).isEqualTo("test-image.jpg");
    }

    @Test
    void getFileById_파일없음() {
        // given
        when(fileUploadRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        // when
        Optional<FileUpload> result = fileUploadDomainService.getFileById(999L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void getFilesByReference_성공() {
        // given
        List<FileUpload> files = List.of(testFileUpload);
        when(fileUploadRepositoryPort.findByReferenceTypeAndReferenceId(
                eq(FileUpload.ReferenceType.POPUP_STORE),
                eq(1L)
        )).thenReturn(files);

        // when
        List<FileUpload> result = fileUploadDomainService.getFilesByReference(
                FileUpload.ReferenceType.POPUP_STORE,
                1L
        );

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void getActiveFilesByReference_성공() {
        // given
        List<FileUpload> files = List.of(testFileUpload);
        when(fileUploadRepositoryPort.findActiveByReferenceTypeAndReferenceId(
                eq(FileUpload.ReferenceType.POPUP_STORE),
                eq(1L)
        )).thenReturn(files);

        // when
        List<FileUpload> result = fileUploadDomainService.getActiveFilesByReference(
                FileUpload.ReferenceType.POPUP_STORE,
                1L
        );

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getActive()).isTrue();
    }

    @Test
    void deleteFile_성공() {
        // given
        when(fileUploadRepositoryPort.findById(1L)).thenReturn(Optional.of(testFileUpload));

        // when
        fileUploadDomainService.deleteFile(1L);

        // then
        verify(s3FileService).deleteFile(testFileUpload.getFilePath());
        verify(fileUploadRepositoryPort).deleteById(1L);
    }

    @Test
    void deleteFile_파일없음() {
        // given
        when(fileUploadRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        // when
        fileUploadDomainService.deleteFile(999L);

        // then
        verify(s3FileService, never()).deleteFile(anyString());
        verify(fileUploadRepositoryPort, never()).deleteById(anyLong());
    }

    @Test
    void deactivateFile_성공() {
        // given
        // when
        fileUploadDomainService.deactivateFile(1L);

        // then
        verify(fileUploadRepositoryPort).deactivateById(1L);
    }

    @Test
    void generateFileUrl_성공() {
        // given
        String filePath = "uploads/images/test.jpg";
        String expectedUrl = "https://s3.amazonaws.com/bucket/uploads/images/test.jpg";
        when(s3FileService.generateFileUrl(filePath)).thenReturn(expectedUrl);

        // when
        String result = fileUploadDomainService.generateFileUrl(filePath);

        // then
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    void generateDownloadUrl_성공() {
        // given
        String filePath = "uploads/images/test.jpg";
        String fileName = "test.jpg";
        String expectedUrl = "https://s3.amazonaws.com/bucket/download/test.jpg";
        when(s3FileService.generateDownloadUrl(filePath, fileName)).thenReturn(expectedUrl);

        // when
        String result = fileUploadDomainService.generateDownloadUrl(filePath, fileName);

        // then
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    void resizeImage_성공() throws IOException {
        // given
        byte[] imageData = "test image content".getBytes();
        byte[] resizedData = "resized content".getBytes();
        when(imageOptimizationService.resizeImage(imageData, 300, 200)).thenReturn(resizedData);

        // when
        byte[] result = fileUploadDomainService.resizeImage(imageData, 300, 200);

        // then
        assertThat(result).isEqualTo(resizedData);
        verify(imageOptimizationService).resizeImage(imageData, 300, 200);
    }

    @Test
    void generateThumbnail_성공() throws IOException {
        // given
        byte[] imageData = "test image content".getBytes();
        byte[] thumbnailData = "thumbnail content".getBytes();
        when(imageOptimizationService.generateThumbnail(imageData, 150, 150)).thenReturn(thumbnailData);

        // when
        byte[] result = fileUploadDomainService.generateThumbnail(imageData, 150, 150);

        // then
        assertThat(result).isEqualTo(thumbnailData);
        verify(imageOptimizationService).generateThumbnail(imageData, 150, 150);
    }
} 