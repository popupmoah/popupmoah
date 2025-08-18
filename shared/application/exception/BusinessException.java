package com.sgyj.popupmoah.shared.application.exception;

import lombok.Getter;

/**
 * 비즈니스 로직 예외
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final int status;

    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
        this.status = 400;
    }

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.status = 400;
    }

    public BusinessException(String errorCode, String message, int status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.status = 400;
    }

    public BusinessException(String errorCode, String message, int status, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.status = status;
    }
}



