package com.sgyj.popupmoah.infra.config.exceptions;

/**
 * 네이버 로그인 인증 실패
 */
public class NaverCallBackException extends UserException {

    public NaverCallBackException(String message) {
        super(message);
    }
}
