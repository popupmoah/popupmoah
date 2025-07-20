package com.sgyj.popupmoah.module.popupstore.service;

import com.sgyj.popupmoah.infra.s3.S3Properties;
import com.sgyj.popupmoah.infra.s3.S3Uploader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class S3UploaderTest {

    @Mock
    private S3Properties s3Properties;
    @Mock
    private S3Client s3Client;
    @InjectMocks
    private S3Uploader s3Uploader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(s3Properties.getBucket()).thenReturn("test-bucket");
        when(s3Properties.getRegion()).thenReturn("ap-northeast-2");
        when(s3Properties.getAccessKey()).thenReturn("access");
        when(s3Properties.getSecretKey()).thenReturn("secret");
        // s3Uploader의 s3Client 필드는 실제로는 @PostConstruct에서 초기화되지만, 테스트에서는 mock을 직접 할당
        s3Uploader = new S3Uploader(s3Properties);
        // reflection을 사용해 s3Client 필드에 mock 객체 할당
        try {
            java.lang.reflect.Field field = S3Uploader.class.getDeclaredField("s3Client");
            field.setAccessible(true);
            field.set(s3Uploader, s3Client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("upload 호출 시 S3에 파일이 업로드되고 S3 key가 반환된다")
    void upload_success() throws IOException {
        // given
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getBytes()).thenReturn("test".getBytes());

        // when
        String result = s3Uploader.upload(file, "popupstore/1");

        // then
        assertThat(result).contains("popupstore/1/");
        verify(s3Client).putObject(any(PutObjectRequest.class), any(software.amazon.awssdk.core.sync.RequestBody.class));
    }
} 