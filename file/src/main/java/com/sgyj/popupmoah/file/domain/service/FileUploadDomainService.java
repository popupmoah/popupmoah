package com.sgyj.popupmoah.file.domain.service;

import com.sgyj.popupmoah.file.domain.entity.FileUpload;
import com.sgyj.popupmoah.file.domain.port.FileUploadRepositoryPort;
import com.sgyj.popupmoah.file.domain.port.FileUploadServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 파일 업로드 도메인 서비스
 * 파일 업로드 도메인의 비즈니스 로직을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FileUploadDomainService implements FileUploadServicePort {
    
    private final FileUploadRepositoryPort fileUploadRepositoryPort;
    private final S3FileService s3FileService;
    private final ImageOptimizationService imageOptimizationService;
    
    @Override
    public FileUpload uploadFile(MultipartFile file, FileUpload.UploadType uploadType, 
                                FileUpload.ReferenceType referenceType, Long referenceId) throws IOException {
        // 도메인 규칙 검증
        validateFile(file, uploadType);
        
        // 파일 정보 생성
        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        String storedFileName = generateStoredFileName(fileExtension);
        String filePath = generateFilePath(uploadType, referenceType, storedFileName);
        
        // S3에 파일 업로드
        s3FileService.uploadFile(file.getInputStream(), filePath, file.getContentType());
        
        // 파일 URL 생성
        String fileUrl = s3FileService.generateFileUrl(filePath);
        
        // 파일 업로드 엔티티 생성
        FileUpload fileUpload = FileUpload.builder()
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .filePath(filePath)
                .fileUrl(fileUrl)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .fileExtension(fileExtension)
                .uploadType(uploadType)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .active(true)
                .build();
        
        // 데이터베이스에 저장
        return fileUploadRepositoryPort.save(fileUpload);
    }
    
    @Override
    public FileUpload uploadAndOptimizeImage(MultipartFile file, FileUpload.ReferenceType referenceType, 
                                            Long referenceId) throws IOException {
        // 이미지 파일 검증
        validateImageFile(file);
        
        // 원본 이미지 업로드
        FileUpload fileUpload = uploadFile(file, FileUpload.UploadType.IMAGE, referenceType, referenceId);
        
        // 이미지 최적화 처리
        byte[] imageData = file.getBytes();
        
        // 이미지 크기 정보 추출
        int[] dimensions = imageOptimizationService.getImageDimensions(imageData);
        fileUpload.setImageDimensions(dimensions[0], dimensions[1]);
        
        // 썸네일 생성 및 업로드
        byte[] thumbnailData = imageOptimizationService.generateThumbnail(imageData, 300, 300);
        String thumbnailPath = generateThumbnailPath(fileUpload.getFilePath());
        s3FileService.uploadFile(thumbnailData, thumbnailPath, "image/jpeg");
        fileUpload.setThumbnailUrl(s3FileService.generateFileUrl(thumbnailPath));
        
        // 최적화된 이미지 생성 및 업로드
        byte[] optimizedData = imageOptimizationService.optimizeImage(imageData, 0.8f);
        String optimizedPath = generateOptimizedPath(fileUpload.getFilePath());
        s3FileService.uploadFile(optimizedData, optimizedPath, "image/jpeg");
        fileUpload.setOptimizedUrl(s3FileService.generateFileUrl(optimizedPath));
        
        // 업데이트된 정보 저장
        return fileUploadRepositoryPort.save(fileUpload);
    }
    
    @Override
    public Optional<FileUpload> getFileById(Long id) {
        return fileUploadRepositoryPort.findById(id);
    }
    
    @Override
    public List<FileUpload> getFilesByReference(FileUpload.ReferenceType referenceType, Long referenceId) {
        return fileUploadRepositoryPort.findByReferenceTypeAndReferenceId(referenceType, referenceId);
    }
    
    @Override
    public List<FileUpload> getActiveFilesByReference(FileUpload.ReferenceType referenceType, Long referenceId) {
        return fileUploadRepositoryPort.findActiveByReferenceTypeAndReferenceId(referenceType, referenceId);
    }
    
    @Override
    public void deleteFile(Long id) {
        Optional<FileUpload> fileUpload = fileUploadRepositoryPort.findById(id);
        if (fileUpload.isPresent()) {
            // S3에서 파일 삭제
            s3FileService.deleteFile(fileUpload.get().getFilePath());
            
            // 썸네일과 최적화된 이미지도 삭제
            if (fileUpload.get().hasThumbnail()) {
                String thumbnailPath = generateThumbnailPath(fileUpload.get().getFilePath());
                s3FileService.deleteFile(thumbnailPath);
            }
            
            if (fileUpload.get().hasOptimizedImage()) {
                String optimizedPath = generateOptimizedPath(fileUpload.get().getFilePath());
                s3FileService.deleteFile(optimizedPath);
            }
            
            // 데이터베이스에서 삭제
            fileUploadRepositoryPort.deleteById(id);
        }
    }
    
    @Override
    public void deactivateFile(Long id) {
        fileUploadRepositoryPort.deactivateById(id);
    }
    
    @Override
    public String generateFileUrl(String filePath) {
        return s3FileService.generateFileUrl(filePath);
    }
    
    @Override
    public String generateDownloadUrl(String filePath, String fileName) {
        return s3FileService.generateDownloadUrl(filePath, fileName);
    }
    
    @Override
    public byte[] resizeImage(byte[] imageData, int width, int height) throws IOException {
        return imageOptimizationService.resizeImage(imageData, width, height);
    }
    
    @Override
    public byte[] generateThumbnail(byte[] imageData, int width, int height) throws IOException {
        return imageOptimizationService.generateThumbnail(imageData, width, height);
    }
    
    @Override
    public byte[] convertImageFormat(byte[] imageData, String format) throws IOException {
        return imageOptimizationService.convertImageFormat(imageData, format);
    }
    
    @Override
    public byte[] optimizeImageQuality(byte[] imageData, float quality) throws IOException {
        return imageOptimizationService.optimizeImage(imageData, quality);
    }
    
    // Private helper methods
    
    private void validateFile(MultipartFile file, FileUpload.UploadType uploadType) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
        
        if (file.getSize() > getMaxFileSize(uploadType)) {
            throw new IllegalArgumentException("파일 크기가 제한을 초과했습니다.");
        }
        
        String contentType = file.getContentType();
        if (!isValidContentType(contentType, uploadType)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
        }
    }
    
    private void validateImageFile(MultipartFile file) {
        validateFile(file, FileUpload.UploadType.IMAGE);
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일이 아닙니다.");
        }
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
    
    private String generateStoredFileName(String fileExtension) {
        return UUID.randomUUID().toString() + "." + fileExtension;
    }
    
    private String generateFilePath(FileUpload.UploadType uploadType, FileUpload.ReferenceType referenceType, String fileName) {
        return String.format("%s/%s/%s", uploadType.name().toLowerCase(), 
                           referenceType.name().toLowerCase(), fileName);
    }
    
    private String generateThumbnailPath(String originalPath) {
        String pathWithoutExtension = originalPath.substring(0, originalPath.lastIndexOf("."));
        return pathWithoutExtension + "_thumb.jpg";
    }
    
    private String generateOptimizedPath(String originalPath) {
        String pathWithoutExtension = originalPath.substring(0, originalPath.lastIndexOf("."));
        return pathWithoutExtension + "_optimized.jpg";
    }
    
    private long getMaxFileSize(FileUpload.UploadType uploadType) {
        switch (uploadType) {
            case IMAGE:
                return 10 * 1024 * 1024; // 10MB
            case VIDEO:
                return 100 * 1024 * 1024; // 100MB
            case AUDIO:
                return 50 * 1024 * 1024; // 50MB
            case DOCUMENT:
                return 20 * 1024 * 1024; // 20MB
            default:
                return 10 * 1024 * 1024; // 10MB
        }
    }
    
    private boolean isValidContentType(String contentType, FileUpload.UploadType uploadType) {
        if (contentType == null) {
            return false;
        }
        
        switch (uploadType) {
            case IMAGE:
                return contentType.startsWith("image/");
            case VIDEO:
                return contentType.startsWith("video/");
            case AUDIO:
                return contentType.startsWith("audio/");
            case DOCUMENT:
                return contentType.startsWith("application/") || 
                       contentType.startsWith("text/") ||
                       contentType.equals("application/pdf");
            default:
                return true;
        }
    }
} 