package com.sgyj.popupmoah.application.file;

import com.sgyj.popupmoah.domain.file.entity.FileUpload;
import com.sgyj.popupmoah.domain.file.port.FileUploadServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 파일 업로드 애플리케이션 서비스
 * 파일 업로드 관련 비즈니스 로직을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileUploadApplicationService {

    private final FileUploadServicePort fileUploadServicePort;

    /**
     * 파일 업로드
     */
    @Transactional
    public FileUpload uploadFile(MultipartFile file, FileUpload.UploadType uploadType,
                                FileUpload.ReferenceType referenceType, Long referenceId) throws IOException {
        log.info("파일 업로드 시작: originalName={}, size={}, uploadType={}, referenceType={}, referenceId={}",
                file.getOriginalFilename(), file.getSize(), uploadType, referenceType, referenceId);
        
        return fileUploadServicePort.uploadFile(file, uploadType, referenceType, referenceId);
    }

    /**
     * 이미지 파일 업로드 및 최적화
     */
    @Transactional
    public FileUpload uploadAndOptimizeImage(MultipartFile file, FileUpload.ReferenceType referenceType,
                                            Long referenceId) throws IOException {
        log.info("이미지 업로드 및 최적화 시작: originalName={}, size={}, referenceType={}, referenceId={}",
                file.getOriginalFilename(), file.getSize(), referenceType, referenceId);
        
        return fileUploadServicePort.uploadAndOptimizeImage(file, referenceType, referenceId);
    }

    /**
     * 파일 조회
     */
    public Optional<FileUpload> getFileById(Long id) {
        return fileUploadServicePort.getFileById(id);
    }

    /**
     * 참조별 파일 목록 조회
     */
    public List<FileUpload> getFilesByReference(FileUpload.ReferenceType referenceType, Long referenceId) {
        return fileUploadServicePort.getFilesByReference(referenceType, referenceId);
    }

    /**
     * 활성화된 파일 목록 조회
     */
    public List<FileUpload> getActiveFilesByReference(FileUpload.ReferenceType referenceType, Long referenceId) {
        return fileUploadServicePort.getActiveFilesByReference(referenceType, referenceId);
    }

    /**
     * 파일 삭제
     */
    @Transactional
    public void deleteFile(Long id) {
        log.info("파일 삭제 시작: fileId={}", id);
        fileUploadServicePort.deleteFile(id);
    }

    /**
     * 파일 비활성화
     */
    @Transactional
    public void deactivateFile(Long id) {
        log.info("파일 비활성화 시작: fileId={}", id);
        fileUploadServicePort.deactivateFile(id);
    }

    /**
     * 파일 URL 생성
     */
    public String generateFileUrl(String filePath) {
        return fileUploadServicePort.generateFileUrl(filePath);
    }

    /**
     * 파일 다운로드 URL 생성
     */
    public String generateDownloadUrl(String filePath, String fileName) {
        return fileUploadServicePort.generateDownloadUrl(filePath, fileName);
    }

    /**
     * 이미지 리사이징
     */
    public byte[] resizeImage(String filePath, int width, int height) throws IOException {
        return fileUploadServicePort.resizeImage(filePath, width, height);
    }

    /**
     * 썸네일 생성
     */
    public byte[] generateThumbnail(byte[] imageData, int width, int height) throws IOException {
        return fileUploadServicePort.generateThumbnail(imageData, width, height);
    }
} 