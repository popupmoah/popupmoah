package com.sgyj.popupmoah.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * CDN 설정 클래스
 * 이미지 CDN 관련 설정을 관리
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.cdn")
public class CdnConfig {

    /**
     * CDN 기본 URL
     */
    private String baseUrl = "https://cdn.popupmoah.com";

    /**
     * 이미지 업로드 경로
     */
    private String imagePath = "/images";

    /**
     * 썸네일 경로
     */
    private String thumbnailPath = "/thumbnails";

    /**
     * 기본 이미지 URL (이미지가 없을 때 사용)
     */
    private String defaultImageUrl = "/images/default-popup-store.jpg";

    /**
     * 이미지 최대 크기 (MB)
     */
    private int maxImageSize = 10;

    /**
     * 허용되는 이미지 확장자
     */
    private String[] allowedExtensions = {"jpg", "jpeg", "png", "gif", "webp"};

    /**
     * 썸네일 크기 설정
     */
    private ThumbnailSize thumbnailSize = new ThumbnailSize();

    @Data
    public static class ThumbnailSize {
        private int width = 300;
        private int height = 200;
        private String quality = "80";
    }

    /**
     * 전체 이미지 URL 생성
     */
    public String getFullImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return baseUrl + defaultImageUrl;
        }
        
        if (imagePath.startsWith("http")) {
            return imagePath; // 이미 전체 URL인 경우
        }
        
        return baseUrl + (imagePath.startsWith("/") ? imagePath : "/" + imagePath);
    }

    /**
     * 썸네일 URL 생성
     */
    public String getThumbnailUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return baseUrl + defaultImageUrl;
        }
        
        String baseImagePath = imagePath.startsWith("/") ? imagePath.substring(1) : imagePath;
        return baseUrl + thumbnailPath + "/" + baseImagePath;
    }
}
