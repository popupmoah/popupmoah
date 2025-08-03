package com.sgyj.popupmoah.infra.config.exceptions;

/**
 * 사용자 에러
 */
public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }
}
