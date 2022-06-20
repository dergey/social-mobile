package com.sergey.zhuravlev.mobile.social.client.dto.profile;

import com.sergey.zhuravlev.mobile.social.client.dto.image.ImageDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

public class ProfileDetailDto {

    private String username;
    private ImageDto avatar;
    private Collection<String> images;
    private String firstName;
    private String middleName;
    private String secondName;
    private LocalDate birthDate;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public ProfileDetailDto() {
    }

    public ProfileDetailDto(String username, ImageDto avatar, Collection<String> images, String firstName, String middleName, String secondName, LocalDate birthDate, LocalDateTime createAt, LocalDateTime updateAt) {
        this.username = username;
        this.avatar = avatar;
        this.images = images;
        this.firstName = firstName;
        this.middleName = middleName;
        this.secondName = secondName;
        this.birthDate = birthDate;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ImageDto getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageDto avatar) {
        this.avatar = avatar;
    }

    public Collection<String> getImages() {
        return images;
    }

    public void setImages(Collection<String> images) {
        this.images = images;
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

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

}
