package com.sgyj.popupmoah.adapter.web.file;

import com.sgyj.popupmoah.application.file.FileUploadApplicationService;
import com.sgyj.popupmoah.domain.file.entity.FileUpload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 파일 업로드 컨트롤러
 * 파일 업로드, 다운로드, 조회 기능을 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadApplicationService fileUploadApplicationService;

    /**
     * 파일 업로드
     */
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("uploadType") FileUpload.UploadType uploadType,
            @RequestParam("referenceType") FileUpload.ReferenceType referenceType,
            @RequestParam("referenceId") Long referenceId) {
        
        try {
            log.info("파일 업로드 요청: originalName={}, size={}, contentType={}", 
                    file.getOriginalFilename(), file.getSize(), file.getContentType());
            
            FileUpload fileUpload;
            
            if (uploadType == FileUpload.UploadType.IMAGE) {
                // 이미지 파일인 경우 최적화 처리
                fileUpload = fileUploadApplicationService.uploadAndOptimizeImage(file, referenceType, referenceId);
            } else {
                // 일반 파일 업로드
                fileUpload = fileUploadApplicationService.uploadFile(file, uploadType, referenceType, referenceId);
            }
            
            FileUploadResponse response = FileUploadResponse.from(fileUpload);
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(FileUploadResponse.error("파일 업로드에 실패했습니다."));
        }
    }

    /**
     * 이미지 파일 업로드 및 최적화
     */
    @PostMapping("/upload/image")
    public ResponseEntity<FileUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("referenceType") FileUpload.ReferenceType referenceType,
            @RequestParam("referenceId") Long referenceId) {
        
        try {
            log.info("이미지 업로드 요청: originalName={}, size={}", 
                    file.getOriginalFilename(), file.getSize());
            
            FileUpload fileUpload = fileUploadApplicationService.uploadAndOptimizeImage(file, referenceType, referenceId);
            FileUploadResponse response = FileUploadResponse.from(fileUpload);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("이미지 업로드 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(FileUploadResponse.error("이미지 업로드에 실패했습니다."));
        }
    }

    /**
     * 파일 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<FileUploadResponse> getFile(@PathVariable Long id) {
        return fileUploadApplicationService.getFileById(id)
                .map(fileUpload -> ResponseEntity.ok(FileUploadResponse.from(fileUpload)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 참조별 파일 목록 조회
     */
    @GetMapping("/reference/{referenceType}/{referenceId}")
    public ResponseEntity<List<FileUploadResponse>> getFilesByReference(
            @PathVariable FileUpload.ReferenceType referenceType,
            @PathVariable Long referenceId,
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        
        List<FileUpload> files;
        if (activeOnly) {
            files = fileUploadApplicationService.getActiveFilesByReference(referenceType, referenceId);
        } else {
            files = fileUploadApplicationService.getFilesByReference(referenceType, referenceId);
        }
        
        List<FileUploadResponse> responses = files.stream()
                .map(FileUploadResponse::from)
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 파일 다운로드 URL 생성
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<FileDownloadResponse> generateDownloadUrl(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean useOriginalName) {
        
        return fileUploadApplicationService.getFileById(id)
                .map(fileUpload -> {
                    String fileName = useOriginalName ? fileUpload.getOriginalFileName() : fileUpload.getStoredFileName();
                    String downloadUrl = fileUploadApplicationService.generateDownloadUrl(fileUpload.getFilePath(), fileName);
                    
                    FileDownloadResponse response = FileDownloadResponse.builder()
                            .fileId(id)
                            .fileName(fileName)
                            .downloadUrl(downloadUrl)
                            .build();
                    
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 파일 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        try {
            fileUploadApplicationService.deleteFile(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("파일 삭제 실패: fileId={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 파일 비활성화
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateFile(@PathVariable Long id) {
        try {
            fileUploadApplicationService.deactivateFile(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("파일 비활성화 실패: fileId={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 이미지 리사이징
     */
    @PostMapping("/{id}/resize")
    public ResponseEntity<FileUploadResponse> resizeImage(
            @PathVariable Long id,
            @RequestParam int width,
            @RequestParam int height) {
        
        try {
            return fileUploadApplicationService.getFileById(id)
                    .map(fileUpload -> {
                        try {
                            // 이미지 데이터를 가져와서 리사이징
                            byte[] resizedData = fileUploadApplicationService.resizeImage(
                                    fileUpload.getFileSize().intValue(), width, height);
                            
                            // 리사이징된 이미지를 새로운 파일로 저장
                            // TODO: 리사이징된 이미지 저장 로직 구현
                            
                            return ResponseEntity.ok(FileUploadResponse.from(fileUpload));
                        } catch (IOException e) {
                            log.error("이미지 리사이징 실패", e);
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(FileUploadResponse.error("이미지 리사이징에 실패했습니다."));
                        }
                    })
                    .orElse(ResponseEntity.notFound().build());
                    
        } catch (Exception e) {
            log.error("이미지 리사이징 실패: fileId={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(FileUploadResponse.error("이미지 리사이징에 실패했습니다."));
        }
    }
} 