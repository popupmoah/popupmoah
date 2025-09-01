package com.sgyj.popupmoah.domain.file.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 파일 업로드 엔티티
 * S3에 업로드된 파일의 메타데이터를 관리합니다.
 */
@Getter
@Entity
@Table(name = "file_upload")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FileUpload extends Object {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String storedFileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String fileExtension;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "upload_type")
    @Enumerated(EnumType.STRING)
    private UploadType uploadType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_type")
    @Enumerated(EnumType.STRING)
    private ReferenceType referenceType;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "optimized_url")
    private String optimizedUrl;

    @Column(name = "image_width")
    private Integer imageWidth;

    @Column(name = "image_height")
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