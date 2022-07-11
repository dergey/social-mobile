package com.sergey.zhuravlev.mobile.social.ui.friend;

import java.util.Objects;

public class FriendRequestItem {

    private String username;
    private String firstName;
    private String middleName;
    private String secondName;
    private boolean accepted;
    private boolean rejected;

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

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendRequestItem that = (FriendRequestItem) o;
        return accepted == that.accepted
                && rejected == that.rejected
                && username.equals(that.username)
                && firstName.equals(that.firstName)
                && Objects.equals(middleName, that.middleName)
                && secondName.equals(that.secondName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, firstName, middleName, secondName, accepted, rejected);
    }


}
