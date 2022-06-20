package com.sergey.zhuravlev.mobile.social.database.model;

import androidx.room.ColumnInfo;

import com.sergey.zhuravlev.mobile.social.enums.MessageSenderType;
import com.sergey.zhuravlev.mobile.social.enums.MessageType;

import java.time.LocalDateTime;

public class MessageEmbeddable {

    private Long id;

    private MessageType type;

    private MessageSenderType sender;

    private String text;

    @ColumnInfo(name = "create_at")
    private LocalDateTime createAt;

    @ColumnInfo(name = "update_at")
    private LocalDateTime updateAt;

    private boolean read;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public MessageSenderType getSender() {
        return sender;
    }

    public void setSender(MessageSenderType sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
