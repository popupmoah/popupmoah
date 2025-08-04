package com.sgyj.popupmoah;

import com.sgyj.popupmoah.adapter.web.file.FileUploadController;
import com.sgyj.popupmoah.application.file.FileUploadApplicationService;
import com.sgyj.popupmoah.domain.file.entity.FileUpload;
import com.sgyj.popupmoah.domain.file.port.FileUploadRepositoryPort;
import com.sgyj.popupmoah.domain.file.port.FileUploadServicePort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 파일 업로드 통합 테스트
 * Mock을 사용하여 외부 의존성을 격리합니다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class FileUploadIntegrationTest {

    @Autowired
    private FileUploadController fileUploadController;

    @Autowired
    private FileUploadApplicationService fileUploadApplicationService;

    @MockBean
    private FileUploadRepositoryPort fileUploadRepositoryPort;

    @MockBean
    private FileUploadServicePort fileUploadServicePort;

    @Test
    void testFileUploadControllerExists() {
        // 컨트롤러가 정상적으로 생성되었는지 확인
        assertNotNull(fileUploadController, "FileUploadController가 정상적으로 생성되었습니다.");
    }

    @Test
    void testFileUploadApplicationServiceExists() {
        // 애플리케이션 서비스가 정상적으로 생성되었는지 확인
        assertNotNull(fileUploadApplicationService, "FileUploadApplicationService가 정상적으로 생성되었습니다.");
    }

    @Test
    void testMockBeansInjected() {
        // Mock 빈들이 정상적으로 주입되었는지 확인
        assertNotNull(fileUploadRepositoryPort, "FileUploadRepositoryPort Mock이 주입되었습니다.");
        assertNotNull(fileUploadServicePort, "FileUploadServicePort Mock이 주입되었습니다.");
    }

    @Test
    void testFileUploadEntityCreation() {
        // 파일 업로드 엔티티 생성 테스트
        FileUpload mockFileUpload = FileUpload.builder()
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

        assertNotNull(mockFileUpload, "파일 업로드 엔티티가 정상적으로 생성되었습니다.");
        assertEquals("test.jpg", mockFileUpload.getOriginalFileName());
        assertEquals(FileUpload.UploadType.IMAGE, mockFileUpload.getUploadType());
    }
} 