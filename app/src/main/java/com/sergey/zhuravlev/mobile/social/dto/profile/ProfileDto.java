package com.sergey.zhuravlev.mobile.social.dto.profile;

import java.util.Objects;

public class ProfileDto {

    private String username;
    private String firstName;
    private String middleName;
    private String secondName;

    public ProfileDto() {
    }

    public ProfileDto(String username, String firstName, String middleName, String secondName) {
        this.username = username;
        this.firstName = firstName;
        this.middleName = middleName;
        this.secondName = secondName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

}
