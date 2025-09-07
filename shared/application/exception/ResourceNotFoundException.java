package com.sgyj.popupmoah.shared.application.exception;

/**
 * 리소스를 찾을 수 없을 때 발생하는 예외
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message, 404);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super("RESOURCE_NOT_FOUND", 
              String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue), 
              404);
    }
}



