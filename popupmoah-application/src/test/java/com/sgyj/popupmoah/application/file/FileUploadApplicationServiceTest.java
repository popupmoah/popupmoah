package com.sgyj.popupmoah.application.file;

import com.sgyj.popupmoah.domain.file.entity.FileUpload;
import com.sgyj.popupmoah.domain.file.port.FileUploadServicePort;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * FileUploadApplicationService 테스트
 */
@ExtendWith(MockitoExtension.class)
class FileUploadApplicationServiceTest {

    @Mock
    private FileUploadServicePort fileUploadServicePort;

    @InjectMocks
    private FileUploadApplicationService fileUploadApplicationService;

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
        when(fileUploadServicePort.uploadFile(
                any(MockMultipartFile.class),
                eq(FileUpload.UploadType.IMAGE),
                eq(FileUpload.ReferenceType.POPUP_STORE),
                eq(1L)
        )).thenReturn(testFileUpload);

        // when
        FileUpload result = fileUploadApplicationService.uploadFile(
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

        verify(fileUploadServicePort).uploadFile(
                testMultipartFile,
                FileUpload.UploadType.IMAGE,
                FileUpload.ReferenceType.POPUP_STORE,
                1L
        );
    }

    @Test
    void uploadAndOptimizeImage_성공() throws IOException {
        // given
        when(fileUploadServicePort.uploadAndOptimizeImage(
                any(MockMultipartFile.class),
                eq(FileUpload.ReferenceType.POPUP_STORE),
                eq(1L)
        )).thenReturn(testFileUpload);

        // when
        FileUpload result = fileUploadApplicationService.uploadAndOptimizeImage(
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

        verify(fileUploadServicePort).uploadAndOptimizeImage(
                testMultipartFile,
                FileUpload.ReferenceType.POPUP_STORE,
                1L
        );
    }

    @Test
    void getFileById_성공() {
        // given
        when(fileUploadServicePort.getFileById(1L)).thenReturn(Optional.of(testFileUpload));

        // when
        Optional<FileUpload> result = fileUploadApplicationService.getFileById(1L);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getOriginalFileName()).isEqualTo("test-image.jpg");

        verify(fileUploadServicePort).getFileById(1L);
    }

    @Test
    void getFileById_파일없음() {
        // given
        when(fileUploadServicePort.getFileById(999L)).thenReturn(Optional.empty());

        // when
        Optional<FileUpload> result = fileUploadApplicationService.getFileById(999L);

        // then
        assertThat(result).isEmpty();

        verify(fileUploadServicePort).getFileById(999L);
    }

    @Test
    void getFilesByReference_성공() {
        // given
        List<FileUpload> files = List.of(testFileUpload);
        when(fileUploadServicePort.getFilesByReference(
                eq(FileUpload.ReferenceType.POPUP_STORE),
                eq(1L)
        )).thenReturn(files);

        // when
        List<FileUpload> result = fileUploadApplicationService.getFilesByReference(
                FileUpload.ReferenceType.POPUP_STORE,
                1L
        );

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);

        verify(fileUploadServicePort).getFilesByReference(
                FileUpload.ReferenceType.POPUP_STORE,
                1L
        );
    }

    @Test
    void getActiveFilesByReference_성공() {
        // given
        List<FileUpload> files = List.of(testFileUpload);
        when(fileUploadServicePort.getActiveFilesByReference(
                eq(FileUpload.ReferenceType.POPUP_STORE),
                eq(1L)
        )).thenReturn(files);

        // when
        List<FileUpload> result = fileUploadApplicationService.getActiveFilesByReference(
                FileUpload.ReferenceType.POPUP_STORE,
                1L
        );

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getActive()).isTrue();

        verify(fileUploadServicePort).getActiveFilesByReference(
                FileUpload.ReferenceType.POPUP_STORE,
                1L
        );
    }

    @Test
    void deleteFile_성공() {
        // given
        // when
        fileUploadApplicationService.deleteFile(1L);

        // then
        verify(fileUploadServicePort).deleteFile(1L);
    }

    @Test
    void deactivateFile_성공() {
        // given
        // when
        fileUploadApplicationService.deactivateFile(1L);

        // then
        verify(fileUploadServicePort).deactivateFile(1L);
    }

    @Test
    void generateFileUrl_성공() {
        // given
        String filePath = "uploads/images/test.jpg";
        String expectedUrl = "https://s3.amazonaws.com/bucket/uploads/images/test.jpg";
        when(fileUploadServicePort.generateFileUrl(filePath)).thenReturn(expectedUrl);

        // when
        String result = fileUploadApplicationService.generateFileUrl(filePath);

        // then
        assertThat(result).isEqualTo(expectedUrl);
        verify(fileUploadServicePort).generateFileUrl(filePath);
    }

    @Test
    void generateDownloadUrl_성공() {
        // given
        String filePath = "uploads/images/test.jpg";
        String fileName = "test.jpg";
        String expectedUrl = "https://s3.amazonaws.com/bucket/download/test.jpg";
        when(fileUploadServicePort.generateDownloadUrl(filePath, fileName)).thenReturn(expectedUrl);

        // when
        String result = fileUploadApplicationService.generateDownloadUrl(filePath, fileName);

        // then
        assertThat(result).isEqualTo(expectedUrl);
        verify(fileUploadServicePort).generateDownloadUrl(filePath, fileName);
    }

    @Test
    void resizeImage_성공() throws IOException {
        // given
        byte[] imageData = "test image content".getBytes();
        byte[] resizedData = "resized content".getBytes();
        when(fileUploadServicePort.resizeImage(imageData, 300, 200)).thenReturn(resizedData);

        // when
        byte[] result = fileUploadApplicationService.resizeImage(imageData, 300, 200);

        // then
        assertThat(result).isEqualTo(resizedData);
        verify(fileUploadServicePort).resizeImage(imageData, 300, 200);
    }

    @Test
    void generateThumbnail_성공() throws IOException {
        // given
        byte[] imageData = "test image content".getBytes();
        byte[] thumbnailData = "thumbnail content".getBytes();
        when(fileUploadServicePort.generateThumbnail(imageData, 150, 150)).thenReturn(thumbnailData);

        // when
        byte[] result = fileUploadApplicationService.generateThumbnail(imageData, 150, 150);

        // then
        assertThat(result).isEqualTo(thumbnailData);
        verify(fileUploadServicePort).generateThumbnail(imageData, 150, 150);
    }
} 