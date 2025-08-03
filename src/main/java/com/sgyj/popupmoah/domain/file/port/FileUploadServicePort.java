package com.sgyj.popupmoah.domain.file.port;

import com.sgyj.popupmoah.domain.file.entity.FileUpload;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 파일 업로드 서비스 포트 (Inbound Port)
 * 외부에서 파일 업로드 도메인 서비스를 호출할 수 있는 인터페이스를 정의합니다.
 */
public interface FileUploadServicePort {
    
    /**
     * 파일을 업로드합니다.
     */
    FileUpload uploadFile(MultipartFile file, FileUpload.UploadType uploadType, 
                         FileUpload.ReferenceType referenceType, Long referenceId) throws IOException;
    
    /**
     * 이미지 파일을 업로드하고 최적화합니다.
     */
    FileUpload uploadAndOptimizeImage(MultipartFile file, FileUpload.ReferenceType referenceType, 
                                     Long referenceId) throws IOException;
    
    /**
     * ID로 파일을 조회합니다.
     */
    Optional<FileUpload> getFileById(Long id);
    
    /**
     * 참조 타입과 ID로 파일들을 조회합니다.
     */
    List<FileUpload> getFilesByReference(FileUpload.ReferenceType referenceType, Long referenceId);
    
    /**
     * 활성화된 파일들을 참조 타입과 ID로 조회합니다.
     */
    List<FileUpload> getActiveFilesByReference(FileUpload.ReferenceType referenceType, Long referenceId);
    
    /**
     * 파일을 삭제합니다.
     */
    void deleteFile(Long id);
    
    /**
     * 파일을 비활성화합니다.
     */
    void deactivateFile(Long id);
    
    /**
     * 파일 URL을 생성합니다.
     */
    String generateFileUrl(String filePath);
    
    /**
     * 파일 다운로드 URL을 생성합니다.
     */
    String generateDownloadUrl(String filePath, String fileName);
    
    /**
     * 이미지 리사이징을 수행합니다.
     */
    byte[] resizeImage(String filePath, int width, int height) throws IOException;
    
    /**
     * 썸네일을 생성합니다.
     */
    byte[] generateThumbnail(byte[] imageData, int width, int height) throws IOException;
    
    /**
     * 이미지 포맷을 변환합니다.
     */
    byte[] convertImageFormat(byte[] imageData, String format) throws IOException;
    
    /**
     * 이미지 품질을 최적화합니다.
     */
    byte[] optimizeImageQuality(byte[] imageData, float quality) throws IOException;
} 