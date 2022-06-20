package com.sergey.zhuravlev.mobile.social.client.dto.chat;

import com.sergey.zhuravlev.mobile.social.client.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDto;

import java.time.LocalDateTime;
import java.util.Objects;

public class ChatPreviewDto {

    private Long id;

    private ProfileDto targetProfile;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private boolean messageAllow;

    private MessageDto lastMessage;

    public ChatPreviewDto() {
    }

    public ChatPreviewDto(Long id, ProfileDto targetProfile, LocalDateTime createAt, LocalDateTime updateAt, boolean messageAllow, MessageDto lastMessage) {
        this.id = id;
        this.targetProfile = targetProfile;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.messageAllow = messageAllow;
        this.lastMessage = lastMessage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProfileDto getTargetProfile() {
        return targetProfile;
    }

    public void setTargetProfile(ProfileDto targetProfile) {
        this.targetProfile = targetProfile;
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

    public boolean isMessageAllow() {
        return messageAllow;
    }

    public void setMessageAllow(boolean messageAllow) {
        this.messageAllow = messageAllow;
    }

    public MessageDto getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageDto lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatPreviewDto that = (ChatPreviewDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(createAt, that.createAt) &&
                Objects.equals(updateAt, that.updateAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createAt, updateAt);
    }

}
