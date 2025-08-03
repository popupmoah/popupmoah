package com.sgyj.popupmoah.adapter.web.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 파일 다운로드 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDownloadResponse {
    
    private Long fileId;
    private String fileName;
    private String downloadUrl;
    private Long expiresIn; // URL 만료 시간 (초)
} 