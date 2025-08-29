package com.sgyj.popupmoah.infra.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 특정 역할이 필요한 메서드나 클래스에 사용하는 어노테이션
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    
    /**
     * 필요한 역할 목록
     */
    String[] value() default {};
    
    /**
     * 모든 역할이 필요한지 여부 (AND 조건)
     * false인 경우 하나의 역할만 있으면 됨 (OR 조건)
     */
    boolean requireAll() default false;
}
