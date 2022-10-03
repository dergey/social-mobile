package com.sergey.zhuravlev.mobile.social.client.dto.registration;

import com.sergey.zhuravlev.mobile.social.enums.Gender;

import java.time.LocalDate;
import java.util.UUID;

public class CompleteRegistrationDto {

    private String continuationCode;

    private String password;

    private String username;

    private String firstName;

    private String middleName;

    private String secondName;

    private Gender gender;

    private LocalDate birthDate;

    public String getContinuationCode() {
        return continuationCode;
    }

    public void setContinuationCode(String continuationCode) {
        this.continuationCode = continuationCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
