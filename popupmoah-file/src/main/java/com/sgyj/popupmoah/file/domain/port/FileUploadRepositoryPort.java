package com.sgyj.popupmoah.domain.file.port;

import com.sgyj.popupmoah.domain.file.entity.FileUpload;

import java.util.List;
import java.util.Optional;

/**
 * 파일 업로드 저장소 포트 (Outbound Port)
 * 도메인에서 외부 저장소와의 인터페이스를 정의합니다.
 */
public interface FileUploadRepositoryPort {
    
    /**
     * 파일 업로드 정보를 저장합니다.
     */
    FileUpload save(FileUpload fileUpload);
    
    /**
     * ID로 파일 업로드 정보를 조회합니다.
     */
    Optional<FileUpload> findById(Long id);
    
    /**
     * 참조 타입과 ID로 파일 업로드 정보들을 조회합니다.
     */
    List<FileUpload> findByReferenceTypeAndReferenceId(FileUpload.ReferenceType referenceType, Long referenceId);
    
    /**
     * 활성화된 파일들을 참조 타입과 ID로 조회합니다.
     */
    List<FileUpload> findActiveByReferenceTypeAndReferenceId(FileUpload.ReferenceType referenceType, Long referenceId);
    
    /**
     * 파일 업로드 정보를 삭제합니다.
     */
    void deleteById(Long id);
    
    /**
     * 파일 업로드 정보를 비활성화합니다.
     */
    void deactivateById(Long id);
    
    /**
     * 참조 타입과 ID로 모든 파일을 삭제합니다.
     */
    void deleteByReferenceTypeAndReferenceId(FileUpload.ReferenceType referenceType, Long referenceId);
    
    /**
     * 참조 타입과 ID로 모든 파일을 비활성화합니다.
     */
    void deactivateByReferenceTypeAndReferenceId(FileUpload.ReferenceType referenceType, Long referenceId);
} 