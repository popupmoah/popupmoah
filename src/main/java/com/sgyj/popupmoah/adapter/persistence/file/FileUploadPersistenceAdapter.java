package com.sgyj.popupmoah.adapter.persistence.file;

import com.sgyj.popupmoah.domain.file.entity.FileUpload;
import com.sgyj.popupmoah.domain.file.port.FileUploadRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 파일 업로드 영속성 어댑터 (Outbound Adapter)
 * 도메인의 FileUploadRepositoryPort를 구현하여 JPA Repository와 연결합니다.
 * 헥사고날 아키텍처의 포트-어댑터 패턴을 완성하는 핵심 컴포넌트입니다.
 */
@Component
@RequiredArgsConstructor
@Transactional
public class FileUploadPersistenceAdapter implements FileUploadRepositoryPort {
    
    private final FileUploadJpaRepository fileUploadJpaRepository;
    
    @Override
    public FileUpload save(FileUpload fileUpload) {
        return fileUploadJpaRepository.save(fileUpload);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<FileUpload> findById(Long id) {
        return fileUploadJpaRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FileUpload> findByReferenceTypeAndReferenceId(FileUpload.ReferenceType referenceType, Long referenceId) {
        return fileUploadJpaRepository.findByReferenceTypeAndReferenceId(referenceType, referenceId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FileUpload> findActiveByReferenceTypeAndReferenceId(FileUpload.ReferenceType referenceType, Long referenceId) {
        return fileUploadJpaRepository.findByReferenceTypeAndReferenceIdAndActiveTrue(referenceType, referenceId);
    }
    
    @Override
    public void deleteById(Long id) {
        fileUploadJpaRepository.deleteById(id);
    }
    
    @Override
    public void deactivateById(Long id) {
        Optional<FileUpload> fileUpload = fileUploadJpaRepository.findById(id);
        if (fileUpload.isPresent()) {
            FileUpload file = fileUpload.get();
            file.deactivate();
            fileUploadJpaRepository.save(file);
        }
    }
    
    @Override
    public void deleteByReferenceTypeAndReferenceId(FileUpload.ReferenceType referenceType, Long referenceId) {
        fileUploadJpaRepository.deleteByReferenceTypeAndReferenceId(referenceType, referenceId);
    }
    
    @Override
    public void deactivateByReferenceTypeAndReferenceId(FileUpload.ReferenceType referenceType, Long referenceId) {
        fileUploadJpaRepository.deactivateByReferenceTypeAndReferenceId(referenceType, referenceId);
    }
}