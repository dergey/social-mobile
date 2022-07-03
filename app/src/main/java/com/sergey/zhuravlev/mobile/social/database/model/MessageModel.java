package com.sergey.zhuravlev.mobile.social.database.model;

import android.net.Uri;

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

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "network_id")
    private Long networkId;

    @ColumnInfo(name = "chat_id")
    private Long chatId;

    private MessageType type;

    private MessageSenderType sender;

    private String text;

    @ColumnInfo(name = "glide_signature")
    private String glideSignature;

    @ColumnInfo(name = "create_at")
    private LocalDateTime createAt;

    @ColumnInfo(name = "update_at")
    private LocalDateTime updateAt;

    private boolean read;

    private boolean prepend;

    @ColumnInfo(name = "prepend_error")
    private boolean prependError;

    @Embedded(prefix = "pageable_")
    private Pageable pageable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
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

    public String getGlideSignature() {
        return glideSignature;
    }

    public void setGlideSignature(String glideSignature) {
        this.glideSignature = glideSignature;
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

    public boolean isPrepend() {
        return prepend;
    }

    public void setPrepend(boolean prepend) {
        this.prepend = prepend;
    }

    public boolean isPrependError() {
        return prependError;
    }

    public void setPrependError(boolean prependError) {
        this.prependError = prependError;
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
        return prepend == that.prepend && prependError == that.prependError && id.equals(that.id) && Objects.equals(networkId, that.networkId) && createAt.equals(that.createAt) && updateAt.equals(that.updateAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, networkId, createAt, updateAt, prepend, prependError);
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "id=" + id +
                ", networkId=" + networkId +
                ", chatId=" + chatId +
                ", type=" + type +
                ", sender=" + sender +
                ", text='" + text + '\'' +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                ", read=" + read +
                ", prepend=" + prepend +
                ", prependError=" + prependError +
                ", pageable=" + pageable +
                '}';
    }

}
