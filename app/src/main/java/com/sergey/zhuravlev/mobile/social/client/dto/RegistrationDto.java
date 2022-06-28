package com.sergey.zhuravlev.mobile.social.client.dto;

import java.time.LocalDate;

public class RegistrationDto {

    private String email;

    private String password;

    private String username;

    private String firstName;

    private String middleName;

    private String secondName;

    private LocalDate birthDate;

    public RegistrationDto() {
    }

    public RegistrationDto(String email, String password, String username, String firstName, String middleName, String secondName, LocalDate birthDate) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.firstName = firstName;
        this.middleName = middleName;
        this.secondName = secondName;
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
