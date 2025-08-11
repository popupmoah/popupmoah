package com.sgyj.popupmoah.core.util;

import com.sgyj.popupmoah.core.exception.BusinessException;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * 공통 유효성 검사 유틸리티
 */
public class ValidationUtils {

    /**
     * 객체가 null이 아닌지 확인합니다.
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new BusinessException(message, "VALIDATION_ERROR");
        }
    }

    /**
     * 문자열이 null이 아니고 비어있지 않은지 확인합니다.
     */
    public static void notBlank(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(message, "VALIDATION_ERROR");
        }
    }

    /**
     * 컬렉션이 null이 아니고 비어있지 않은지 확인합니다.
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(message, "VALIDATION_ERROR");
        }
    }

    /**
     * 조건이 참인지 확인합니다.
     */
    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new BusinessException(message, "VALIDATION_ERROR");
        }
    }

    /**
     * 조건이 거짓인지 확인합니다.
     */
    public static void isFalse(boolean condition, String message) {
        if (condition) {
            throw new BusinessException(message, "VALIDATION_ERROR");
        }
    }
} 