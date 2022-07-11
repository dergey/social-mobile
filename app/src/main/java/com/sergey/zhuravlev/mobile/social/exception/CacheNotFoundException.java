package com.sergey.zhuravlev.mobile.social.exception;

public class CacheNotFoundException extends RuntimeException {

    public CacheNotFoundException(String message) {
        super(message);
    }

    public CacheNotFoundException(Class<?> clazz) {
        super(String.format("Not found %s", clazz.getSimpleName()));
    }
}
