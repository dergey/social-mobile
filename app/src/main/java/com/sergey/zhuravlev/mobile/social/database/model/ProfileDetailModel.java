package com.sergey.zhuravlev.mobile.social.database.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.sergey.zhuravlev.mobile.social.enums.RelationshipStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(tableName = "profile_details")
public class ProfileDetailModel {

    @PrimaryKey
    @NonNull
    private String username;

    @Ignore
    private LocalDateTime lastSeen;

    private String overview;

    @ColumnInfo(name = "relationship_status")
    private RelationshipStatus relationshipStatus;

    private String city;

    private String workplace;

    private String education;

    @ColumnInfo(name = "birth_date")
    private LocalDate birthDate;

    @ColumnInfo(name = "create_at")
    private LocalDateTime createAt;

    @ColumnInfo(name = "update_at")
    private LocalDateTime updateAt;

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public RelationshipStatus getRelationshipStatus() {
        return relationshipStatus;
    }

    public void setRelationshipStatus(RelationshipStatus relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
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
