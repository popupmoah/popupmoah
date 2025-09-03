package com.sgyj.popupmoah.file.infrastructure.persistence;

import com.sgyj.popupmoah.file.domain.entity.FileUpload;
import com.sgyj.popupmoah.file.domain.port.FileUploadRepositoryPort;
import com.sgyj.popupmoah.file.infrastructure.persistence.FileUploadJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 파일 업로드 저장소 어댑터 (Outbound Adapter)
 * FileUploadRepositoryPort 인터페이스를 구현하여 JPA Repository와 연결합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileUploadPersistenceAdapter implements FileUploadRepositoryPort {

    private final FileUploadJpaRepository fileUploadJpaRepository;

    @Override
    public FileUpload save(FileUpload fileUpload) {
        log.info("파일 업로드 정보 저장: id={}, originalFileName={}", 
                fileUpload.getId(), fileUpload.getOriginalFileName());
        return fileUploadJpaRepository.save(fileUpload);
    }

    @Override
    public Optional<FileUpload> findById(Long id) {
        return fileUploadJpaRepository.findById(id);
    }

    @Override
    public List<FileUpload> findByReferenceTypeAndReferenceId(FileUpload.ReferenceType referenceType, Long referenceId) {
        return fileUploadJpaRepository.findByReferenceTypeAndReferenceId(referenceType, referenceId);
    }

    @Override
    public List<FileUpload> findActiveByReferenceTypeAndReferenceId(FileUpload.ReferenceType referenceType, Long referenceId) {
        return fileUploadJpaRepository.findByReferenceTypeAndReferenceIdAndActiveTrue(referenceType, referenceId);
    }

    @Override
    public void deleteById(Long id) {
        log.info("파일 업로드 정보 삭제: id={}", id);
        fileUploadJpaRepository.deleteById(id);
    }

    @Override
    public void deactivateById(Long id) {
        log.info("파일 업로드 정보 비활성화: id={}", id);
        fileUploadJpaRepository.deactivateById(id);
    }

    @Override
    public void deleteByReferenceTypeAndReferenceId(FileUpload.ReferenceType referenceType, Long referenceId) {
        log.info("참조별 파일 업로드 정보 삭제: referenceType={}, referenceId={}", referenceType, referenceId);
        fileUploadJpaRepository.deleteByReferenceTypeAndReferenceId(referenceType, referenceId);
    }

    @Override
    public void deactivateByReferenceTypeAndReferenceId(FileUpload.ReferenceType referenceType, Long referenceId) {
        log.info("참조별 파일 업로드 정보 비활성화: referenceType={}, referenceId={}", referenceType, referenceId);
        fileUploadJpaRepository.deactivateByReferenceTypeAndReferenceId(referenceType, referenceId);
    }
} 