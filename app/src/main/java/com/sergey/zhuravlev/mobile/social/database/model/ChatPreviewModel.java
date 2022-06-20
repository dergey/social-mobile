package com.sergey.zhuravlev.mobile.social.database.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.sergey.zhuravlev.mobile.social.client.dto.chat.ChatPreviewDto;
import com.sergey.zhuravlev.mobile.social.database.paggeble.Pageable;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity(tableName = "chat_previews")
public class ChatPreviewModel {

    @PrimaryKey(autoGenerate = false)
    private long id;

    @Embedded(prefix = "profile_")
    private ProfilePreviewEmbeddable targetProfile;

    @ColumnInfo(name = "create_at")
    private LocalDateTime createAt;

    @ColumnInfo(name = "update_at")
    private LocalDateTime updateAt;

    @ColumnInfo(name = "message_allow")
    private boolean messageAllow;

    @Embedded(prefix = "message_")
    private MessageEmbeddable lastMessage;

    @Embedded(prefix = "pageable_")
    private Pageable pageable;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProfilePreviewEmbeddable getTargetProfile() {
        return targetProfile;
    }

    public void setTargetProfile(ProfilePreviewEmbeddable targetProfile) {
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

    public MessageEmbeddable getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageEmbeddable lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatPreviewModel model = (ChatPreviewModel) o;
        return id == model.id && createAt.equals(model.createAt) && updateAt.equals(model.updateAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createAt, updateAt);
    }

}

