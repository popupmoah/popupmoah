package com.sgyj.popupmoah.infra.service;

import com.sgyj.popupmoah.infra.config.CdnConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

/**
 * 이미지 CDN 서비스
 * 이미지 업로드, URL 생성, 썸네일 생성 등을 담당
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageCdnService {

    private final CdnConfig cdnConfig;

    /**
     * 이미지 업로드
     */
    public String uploadImage(MultipartFile file, String category) {
        try {
            // 파일 유효성 검사
            validateImageFile(file);

            // 파일명 생성
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String filename = generateUniqueFilename(extension);

            // 업로드 경로 생성
            String uploadPath = cdnConfig.getImagePath() + "/" + category;
            Path targetPath = Paths.get("uploads" + uploadPath, filename);

            // 디렉토리 생성
            Files.createDirectories(targetPath.getParent());

            // 파일 저장
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // CDN URL 반환
            String imageUrl = uploadPath + "/" + filename;
            log.info("이미지 업로드 완료: {}", imageUrl);
            
            return imageUrl;

        } catch (IOException e) {
            log.error("이미지 업로드 실패: {}", e.getMessage());
            throw new RuntimeException("이미지 업로드에 실패했습니다.", e);
        }
    }

    /**
     * 이미지 URL 생성
     */
    public String getImageUrl(String imagePath) {
        return cdnConfig.getFullImageUrl(imagePath);
    }

    /**
     * 썸네일 URL 생성
     */
    public String getThumbnailUrl(String imagePath) {
        return cdnConfig.getThumbnailUrl(imagePath);
    }

    /**
     * 이미지 삭제
     */
    public void deleteImage(String imagePath) {
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                Path filePath = Paths.get("uploads" + imagePath);
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    log.info("이미지 삭제 완료: {}", imagePath);
                }
            }
        } catch (IOException e) {
            log.error("이미지 삭제 실패: {}", e.getMessage());
        }
    }

    /**
     * 이미지 파일 유효성 검사
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 선택되지 않았습니다.");
        }

        // 파일 크기 검사
        if (file.getSize() > cdnConfig.getMaxImageSize() * 1024 * 1024) {
            throw new IllegalArgumentException(
                String.format("파일 크기가 너무 큽니다. 최대 %dMB까지 허용됩니다.", cdnConfig.getMaxImageSize())
            );
        }

        // 파일 확장자 검사
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("파일명이 올바르지 않습니다.");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!Arrays.asList(cdnConfig.getAllowedExtensions()).contains(extension)) {
            throw new IllegalArgumentException(
                String.format("지원되지 않는 파일 형식입니다. 허용된 형식: %s", 
                    String.join(", ", cdnConfig.getAllowedExtensions()))
            );
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * 고유한 파일명 생성
     */
    private String generateUniqueFilename(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }

    /**
     * 이미지 최적화 (실제 구현에서는 이미지 리사이징 라이브러리 사용)
     */
    public String optimizeImage(String imagePath) {
        // 실제 구현에서는 ImageIO, Thumbnailator 등을 사용하여 이미지 최적화
        log.info("이미지 최적화: {}", imagePath);
        return imagePath;
    }
}
