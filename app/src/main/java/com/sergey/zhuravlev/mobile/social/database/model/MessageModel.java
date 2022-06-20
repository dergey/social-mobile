package com.sergey.zhuravlev.mobile.social.database.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.sergey.zhuravlev.mobile.social.database.paggeble.Pageable;
import com.sergey.zhuravlev.mobile.social.enums.MessageSenderType;
import com.sergey.zhuravlev.mobile.social.enums.MessageType;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity(tableName = "messages")
public class MessageModel {

    @PrimaryKey(autoGenerate = false)
    private Long id;

    @ColumnInfo(name = "chat_id")
    private Long chatId;

    private MessageType type;

    private MessageSenderType sender;

    private String text;

    @ColumnInfo(name = "create_at")
    private LocalDateTime createAt;

    @ColumnInfo(name = "update_at")
    private LocalDateTime updateAt;

    private boolean read;

    @Embedded(prefix = "pageable_")
    private Pageable pageable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
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
        MessageModel that = (MessageModel) o;
        return id.equals(that.id) && createAt.equals(that.createAt) && updateAt.equals(that.updateAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createAt, updateAt);
    }

}
