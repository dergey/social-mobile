package com.sergey.zhuravlev.mobile.social.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.sergey.zhuravlev.mobile.social.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.dto.LoginResponseDto;

import java.io.IOException;
import java.lang.reflect.Type;

import retrofit2.HttpException;

public abstract class Result<T, E> {

    public abstract boolean isSuccess();

    public final static class Success<T, E> extends Result<T, E> {
        private T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }

        @Override
        public boolean isSuccess() {
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

        public String getMessage() {
            return message;
        }

        @Nullable
        public String getErrorData() {
            return errorData;
        }

        public E getErrorObject(Class<E> clazz) {
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

    }

}