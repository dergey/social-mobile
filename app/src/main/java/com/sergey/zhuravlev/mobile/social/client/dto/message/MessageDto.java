package com.sergey.zhuravlev.mobile.social.client.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sergey.zhuravlev.mobile.social.enums.MessageSenderType;
import com.sergey.zhuravlev.mobile.social.enums.MessageType;

import java.time.LocalDateTime;
import java.util.Objects;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextMessageDto.class, name = "TEXT"),
        @JsonSubTypes.Type(value = ImageMessageDto.class, name = "IMAGE"),
        @JsonSubTypes.Type(value = StickerMessageDto.class, name = "STICKER")
})
public abstract class MessageDto {

    private Long id;

    private MessageType type;

    private MessageSenderType sender;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    @JsonProperty("isRead")
    private boolean read;

    public MessageDto() {
    }

    public MessageDto(Long id, MessageType type, MessageSenderType sender, LocalDateTime createAt, LocalDateTime updateAt, boolean read) {
        this.id = id;
        this.type = type;
        this.sender = sender;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.read = read;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageDto that = (MessageDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(createAt, that.createAt) &&
                Objects.equals(updateAt, that.updateAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createAt, updateAt);
    }

}
