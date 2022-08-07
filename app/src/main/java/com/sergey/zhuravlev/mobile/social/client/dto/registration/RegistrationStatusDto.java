package com.sergey.zhuravlev.mobile.social.client.dto.registration;

import com.sergey.zhuravlev.mobile.social.enums.RegistrationStatus;

import java.util.UUID;

public class RegistrationStatusDto {

    private RegistrationStatus status;

    private UUID continuationCode;

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public UUID getContinuationCode() {
        return continuationCode;
    }

    public void setContinuationCode(UUID continuationCode) {
        this.continuationCode = continuationCode;
    }
}
