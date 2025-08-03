package com.sgyj.popupmoah.adapter.web.file;

import com.sgyj.popupmoah.domain.file.entity.FileUpload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 파일 업로드 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    
    private Long id;
    private String originalFileName;
    private String storedFileName;
    private String filePath;
    private String fileUrl;
    private String contentType;
    private Long fileSize;
    private String fileExtension;
    private Boolean active;
    private String uploadType;
    private Long referenceId;
    private String referenceType;
    private String thumbnailUrl;
    private String optimizedUrl;
    private Integer imageWidth;
    private Integer imageHeight;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message;
    private Boolean success;

    /**
     * FileUpload 엔티티로부터 응답 DTO 생성
     */
    public static FileUploadResponse from(FileUpload fileUpload) {
        return FileUploadResponse.builder()
                .id(fileUpload.getId())
                .originalFileName(fileUpload.getOriginalFileName())
                .storedFileName(fileUpload.getStoredFileName())
                .filePath(fileUpload.getFilePath())
                .fileUrl(fileUpload.getFileUrl())
                .contentType(fileUpload.getContentType())
                .fileSize(fileUpload.getFileSize())
                .fileExtension(fileUpload.getFileExtension())
                .active(fileUpload.getActive())
                .uploadType(fileUpload.getUploadType() != null ? fileUpload.getUploadType().name() : null)
                .referenceId(fileUpload.getReferenceId())
                .referenceType(fileUpload.getReferenceType() != null ? fileUpload.getReferenceType().name() : null)
                .thumbnailUrl(fileUpload.getThumbnailUrl())
                .optimizedUrl(fileUpload.getOptimizedUrl())
                .imageWidth(fileUpload.getImageWidth())
                .imageHeight(fileUpload.getImageHeight())
                .createdAt(fileUpload.getCreatedAt())
                .updatedAt(fileUpload.getUpdatedAt())
                .success(true)
                .build();
    }

    /**
     * 에러 응답 생성
     */
    public static FileUploadResponse error(String message) {
        return FileUploadResponse.builder()
                .message(message)
                .success(false)
                .build();
    }
} 