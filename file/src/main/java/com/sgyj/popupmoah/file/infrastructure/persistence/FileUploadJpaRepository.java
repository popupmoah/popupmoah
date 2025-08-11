package com.sgyj.popupmoah.adapter.persistence.file;

import com.sgyj.popupmoah.file.domain.entity.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 파일 업로드 JPA Repository
 * Spring Data JPA를 사용한 데이터 접근 계층
 */
@Repository
public interface FileUploadJpaRepository extends JpaRepository<FileUpload, Long> {

    /**
     * 참조 타입과 ID로 파일들을 조회
     */
    List<FileUpload> findByReferenceTypeAndReferenceId(FileUpload.ReferenceType referenceType, Long referenceId);

    /**
     * 활성화된 파일들을 참조 타입과 ID로 조회
     */
    List<FileUpload> findByReferenceTypeAndReferenceIdAndActiveTrue(FileUpload.ReferenceType referenceType, Long referenceId);

    /**
     * 업로드 타입별로 파일들을 조회
     */
    List<FileUpload> findByUploadType(FileUpload.UploadType uploadType);

    /**
     * 활성화된 파일들을 업로드 타입별로 조회
     */
    List<FileUpload> findByUploadTypeAndActiveTrue(FileUpload.UploadType uploadType);

    /**
     * 파일 확장자별로 파일들을 조회
     */
    List<FileUpload> findByFileExtension(String fileExtension);

    /**
     * 활성화된 파일들을 파일 확장자별로 조회
     */
    List<FileUpload> findByFileExtensionAndActiveTrue(String fileExtension);

    /**
     * 파일 크기 범위로 파일들을 조회
     */
    @Query("SELECT f FROM FileUpload f WHERE f.fileSize BETWEEN :minSize AND :maxSize")
    List<FileUpload> findByFileSizeBetween(@Param("minSize") Long minSize, @Param("maxSize") Long maxSize);

    /**
     * 활성화된 파일들을 파일 크기 범위로 조회
     */
    @Query("SELECT f FROM FileUpload f WHERE f.fileSize BETWEEN :minSize AND :maxSize AND f.active = true")
    List<FileUpload> findActiveByFileSizeBetween(@Param("minSize") Long minSize, @Param("maxSize") Long maxSize);

    /**
     * 참조 타입과 ID로 파일을 비활성화
     */
    @Modifying
    @Query("UPDATE FileUpload f SET f.active = false WHERE f.referenceType = :referenceType AND f.referenceId = :referenceId")
    void deactivateByReferenceTypeAndReferenceId(@Param("referenceType") FileUpload.ReferenceType referenceType, 
                                                @Param("referenceId") Long referenceId);

    /**
     * 참조 타입과 ID로 파일을 삭제
     */
    @Modifying
    @Query("DELETE FROM FileUpload f WHERE f.referenceType = :referenceType AND f.referenceId = :referenceId")
    void deleteByReferenceTypeAndReferenceId(@Param("referenceType") FileUpload.ReferenceType referenceType, 
                                           @Param("referenceId") Long referenceId);

    /**
     * 파일 크기가 큰 파일들을 조회 (정렬 포함)
     */
    @Query("SELECT f FROM FileUpload f WHERE f.fileSize > :size ORDER BY f.fileSize DESC")
    List<FileUpload> findLargeFiles(@Param("size") Long size);

    /**
     * 최근 업로드된 파일들을 조회
     */
    @Query("SELECT f FROM FileUpload f ORDER BY f.createdAt DESC")
    List<FileUpload> findRecentFiles();

    /**
     * 특정 기간 내에 업로드된 파일들을 조회
     */
    @Query("SELECT f FROM FileUpload f WHERE f.createdAt BETWEEN :startDate AND :endDate")
    List<FileUpload> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate, 
                                          @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * 이미지 파일들만 조회
     */
    @Query("SELECT f FROM FileUpload f WHERE f.uploadType = 'IMAGE'")
    List<FileUpload> findImageFiles();

    /**
     * 활성화된 이미지 파일들만 조회
     */
    @Query("SELECT f FROM FileUpload f WHERE f.uploadType = 'IMAGE' AND f.active = true")
    List<FileUpload> findActiveImageFiles();

    /**
     * 썸네일이 있는 이미지 파일들을 조회
     */
    @Query("SELECT f FROM FileUpload f WHERE f.uploadType = 'IMAGE' AND f.thumbnailUrl IS NOT NULL")
    List<FileUpload> findImageFilesWithThumbnail();

    /**
     * 최적화된 이미지가 있는 파일들을 조회
     */
    @Query("SELECT f FROM FileUpload f WHERE f.uploadType = 'IMAGE' AND f.optimizedUrl IS NOT NULL")
    List<FileUpload> findImageFilesWithOptimized();

    /**
     * ID로 파일을 비활성화
     */
    @Modifying
    @Query("UPDATE FileUpload f SET f.active = false WHERE f.id = :id")
    void deactivateById(@Param("id") Long id);
} 