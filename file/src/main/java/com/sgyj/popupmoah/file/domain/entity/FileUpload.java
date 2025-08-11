package com.sgyj.popupmoah.file.domain.entity;

import com.sgyj.popupmoah.core.entity.CreatedEntity;
import com.sgyj.popupmoah.core.entity.UpdatedEntity;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 파일 업로드 도메인 엔티티
 * 순수 Java로 구현된 도메인 객체
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FileUpload extends CreatedEntity {

    private Long id;
    private String originalFileName;
    private String storedFileName;
    private String filePath;
    private String fileUrl;
    private String contentType;
    private Long fileSize;
    private String fileExtension;
    private Boolean active;
    private UploadType uploadType;
    private Long referenceId;
    private ReferenceType referenceType;
    private String thumbnailUrl;
    private String optimizedUrl;
    private Integer imageWidth;
    private Integer imageHeight;

    /**
     * 파일 업로드 타입
     */
    public enum UploadType {
        IMAGE,      // 이미지 파일
        DOCUMENT,   // 문서 파일
        VIDEO,      // 비디오 파일
        AUDIO       // 오디오 파일
    }

    /**
     * 참조 타입
     */
    public enum ReferenceType {
        POPUP_STORE,    // 팝업스토어
        REVIEW,         // 리뷰
        PROFILE,        // 프로필
        CATEGORY        // 카테고리
    }

    /**
     * 파일이 이미지인지 확인
     */
    public boolean isImage() {
        return uploadType == UploadType.IMAGE;
    }

    /**
     * 썸네일이 있는지 확인
     */
    public boolean hasThumbnail() {
        return thumbnailUrl != null && !thumbnailUrl.isEmpty();
    }

    /**
     * 최적화된 이미지가 있는지 확인
     */
    public boolean hasOptimizedImage() {
        return optimizedUrl != null && !optimizedUrl.isEmpty();
    }

    /**
     * 파일 크기를 MB 단위로 반환
     */
    public double getFileSizeInMB() {
        return fileSize / (1024.0 * 1024.0);
    }

    /**
     * 파일을 비활성화
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * 썸네일 URL 설정
     */
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    /**
     * 최적화된 이미지 URL 설정
     */
    public void setOptimizedUrl(String optimizedUrl) {
        this.optimizedUrl = optimizedUrl;
    }

    /**
     * 이미지 크기 설정
     */
    public void setImageDimensions(Integer width, Integer height) {
        this.imageWidth = width;
        this.imageHeight = height;
    }
} 