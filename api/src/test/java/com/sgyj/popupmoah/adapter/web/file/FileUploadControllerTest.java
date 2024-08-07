package com.sgyj.popupmoah.adapter.web.file;

import com.sgyj.popupmoah.application.file.FileUploadApplicationService;
import com.sgyj.popupmoah.domain.file.entity.FileUpload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * 파일 업로드 컨트롤러 테스트
 */
@ExtendWith(MockitoExtension.class)
class FileUploadControllerTest {

    @Mock
    private FileUploadApplicationService fileUploadApplicationService;

    @InjectMocks
    private FileUploadController fileUploadController;

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
        when(fileUploadApplicationService.uploadFile(
                any(MockMultipartFile.class),
                eq(FileUpload.UploadType.IMAGE),
                eq(FileUpload.ReferenceType.POPUP_STORE),
                eq(1L)
        )).thenReturn(testFileUpload);

        // when
        ResponseEntity<FileUploadResponse> response = fileUploadController.uploadFile(
                testMultipartFile,
                FileUpload.UploadType.IMAGE,
                FileUpload.ReferenceType.POPUP_STORE,
                1L
        );

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getOriginalFileName()).isEqualTo("test-image.jpg");
        assertThat(response.getBody().getSuccess()).isTrue();
    }

    @Test
    void uploadImage_성공() throws IOException {
        // given
        when(fileUploadApplicationService.uploadAndOptimizeImage(
                any(MockMultipartFile.class),
                eq(FileUpload.ReferenceType.POPUP_STORE),
                eq(1L)
        )).thenReturn(testFileUpload);

        // when
        ResponseEntity<FileUploadResponse> response = fileUploadController.uploadImage(
                testMultipartFile,
                FileUpload.ReferenceType.POPUP_STORE,
                1L
        );

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getThumbnailUrl()).isNotNull();
        assertThat(response.getBody().getOptimizedUrl()).isNotNull();
    }

    @Test
    void getFile_성공() {
        // given
        when(fileUploadApplicationService.getFileById(1L)).thenReturn(Optional.of(testFileUpload));

        // when
        ResponseEntity<FileUploadResponse> response = fileUploadController.getFile(1L);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void getFile_파일없음() {
        // given
        when(fileUploadApplicationService.getFileById(999L)).thenReturn(Optional.empty());

        // when
        ResponseEntity<FileUploadResponse> response = fileUploadController.getFile(999L);

        // then
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void getFilesByReference_성공() {
        // given
        List<FileUpload> files = List.of(testFileUpload);
        when(fileUploadApplicationService.getActiveFilesByReference(
                eq(FileUpload.ReferenceType.POPUP_STORE),
                eq(1L)
        )).thenReturn(files);

        // when
        ResponseEntity<List<FileUploadResponse>> response = fileUploadController.getFilesByReference(
                FileUpload.ReferenceType.POPUP_STORE,
                1L,
                true
        );

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void generateDownloadUrl_성공() {
        // given
        String downloadUrl = "https://s3.amazonaws.com/bucket/download/test-image-123.jpg";
        when(fileUploadApplicationService.getFileById(1L)).thenReturn(Optional.of(testFileUpload));
        when(fileUploadApplicationService.generateDownloadUrl(
                eq(testFileUpload.getFilePath()),
                eq(testFileUpload.getOriginalFileName())
        )).thenReturn(downloadUrl);

        // when
        ResponseEntity<FileDownloadResponse> response = fileUploadController.generateDownloadUrl(1L, true);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFileId()).isEqualTo(1L);
        assertThat(response.getBody().getFileName()).isEqualTo("test-image.jpg");
        assertThat(response.getBody().getDownloadUrl()).isEqualTo(downloadUrl);
    }

    @Test
    void deleteFile_성공() {
        // given
        // when
        ResponseEntity<Void> response = fileUploadController.deleteFile(1L);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void deactivateFile_성공() {
        // given
        // when
        ResponseEntity<Void> response = fileUploadController.deactivateFile(1L);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }
} 