package com.sgyj.popupmoah.infra.config.exceptions;

/**
 * 회원가입 검증 실패
 * 이미 가입된 정보가 있을 경우 에러 발생
 */
public class AlreadyExistInfoException extends UserException {

    public AlreadyExistInfoException(String message) {
        super(message);
    }
}
