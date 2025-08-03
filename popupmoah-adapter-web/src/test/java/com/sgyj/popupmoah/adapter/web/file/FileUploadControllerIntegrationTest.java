package com.sgyj.popupmoah.adapter.web.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgyj.popupmoah.application.file.FileUploadApplicationService;
import com.sgyj.popupmoah.domain.file.entity.FileUpload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * FileUploadController 통합 테스트
 */
@WebMvcTest(FileUploadController.class)
class FileUploadControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileUploadApplicationService fileUploadApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void uploadFile_성공() throws Exception {
        // given
        when(fileUploadApplicationService.uploadFile(
                any(MockMultipartFile.class),
                eq(FileUpload.UploadType.IMAGE),
                eq(FileUpload.ReferenceType.POPUP_STORE),
                eq(1L)
        )).thenReturn(testFileUpload);

        // when & then
        mockMvc.perform(multipart("/api/v1/files/upload")
                        .file(testMultipartFile)
                        .param("uploadType", "IMAGE")
                        .param("referenceType", "POPUP_STORE")
                        .param("referenceId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.originalFileName").value("test-image.jpg"))
                .andExpect(jsonPath("$.uploadType").value("IMAGE"))
                .andExpect(jsonPath("$.referenceType").value("POPUP_STORE"))
                .andExpect(jsonPath("$.referenceId").value(1))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void uploadImage_성공() throws Exception {
        // given
        when(fileUploadApplicationService.uploadAndOptimizeImage(
                any(MockMultipartFile.class),
                eq(FileUpload.ReferenceType.POPUP_STORE),
                eq(1L)
        )).thenReturn(testFileUpload);

        // when & then
        mockMvc.perform(multipart("/api/v1/files/upload/image")
                        .file(testMultipartFile)
                        .param("referenceType", "POPUP_STORE")
                        .param("referenceId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.thumbnailUrl").value("https://s3.amazonaws.com/bucket/thumbnails/test-image-123.jpg"))
                .andExpect(jsonPath("$.optimizedUrl").value("https://s3.amazonaws.com/bucket/optimized/test-image-123.jpg"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getFile_성공() throws Exception {
        // given
        when(fileUploadApplicationService.getFileById(1L)).thenReturn(Optional.of(testFileUpload));

        // when & then
        mockMvc.perform(get("/api/v1/files/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.originalFileName").value("test-image.jpg"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getFile_파일없음() throws Exception {
        // given
        when(fileUploadApplicationService.getFileById(999L)).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(get("/api/v1/files/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFilesByReference_성공() throws Exception {
        // given
        List<FileUpload> files = List.of(testFileUpload);
        when(fileUploadApplicationService.getActiveFilesByReference(
                eq(FileUpload.ReferenceType.POPUP_STORE),
                eq(1L)
        )).thenReturn(files);

        // when & then
        mockMvc.perform(get("/api/v1/files/reference/POPUP_STORE/1")
                        .param("activeOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].originalFileName").value("test-image.jpg"));
    }

    @Test
    void generateDownloadUrl_성공() throws Exception {
        // given
        String downloadUrl = "https://s3.amazonaws.com/bucket/download/test-image-123.jpg";
        when(fileUploadApplicationService.getFileById(1L)).thenReturn(Optional.of(testFileUpload));
        when(fileUploadApplicationService.generateDownloadUrl(
                eq(testFileUpload.getFilePath()),
                eq(testFileUpload.getOriginalFileName())
        )).thenReturn(downloadUrl);

        // when & then
        mockMvc.perform(get("/api/v1/files/1/download")
                        .param("useOriginalName", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileId").value(1))
                .andExpect(jsonPath("$.fileName").value("test-image.jpg"))
                .andExpect(jsonPath("$.downloadUrl").value(downloadUrl));
    }

    @Test
    void deleteFile_성공() throws Exception {
        // given
        // when & then
        mockMvc.perform(delete("/api/v1/files/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deactivateFile_성공() throws Exception {
        // given
        // when & then
        mockMvc.perform(patch("/api/v1/files/1/deactivate"))
                .andExpect(status().isNoContent());
    }

    @Test
    void uploadFile_파일없음_실패() throws Exception {
        // when & then
        mockMvc.perform(multipart("/api/v1/files/upload")
                        .param("uploadType", "IMAGE")
                        .param("referenceType", "POPUP_STORE")
                        .param("referenceId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadFile_잘못된파라미터_실패() throws Exception {
        // when & then
        mockMvc.perform(multipart("/api/v1/files/upload")
                        .file(testMultipartFile)
                        .param("uploadType", "INVALID_TYPE")
                        .param("referenceType", "POPUP_STORE")
                        .param("referenceId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadFile_IOException_실패() throws Exception {
        // given
        when(fileUploadApplicationService.uploadFile(
                any(MockMultipartFile.class),
                eq(FileUpload.UploadType.IMAGE),
                eq(FileUpload.ReferenceType.POPUP_STORE),
                eq(1L)
        )).thenThrow(new IOException("Upload failed"));

        // when & then
        mockMvc.perform(multipart("/api/v1/files/upload")
                        .file(testMultipartFile)
                        .param("uploadType", "IMAGE")
                        .param("referenceType", "POPUP_STORE")
                        .param("referenceId", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("파일 업로드에 실패했습니다."))
                .andExpect(jsonPath("$.success").value(false));
    }
} 