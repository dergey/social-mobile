package com.sergey.zhuravlev.mobile.social.dto;

import com.sergey.zhuravlev.mobile.social.dto.enums.ErrorCode;

import java.util.Collection;

public class ErrorDto {

    public static class FieldError {
        private String field;
        private String message;

        public FieldError() {
        }

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    private ErrorCode code;
    private String message;
    private Collection<FieldError> fields;

    public ErrorDto() {
    }

    public ErrorDto(ErrorCode code, String message, Collection<FieldError> fields) {
        this.code = code;
        this.message = message;
        this.fields = fields;
    }

    public ErrorCode getCode() {
        return code;
    }

    public void setCode(ErrorCode code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Collection<FieldError> getFields() {
        return fields;
    }

    public void setFields(Collection<FieldError> fields) {
        this.fields = fields;
    }
}
