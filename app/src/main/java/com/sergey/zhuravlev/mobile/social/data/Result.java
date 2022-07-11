package com.sergey.zhuravlev.mobile.social.data;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergey.zhuravlev.mobile.social.exception.CacheNotFoundException;

import java.io.IOException;
import java.util.function.Function;

import kotlin.jvm.functions.Function1;
import retrofit2.HttpException;

public abstract class Result<T, E> {

    public abstract boolean isSuccess();

    public abstract boolean isCache();

    public abstract <N> Result<N, E> map(Function<? super T, ? extends N> dataMapper);

    public final static class Success<T, E> extends Result<T, E> {
        private T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }

        @Override
        public <N> Result<N, E> map(Function<? super T, ? extends N> dataMapper) {
            return new Success<>(dataMapper.apply(data));
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isCache() {
            return false;
        }
    }

    public final static class CacheSuccess<T, E> extends Result<T, E> {
        private T data;

        public CacheSuccess(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }

        @Override
        public <N> Result<N, E> map(Function<? super T, ? extends N> dataMapper) {
            return new CacheSuccess<>(dataMapper.apply(data));
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isCache() {
            return true;
        }

    }

    public final static class Error<T, E> extends Result<T, E> {

        private static final ObjectMapper objectMapper = new ObjectMapper();

        private String message;
        private String errorData;

        public Error(String message) {
            this.message = message;
        }

        public Error(String message, String errorData) {
            this.message = message;
            this.errorData = errorData;
        }

        @Override
        public <N> Result<N, E> map(Function<? super T, ? extends N> dataMapper) {
            return new Error<>(message, errorData);
        }

        public String getMessage() {
            return message;
        }

        @Nullable
        public String getErrorData() {
            return errorData;
        }

        public E getErrorObject(Class<E> clazz) {
            if (errorData == null) {
                return null;
            }
            try {
                return objectMapper.readValue(errorData, clazz);
            } catch (JsonProcessingException e) {
                return null;
            }
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isCache() {
            return false;
        }

        public static <T, E> Result.Error<T, E> fromHttpException(HttpException e) {
            String errorData = null;
            try {
                if (e.response() != null && e.response().errorBody() != null) {
                    errorData = e.response().errorBody().string();
                }
            } catch (IOException ignored) {
            }
            if (errorData != null) {
                return new Error<>(String.format("Server send error response with status %s", e.code()), errorData);
            } else {
                return new Error<>(String.format("Server send error with status %s", e.code()));
            }
        }

        public static <T, E> Result.Error<T, E> fromIOException(IOException e) {
            return new Result.Error<T, E>("Connection error when requesting data to server");
        }

        public static <T, E> Result.Error<T, E> fromCacheException(CacheNotFoundException e) {
            return new Result.Error<T, E>(e.getMessage());
        }

    }

}