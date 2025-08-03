package com.sgyj.popupmoah.infra.config.exceptions;

/**
 * 패스워드 불일치
 */
public class PasswordMismatchException extends UserException {

    public PasswordMismatchException(String message) {
        super(message);
    }
}
