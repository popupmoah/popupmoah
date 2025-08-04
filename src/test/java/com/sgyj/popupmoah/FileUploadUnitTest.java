package com.sgyj.popupmoah;

import com.sgyj.popupmoah.domain.file.entity.FileUpload;
import com.sgyj.popupmoah.domain.file.port.FileUploadRepositoryPort;
import com.sgyj.popupmoah.domain.file.port.FileUploadServicePort;
import com.sgyj.popupmoah.domain.file.service.FileUploadDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 파일 업로드 단위 테스트
 * Mock을 사용하여 외부 의존성을 격리합니다.
 */
@ExtendWith(MockitoExtension.class)
class FileUploadUnitTest {

    @Mock
    private FileUploadRepositoryPort fileUploadRepositoryPort;

    @Mock
    private FileUploadServicePort fileUploadServicePort;

    @InjectMocks
    private FileUploadDomainService fileUploadDomainService;

    private FileUpload testFileUpload;

    @BeforeEach
    void setUp() {
        testFileUpload = FileUpload.builder()
                .id(1L)
                .originalFileName("test.jpg")
                .storedFileName("test_123.jpg")
                .filePath("/uploads/test_123.jpg")
                .fileUrl("https://s3.amazonaws.com/bucket/test_123.jpg")
                .contentType("image/jpeg")
                .fileSize(1024L)
                .fileExtension("jpg")
                .uploadType(FileUpload.UploadType.IMAGE)
                .build();
    }

    @Test
    void testFileUploadCreation() {
        // 파일 업로드 생성 테스트
        assertNotNull(testFileUpload, "파일 업로드 객체가 정상적으로 생성되었습니다.");
        assertEquals("test.jpg", testFileUpload.getOriginalFileName());
        assertEquals(FileUpload.UploadType.IMAGE, testFileUpload.getUploadType());
    }

    @Test
    void testFileUploadIsImage() {
        // 이미지 파일 확인 테스트
        assertTrue(testFileUpload.isImage(), "이미지 파일이 정상적으로 인식됩니다.");
    }

    @Test
    void testFileUploadIsActive() {
        // 활성 상태 확인 테스트
        assertTrue(testFileUpload.isActive(), "파일이 활성 상태입니다.");
    }

    @Test
    void testFileUploadRepositoryExists() {
        // 저장소 존재 확인 테스트
        assertNotNull(fileUploadRepositoryPort, "FileUploadRepositoryPort가 정상적으로 주입되었습니다.");
    }

    @Test
    void testFileUploadServiceExists() {
        // 서비스 존재 확인 테스트
        assertNotNull(fileUploadServicePort, "FileUploadServicePort가 정상적으로 주입되었습니다.");
    }
} 