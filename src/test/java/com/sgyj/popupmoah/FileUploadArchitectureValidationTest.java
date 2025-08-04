package com.sgyj.popupmoah;

import com.sgyj.popupmoah.adapter.persistence.file.FileUploadPersistenceAdapter;
import com.sgyj.popupmoah.config.TestSecurityConfig;
import com.sgyj.popupmoah.domain.file.port.FileUploadRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 파일 업로드 헥사고날 아키텍처 검증 테스트
 * FileUploadPersistenceAdapter가 올바르게 구현되고 의존성 주입이 작동하는지 확인
 */
@SpringBootTest(classes = {PopupmoahApplication.class})
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class FileUploadArchitectureValidationTest {

    @Autowired(required = false)
    private FileUploadRepositoryPort fileUploadRepositoryPort;

    @Autowired(required = false) 
    private FileUploadPersistenceAdapter fileUploadPersistenceAdapter;

    @Test
    void fileUploadRepositoryPortIsAvailable() {
        // FileUploadRepositoryPort 구현체가 Spring 컨텍스트에 등록되어 있는지 확인
        assertThat(fileUploadRepositoryPort).isNotNull();
        assertThat(fileUploadRepositoryPort).isInstanceOf(FileUploadPersistenceAdapter.class);
    }

    @Test
    void fileUploadPersistenceAdapterIsRegistered() {
        // FileUploadPersistenceAdapter가 직접 등록되어 있는지 확인
        assertThat(fileUploadPersistenceAdapter).isNotNull();
    }

    @Test
    void hexagonalArchitectureIsComplete() {
        // 헥사고날 아키텍처가 완성되었는지 확인:
        // Domain Port -> Adapter 구현체의 의존성이 올바르게 연결되어 있는지
        assertThat(fileUploadRepositoryPort).isSameAs(fileUploadPersistenceAdapter);
    }
}