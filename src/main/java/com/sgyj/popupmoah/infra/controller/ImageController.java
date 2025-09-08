package com.sgyj.popupmoah.infra.controller;

import com.sgyj.popupmoah.infra.service.ImageCdnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 이미지 업로드 및 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageCdnService imageCdnService;

    /**
     * 이미지 업로드
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", defaultValue = "general") String category) {
        
        try {
            log.info("이미지 업로드 요청: category={}, filename={}", category, file.getOriginalFilename());
            
            String imagePath = imageCdnService.uploadImage(file, category);
            String imageUrl = imageCdnService.getImageUrl(imagePath);
            String thumbnailUrl = imageCdnService.getThumbnailUrl(imagePath);
            
            Map<String, String> response = new HashMap<>();
            response.put("imagePath", imagePath);
            response.put("imageUrl", imageUrl);
            response.put("thumbnailUrl", thumbnailUrl);
            response.put("message", "이미지 업로드가 완료되었습니다.");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("이미지 업로드 실패: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 이미지 URL 조회
     */
    @GetMapping("/url")
    public ResponseEntity<Map<String, String>> getImageUrl(@RequestParam("path") String imagePath) {
        try {
            String imageUrl = imageCdnService.getImageUrl(imagePath);
            String thumbnailUrl = imageCdnService.getThumbnailUrl(imagePath);
            
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("thumbnailUrl", thumbnailUrl);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("이미지 URL 조회 실패: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 이미지 삭제
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteImage(@RequestParam("path") String imagePath) {
        try {
            imageCdnService.deleteImage(imagePath);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "이미지가 삭제되었습니다.");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("이미지 삭제 실패: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
