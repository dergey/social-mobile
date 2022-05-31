package com.sergey.zhuravlev.mobile.social.dto.chat;

import com.sergey.zhuravlev.mobile.social.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.dto.profile.ProfileDto;

import java.time.LocalDateTime;
import java.util.Collection;

public class ChatDto {

    private Long id;

    private ProfileDto targetProfile;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private boolean messageAllow;

    private Collection<MessageDto> lastMessages;

    public ChatDto() {
    }

    public ChatDto(Long id, ProfileDto targetProfile, LocalDateTime createAt, LocalDateTime updateAt, boolean messageAllow, Collection<MessageDto> lastMessages) {
        this.id = id;
        this.targetProfile = targetProfile;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.messageAllow = messageAllow;
        this.lastMessages = lastMessages;
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

    public Collection<MessageDto> getLastMessages() {
        return lastMessages;
    }

    public void setLastMessages(Collection<MessageDto> lastMessages) {
        this.lastMessages = lastMessages;
    }

}
