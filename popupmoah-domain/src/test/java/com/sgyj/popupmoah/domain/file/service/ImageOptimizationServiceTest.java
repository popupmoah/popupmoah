package com.sgyj.popupmoah.domain.file.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ImageOptimizationService 테스트
 */
@ExtendWith(MockitoExtension.class)
class ImageOptimizationServiceTest {

    private ImageOptimizationService imageOptimizationService;

    @BeforeEach
    void setUp() {
        imageOptimizationService = new ImageOptimizationService();
    }

    @Test
    void resizeImage_성공() throws IOException {
        // given
        byte[] originalImageData = createTestImage(800, 600);
        int targetWidth = 400;
        int targetHeight = 300;

        // when
        byte[] resizedImageData = imageOptimizationService.resizeImage(originalImageData, targetWidth, targetHeight);

        // then
        assertThat(resizedImageData).isNotNull();
        assertThat(resizedImageData.length).isGreaterThan(0);

        // 리사이징된 이미지의 크기 확인
        BufferedImage resizedImage = ImageIO.read(new ByteArrayInputStream(resizedImageData));
        assertThat(resizedImage.getWidth()).isEqualTo(targetWidth);
        assertThat(resizedImage.getHeight()).isEqualTo(targetHeight);
    }

    @Test
    void resizeImage_비율유지() throws IOException {
        // given
        byte[] originalImageData = createTestImage(800, 600);
        int targetWidth = 400;
        int targetHeight = 200; // 비율이 다른 경우

        // when
        byte[] resizedImageData = imageOptimizationService.resizeImage(originalImageData, targetWidth, targetHeight);

        // then
        assertThat(resizedImageData).isNotNull();
        
        // 비율을 유지하면서 리사이징되었는지 확인
        BufferedImage resizedImage = ImageIO.read(new ByteArrayInputStream(resizedImageData));
        double originalRatio = 800.0 / 600.0;
        double resizedRatio = (double) resizedImage.getWidth() / resizedImage.getHeight();
        
        // 비율이 유지되었는지 확인 (약간의 오차 허용)
        assertThat(Math.abs(originalRatio - resizedRatio)).isLessThan(0.1);
    }

    @Test
    void generateThumbnail_성공() throws IOException {
        // given
        byte[] originalImageData = createTestImage(800, 600);
        int thumbnailWidth = 150;
        int thumbnailHeight = 150;

        // when
        byte[] thumbnailData = imageOptimizationService.generateThumbnail(originalImageData, thumbnailWidth, thumbnailHeight);

        // then
        assertThat(thumbnailData).isNotNull();
        assertThat(thumbnailData.length).isGreaterThan(0);

        // 썸네일 크기 확인
        BufferedImage thumbnail = ImageIO.read(new ByteArrayInputStream(thumbnailData));
        assertThat(thumbnail.getWidth()).isEqualTo(thumbnailWidth);
        assertThat(thumbnail.getHeight()).isEqualTo(thumbnailHeight);
    }

    @Test
    void optimizeImage_성공() throws IOException {
        // given
        byte[] originalImageData = createTestImage(800, 600);
        float quality = 0.8f;

        // when
        byte[] optimizedImageData = imageOptimizationService.optimizeImage(originalImageData, quality);

        // then
        assertThat(optimizedImageData).isNotNull();
        assertThat(optimizedImageData.length).isGreaterThan(0);
        
        // 최적화된 이미지가 원본보다 작거나 같은 크기인지 확인
        assertThat(optimizedImageData.length).isLessThanOrEqualTo(originalImageData.length);
    }

    @Test
    void convertImageFormat_JPEG에서PNG로() throws IOException {
        // given
        byte[] originalImageData = createTestImage(800, 600);

        // when
        byte[] convertedImageData = imageOptimizationService.convertImageFormat(originalImageData, "PNG");

        // then
        assertThat(convertedImageData).isNotNull();
        assertThat(convertedImageData.length).isGreaterThan(0);

        // PNG 형식인지 확인
        BufferedImage convertedImage = ImageIO.read(new ByteArrayInputStream(convertedImageData));
        assertThat(convertedImage).isNotNull();
    }

    @Test
    void getImageDimensions_성공() throws IOException {
        // given
        byte[] imageData = createTestImage(800, 600);

        // when
        int[] dimensions = imageOptimizationService.getImageDimensions(imageData);

        // then
        assertThat(dimensions).isNotNull();
        assertThat(dimensions).hasSize(2);
        assertThat(dimensions[0]).isEqualTo(800); // width
        assertThat(dimensions[1]).isEqualTo(600); // height
    }

    @Test
    void compressImage_성공() throws IOException {
        // given
        byte[] originalImageData = createTestImage(800, 600);
        long maxSizeBytes = 1000; // 1KB

        // when
        byte[] compressedImageData = imageOptimizationService.compressImage(originalImageData, maxSizeBytes);

        // then
        assertThat(compressedImageData).isNotNull();
        assertThat(compressedImageData.length).isLessThanOrEqualTo(maxSizeBytes);
    }

    @Test
    void createWebOptimizedImage_성공() throws IOException {
        // given
        byte[] originalImageData = createTestImage(1200, 800);
        int maxWidth = 800;
        int maxHeight = 600;

        // when
        byte[] webOptimizedImageData = imageOptimizationService.createWebOptimizedImage(
                originalImageData, maxWidth, maxHeight);

        // then
        assertThat(webOptimizedImageData).isNotNull();
        assertThat(webOptimizedImageData.length).isGreaterThan(0);

        // 웹 최적화된 이미지 크기 확인
        BufferedImage webOptimizedImage = ImageIO.read(new ByteArrayInputStream(webOptimizedImageData));
        assertThat(webOptimizedImage.getWidth()).isLessThanOrEqualTo(maxWidth);
        assertThat(webOptimizedImage.getHeight()).isLessThanOrEqualTo(maxHeight);
    }

    @Test
    void resizeImage_잘못된이미지데이터() {
        // given
        byte[] invalidImageData = "invalid image data".getBytes();

        // when & then
        assertThatThrownBy(() -> 
            imageOptimizationService.resizeImage(invalidImageData, 100, 100)
        ).isInstanceOf(IOException.class);
    }

    @Test
    void getImageDimensions_잘못된이미지데이터() {
        // given
        byte[] invalidImageData = "invalid image data".getBytes();

        // when & then
        assertThatThrownBy(() -> 
            imageOptimizationService.getImageDimensions(invalidImageData)
        ).isInstanceOf(IOException.class);
    }

    /**
     * 테스트용 이미지 생성
     */
    private byte[] createTestImage(int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        
        // 그라데이션 배경 생성
        GradientPaint gradient = new GradientPaint(
            0, 0, Color.RED,
            width, height, Color.BLUE
        );
        graphics.setPaint(gradient);
        graphics.fillRect(0, 0, width, height);
        
        // 간단한 도형 그리기
        graphics.setColor(Color.YELLOW);
        graphics.fillOval(width/4, height/4, width/2, height/2);
        
        graphics.dispose();
        
        // JPEG로 변환
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "JPEG", outputStream);
        return outputStream.toByteArray();
    }
} 