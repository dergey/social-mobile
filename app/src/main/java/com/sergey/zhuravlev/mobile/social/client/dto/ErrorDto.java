package com.sergey.zhuravlev.mobile.social.client.dto;

import com.sergey.zhuravlev.mobile.social.enums.ErrorCode;

import java.util.Collection;

public class ErrorDto {

    public static class FieldError {
        private String field;
        private String code;

        public FieldError() {
        }

        public FieldError(String field, String message) {
            this.field = field;
            this.code = message;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
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
