package com.sergey.zhuravlev.mobile.social.client.dto;

public class LoginResponseDto {
    private String jwtToken;

    public LoginResponseDto() {
    }

    public LoginResponseDto(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

}
