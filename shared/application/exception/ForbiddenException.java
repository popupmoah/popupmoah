package com.sgyj.popupmoah.shared.application.exception;

/**
 * 권한이 없을 때 발생하는 예외
 */
public class ForbiddenException extends BusinessException {

    public ForbiddenException(String message) {
        super("FORBIDDEN", message, 403);
    }

    public ForbiddenException() {
        super("FORBIDDEN", "접근 권한이 없습니다.", 403);
    }
}



