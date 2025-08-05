package com.sgyj.popupmoah.domain.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.Date;

/**
 * S3 파일 서비스
 * AWS S3와의 파일 업로드/다운로드/삭제 기능을 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3FileService {
    
    private final AmazonS3 amazonS3;
    
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    
    @Value("${aws.s3.region}")
    private String region;
    
    /**
     * 파일을 S3에 업로드합니다.
     */
    public void uploadFile(InputStream inputStream, String filePath, String contentType) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, filePath, inputStream, metadata);
            amazonS3.putObject(putObjectRequest);
            
            log.info("파일이 S3에 업로드되었습니다: {}", filePath);
        } catch (Exception e) {
            log.error("S3 파일 업로드 실패: {}", filePath, e);
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }
    
    /**
     * 바이트 배열을 S3에 업로드합니다.
     */
    public void uploadFile(byte[] fileData, String filePath, String contentType) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData)) {
            uploadFile(inputStream, filePath, contentType);
        } catch (Exception e) {
            log.error("S3 파일 업로드 실패: {}", filePath, e);
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }
    
    /**
     * S3에서 파일을 다운로드합니다.
     */
    public byte[] downloadFile(String filePath) {
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, filePath);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            
            return inputStream.readAllBytes();
        } catch (Exception e) {
            log.error("S3 파일 다운로드 실패: {}", filePath, e);
            throw new RuntimeException("파일 다운로드에 실패했습니다.", e);
        }
    }
    
    /**
     * S3에서 파일을 삭제합니다.
     */
    public void deleteFile(String filePath) {
        try {
            amazonS3.deleteObject(bucketName, filePath);
            log.info("파일이 S3에서 삭제되었습니다: {}", filePath);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: {}", filePath, e);
            throw new RuntimeException("파일 삭제에 실패했습니다.", e);
        }
    }
    
    /**
     * 여러 파일을 S3에서 삭제합니다.
     */
    public void deleteFiles(String... filePaths) {
        try {
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
            deleteObjectsRequest.withKeys(filePaths);
            
            amazonS3.deleteObjects(deleteObjectsRequest);
            log.info("{}개의 파일이 S3에서 삭제되었습니다.", filePaths.length);
        } catch (Exception e) {
            log.error("S3 파일들 삭제 실패", e);
            throw new RuntimeException("파일들 삭제에 실패했습니다.", e);
        }
    }
    
    /**
     * 파일 URL을 생성합니다.
     */
    public String generateFileUrl(String filePath) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, filePath);
    }
    
    /**
     * 파일 다운로드 URL을 생성합니다 (만료 시간 포함).
     */
    public String generateDownloadUrl(String filePath, String fileName) {
        try {
            Date expiration = new Date();
            expiration.setTime(expiration.getTime() + Duration.ofHours(1).toMillis()); // 1시간 만료
            
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, filePath);
            request.setMethod(HttpMethod.GET);
            request.setExpiration(expiration);
            request.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            
            URL presignedUrl = amazonS3.generatePresignedUrl(request);
            return presignedUrl.toString();
        } catch (Exception e) {
            log.error("다운로드 URL 생성 실패: {}", filePath, e);
            throw new RuntimeException("다운로드 URL 생성에 실패했습니다.", e);
        }
    }
    
    /**
     * 파일이 존재하는지 확인합니다.
     */
    public boolean fileExists(String filePath) {
        try {
            return amazonS3.doesObjectExist(bucketName, filePath);
        } catch (Exception e) {
            log.error("파일 존재 확인 실패: {}", filePath, e);
            return false;
        }
    }
    
    /**
     * 파일 크기를 가져옵니다.
     */
    public long getFileSize(String filePath) {
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, filePath);
            return s3Object.getObjectMetadata().getContentLength();
        } catch (Exception e) {
            log.error("파일 크기 조회 실패: {}", filePath, e);
            throw new RuntimeException("파일 크기 조회에 실패했습니다.", e);
        }
    }
    
    /**
     * 파일의 메타데이터를 가져옵니다.
     */
    public ObjectMetadata getFileMetadata(String filePath) {
        try {
            return amazonS3.getObjectMetadata(bucketName, filePath);
        } catch (Exception e) {
            log.error("파일 메타데이터 조회 실패: {}", filePath, e);
            throw new RuntimeException("파일 메타데이터 조회에 실패했습니다.", e);
        }
    }
    
    /**
     * 파일을 복사합니다.
     */
    public void copyFile(String sourcePath, String destinationPath) {
        try {
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, sourcePath, bucketName, destinationPath);
            amazonS3.copyObject(copyObjectRequest);
            
            log.info("파일이 복사되었습니다: {} -> {}", sourcePath, destinationPath);
        } catch (Exception e) {
            log.error("파일 복사 실패: {} -> {}", sourcePath, destinationPath, e);
            throw new RuntimeException("파일 복사에 실패했습니다.", e);
        }
    }
} 