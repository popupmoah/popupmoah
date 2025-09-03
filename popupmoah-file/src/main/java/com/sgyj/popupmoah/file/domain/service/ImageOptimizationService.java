package com.sgyj.popupmoah.file.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 이미지 최적화 서비스
 * 이미지 리사이징, 썸네일 생성, 포맷 변환, 품질 최적화 기능을 제공합니다.
 */
@Slf4j
@Service
public class ImageOptimizationService {
    
    /**
     * 이미지 크기를 조정합니다.
     */
    public byte[] resizeImage(byte[] imageData, int targetWidth, int targetHeight) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData)) {
            BufferedImage originalImage = ImageIO.read(inputStream);
            
            if (originalImage == null) {
                throw new IOException("이미지를 읽을 수 없습니다.");
            }
            
            // 비율을 유지하면서 크기 조정
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            
            double scaleX = (double) targetWidth / originalWidth;
            double scaleY = (double) targetHeight / originalHeight;
            double scale = Math.min(scaleX, scaleY);
            
            int newWidth = (int) (originalWidth * scale);
            int newHeight = (int) (originalHeight * scale);
            
            // 고품질 리사이징
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = resizedImage.createGraphics();
            
            // 렌더링 품질 설정
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            graphics.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            graphics.dispose();
            
            // 바이트 배열로 변환
            return convertToByteArray(resizedImage, "JPEG");
        }
    }
    
    /**
     * 썸네일을 생성합니다.
     */
    public byte[] generateThumbnail(byte[] imageData, int thumbnailWidth, int thumbnailHeight) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData)) {
            BufferedImage originalImage = ImageIO.read(inputStream);
            
            if (originalImage == null) {
                throw new IOException("이미지를 읽을 수 없습니다.");
            }
            
            // 정사각형 썸네일 생성
            int size = Math.min(thumbnailWidth, thumbnailHeight);
            
            // 중앙을 기준으로 크롭
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            
            int cropSize = Math.min(originalWidth, originalHeight);
            int cropX = (originalWidth - cropSize) / 2;
            int cropY = (originalHeight - cropSize) / 2;
            
            BufferedImage croppedImage = originalImage.getSubimage(cropX, cropY, cropSize, cropSize);
            
            // 썸네일 크기로 리사이징
            BufferedImage thumbnail = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = thumbnail.createGraphics();
            
            // 렌더링 품질 설정
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            graphics.drawImage(croppedImage, 0, 0, size, size, null);
            graphics.dispose();
            
            // 바이트 배열로 변환
            return convertToByteArray(thumbnail, "JPEG");
        }
    }
    
    /**
     * 이미지 포맷을 변환합니다.
     */
    public byte[] convertImageFormat(byte[] imageData, String format) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData)) {
            BufferedImage image = ImageIO.read(inputStream);
            
            if (image == null) {
                throw new IOException("이미지를 읽을 수 없습니다.");
            }
            
            return convertToByteArray(image, format.toUpperCase());
        }
    }
    
    /**
     * 이미지 품질을 최적화합니다.
     */
    public byte[] optimizeImage(byte[] imageData, float quality) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData)) {
            BufferedImage image = ImageIO.read(inputStream);
            
            if (image == null) {
                throw new IOException("이미지를 읽을 수 없습니다.");
            }
            
            // 품질 설정을 위한 ImageWriter 사용
            return convertToByteArrayWithQuality(image, "JPEG", quality);
        }
    }
    
    /**
     * 이미지 크기 정보를 가져옵니다.
     */
    public int[] getImageDimensions(byte[] imageData) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData)) {
            BufferedImage image = ImageIO.read(inputStream);
            
            if (image == null) {
                throw new IOException("이미지를 읽을 수 없습니다.");
            }
            
            return new int[]{image.getWidth(), image.getHeight()};
        }
    }
    
    /**
     * 이미지를 바이트 배열로 변환합니다.
     */
    private byte[] convertToByteArray(BufferedImage image, String format) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, outputStream);
            return outputStream.toByteArray();
        }
    }
    
    /**
     * 품질 설정을 포함하여 이미지를 바이트 배열로 변환합니다.
     */
    private byte[] convertToByteArrayWithQuality(BufferedImage image, String format, float quality) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // JPEG 품질 설정
            if ("JPEG".equalsIgnoreCase(format)) {
                javax.imageio.ImageWriter writer = ImageIO.getImageWritersByFormatName("JPEG").next();
                javax.imageio.ImageWriteParam param = writer.getDefaultWriteParam();
                
                if (param.canWriteCompressed()) {
                    param.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionQuality(quality);
                }
                
                javax.imageio.stream.ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);
                writer.setOutput(imageOutputStream);
                writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
                writer.dispose();
                imageOutputStream.close();
            } else {
                ImageIO.write(image, format, outputStream);
            }
            
            return outputStream.toByteArray();
        }
    }
    
    /**
     * 이미지가 유효한지 확인합니다.
     */
    public boolean isValidImage(byte[] imageData) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData)) {
            BufferedImage image = ImageIO.read(inputStream);
            return image != null;
        } catch (IOException e) {
            log.warn("이미지 유효성 검사 실패", e);
            return false;
        }
    }
    
    /**
     * 이미지 파일 크기를 줄입니다.
     */
    public byte[] compressImage(byte[] imageData, long maxSizeBytes) throws IOException {
        byte[] compressedData = imageData;
        float quality = 0.9f;
        
        while (compressedData.length > maxSizeBytes && quality > 0.1f) {
            compressedData = optimizeImage(imageData, quality);
            quality -= 0.1f;
        }
        
        return compressedData;
    }
    
    /**
     * 웹 최적화 이미지를 생성합니다.
     */
    public byte[] createWebOptimizedImage(byte[] imageData, int maxWidth, int maxHeight) throws IOException {
        // 이미지 크기 조정
        byte[] resizedData = resizeImage(imageData, maxWidth, maxHeight);
        
        // 품질 최적화 (웹용으로 적절한 품질 설정)
        return optimizeImage(resizedData, 0.8f);
    }
} 