package com.sgyj.popupmoah;

import com.sgyj.popupmoah.adapter.persistence.file.FileUploadJpaRepository;
import com.sgyj.popupmoah.adapter.persistence.file.FileUploadPersistenceAdapter;
import com.sgyj.popupmoah.domain.file.entity.FileUpload;
import com.sgyj.popupmoah.domain.file.port.FileUploadRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

/**
 * FileUploadPersistenceAdapter 단위 테스트
 * Spring 컨텍스트 없이 어댑터의 기본 동작을 검증
 */
class FileUploadPersistenceAdapterUnitTest {

    @Mock
    private FileUploadJpaRepository fileUploadJpaRepository;

    private FileUploadPersistenceAdapter fileUploadPersistenceAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fileUploadPersistenceAdapter = new FileUploadPersistenceAdapter(fileUploadJpaRepository);
    }

    @Test
    @DisplayName("FileUploadPersistenceAdapter는 FileUploadRepositoryPort를 구현한다")
    void implementsFileUploadRepositoryPort() {
        // FileUploadPersistenceAdapter가 포트 인터페이스를 올바르게 구현하는지 확인
        assertThat(fileUploadPersistenceAdapter).isInstanceOf(FileUploadRepositoryPort.class);
    }

    @Test
    @DisplayName("save 메서드가 JPA Repository에 올바르게 위임된다")
    void save_delegatesToJpaRepository() {
        // Given
        FileUpload mockFileUpload = FileUpload.builder()
                .originalFileName("test.jpg")
                .storedFileName("stored_test.jpg")
                .filePath("/uploads/stored_test.jpg")
                .fileUrl("https://example.com/stored_test.jpg")
                .contentType("image/jpeg")
                .fileSize(1024L)
                .fileExtension("jpg")
                .uploadType(FileUpload.UploadType.IMAGE)
                .referenceType(FileUpload.ReferenceType.PROFILE)
                .referenceId(1L)
                .build();

        when(fileUploadJpaRepository.save(any(FileUpload.class))).thenReturn(mockFileUpload);

        // When
        FileUpload result = fileUploadPersistenceAdapter.save(mockFileUpload);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOriginalFileName()).isEqualTo("test.jpg");
        verify(fileUploadJpaRepository).save(mockFileUpload);
    }

    @Test
    @DisplayName("findById 메서드가 JPA Repository에 올바르게 위임된다")
    void findById_delegatesToJpaRepository() {
        // Given
        Long fileId = 1L;
        FileUpload mockFileUpload = FileUpload.builder()
                .originalFileName("test.jpg")
                .build();
        when(fileUploadJpaRepository.findById(fileId)).thenReturn(Optional.of(mockFileUpload));

        // When
        Optional<FileUpload> result = fileUploadPersistenceAdapter.findById(fileId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getOriginalFileName()).isEqualTo("test.jpg");
        verify(fileUploadJpaRepository).findById(fileId);
    }

    @Test
    @DisplayName("findByReferenceTypeAndReferenceId 메서드가 JPA Repository에 올바르게 위임된다")
    void findByReferenceTypeAndReferenceId_delegatesToJpaRepository() {
        // Given
        FileUpload.ReferenceType referenceType = FileUpload.ReferenceType.PROFILE;
        Long referenceId = 1L;
        List<FileUpload> mockFiles = List.of(
                FileUpload.builder().originalFileName("file1.jpg").build(),
                FileUpload.builder().originalFileName("file2.jpg").build()
        );
        when(fileUploadJpaRepository.findByReferenceTypeAndReferenceId(referenceType, referenceId))
                .thenReturn(mockFiles);

        // When
        List<FileUpload> result = fileUploadPersistenceAdapter.findByReferenceTypeAndReferenceId(referenceType, referenceId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getOriginalFileName()).isEqualTo("file1.jpg");
        verify(fileUploadJpaRepository).findByReferenceTypeAndReferenceId(referenceType, referenceId);
    }

    @Test
    @DisplayName("헥사고날 아키텍처: 도메인 포트와 어댑터가 올바르게 연결되어 있다")
    void hexagonalArchitecture_portAdapterConnection() {
        // 포트(인터페이스)를 통해 어댑터에 접근할 수 있는지 확인
        FileUploadRepositoryPort port = fileUploadPersistenceAdapter;
        
        assertThat(port).isNotNull();
        assertThat(port).isInstanceOf(FileUploadPersistenceAdapter.class);
        
        // 포트의 모든 메서드가 구현되어 있는지 확인
        assertThat(port).isInstanceOf(FileUploadRepositoryPort.class);
    }
}