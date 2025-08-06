package com.sgyj.popupmoah.infrastructure.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AWS S3 Configuration
 * AmazonS3 클라이언트 빈을 설정합니다.
 */
@Slf4j
@Configuration
public class S3Configuration {

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {
        // 로컬 개발 환경에서는 기본 자격 증명 체인 사용
        if (accessKey.isEmpty() || secretKey.isEmpty()) {
            log.info("AWS 자격 증명이 설정되지 않았습니다. 기본 자격 증명 체인을 사용합니다.");
            return AmazonS3ClientBuilder.standard()
                    .withRegion(region)
                    .build();
        }

        // 명시적 자격 증명 사용
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
} 