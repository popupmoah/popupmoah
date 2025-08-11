package com.sgyj.popupmoah.file.domain.port;

import com.sgyj.popupmoah.file.domain.entity.FileUpload;

import java.util.List;
import java.util.Optional;

/**
 * 파일 업로드 리포지토리 포트
 * 도메인이 정의하는 인터페이스로, 인프라스트럭처가 구현
 */
public interface FileUploadRepositoryPort {
    
    /**
     * 파일 업로드를 저장합니다.
     */
    FileUpload save(FileUpload fileUpload);
    
    /**
     * ID로 파일 업로드를 조회합니다.
     */
    Optional<FileUpload> findById(Long id);
    
    /**
     * 모든 파일 업로드를 조회합니다.
     */
    List<FileUpload> findAll();
    
    /**
     * 활성화된 파일 업로드만 조회합니다.
     */
    List<FileUpload> findByActiveTrue();
    
    /**
     * 참조 ID와 타입으로 파일 업로드를 조회합니다.
     */
    List<FileUpload> findByReferenceIdAndReferenceType(Long referenceId, FileUpload.ReferenceType referenceType);
    
    /**
     * 업로드 타입으로 파일 업로드를 조회합니다.
     */
    List<FileUpload> findByUploadType(FileUpload.UploadType uploadType);
    
    /**
     * 파일 업로드를 삭제합니다.
     */
    void deleteById(Long id);
    
    /**
     * 파일 업로드가 존재하는지 확인합니다.
     */
    boolean existsById(Long id);
} 