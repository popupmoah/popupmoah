package com.sgyj.popupmoah.shared.application.exception;

/**
 * 인증되지 않은 접근 시 발생하는 예외
 */
public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message, 401);
    }

    public UnauthorizedException() {
        super("UNAUTHORIZED", "인증이 필요합니다.", 401);
    }
}



