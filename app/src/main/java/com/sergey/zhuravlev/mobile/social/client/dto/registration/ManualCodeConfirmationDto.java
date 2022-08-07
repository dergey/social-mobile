package com.sergey.zhuravlev.mobile.social.client.dto.registration;

import java.util.UUID;

public class ManualCodeConfirmationDto {

    private String continuationCode;

    private String manualCode;

    public String getContinuationCode() {
        return continuationCode;
    }

    public void setContinuationCode(String continuationCode) {
        this.continuationCode = continuationCode;
    }

    public String getManualCode() {
        return manualCode;
    }

    public void setManualCode(String manualCode) {
        this.manualCode = manualCode;
    }
}
