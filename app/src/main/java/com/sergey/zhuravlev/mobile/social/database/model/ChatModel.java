package com.sergey.zhuravlev.mobile.social.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.sergey.zhuravlev.mobile.social.database.converter.LocalDateTimeConverter;
import com.sergey.zhuravlev.mobile.social.database.paggeble.Pageable;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity(tableName = "chats")
public class ChatModel implements Parcelable {

    @PrimaryKey
    private long id;

    @Embedded(prefix = "profile_")
    private ProfilePreviewEmbeddable targetProfile;

    @ColumnInfo(name = "create_at")
    private LocalDateTime createAt;

    @ColumnInfo(name = "update_at")
    private LocalDateTime updateAt;

    @ColumnInfo(name = "message_allow")
    private boolean messageAllow;

    @ColumnInfo(name = "unread_messages")
    private Long unreadMessages;

    @ColumnInfo(name = "last_message_id")
    private Long lastMessageId;

    @Embedded(prefix = "pageable_")
    private Pageable pageable;

    public ChatModel() {
    }

    @Ignore
    public ChatModel(long id,
                     ProfilePreviewEmbeddable targetProfile,
                     LocalDateTime createAt,
                     LocalDateTime updateAt,
                     boolean messageAllow,
                     Long unreadMessages,
                     Long lastMessageId,
                     Pageable pageable) {
        this.id = id;
        this.targetProfile = targetProfile;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.messageAllow = messageAllow;
        this.unreadMessages = unreadMessages;
        this.lastMessageId = lastMessageId;
        this.pageable = pageable;
    }

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

    public Long getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(Long unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public Long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
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
        ChatModel chatModel = (ChatModel) o;
        return id == chatModel.id &&
                messageAllow == chatModel.messageAllow &&
                lastMessageId.equals(chatModel.lastMessageId) &&
                targetProfile.equals(chatModel.targetProfile) &&
                createAt.equals(chatModel.createAt) &&
                updateAt.equals(chatModel.updateAt) &&
                unreadMessages.equals(chatModel.unreadMessages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, targetProfile, createAt, updateAt, unreadMessages, messageAllow, lastMessageId);
    }

    @Ignore
    @Override
    public int describeContents() {
        return 0;
    }

    @Ignore
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeTypedObject(targetProfile, 0);
        dest.writeLong(LocalDateTimeConverter.toEpochMilli(createAt));
        dest.writeLong(LocalDateTimeConverter.toEpochMilli(updateAt));
        dest.writeBoolean(messageAllow);
        dest.writeLong(unreadMessages);
        dest.writeLong(lastMessageId);
        dest.writeTypedObject(pageable, 0);
    }

    public static final Parcelable.Creator<ChatModel> CREATOR = new Parcelable.Creator<>() {

        public ChatModel createFromParcel(Parcel in) {
            long id = in.readLong();
            ProfilePreviewEmbeddable profile = in.readTypedObject(ProfilePreviewEmbeddable.CREATOR);
            LocalDateTime createAt = LocalDateTimeConverter.fromEpochMilli(in.readLong());
            LocalDateTime updateAt = LocalDateTimeConverter.fromEpochMilli(in.readLong());
            boolean messageAllow = in.readBoolean();
            long unreadMessages = in.readLong();
            Long lastMessageId = in.readLong();
            Pageable pageable = in.readTypedObject(Pageable.CREATOR);
            return new ChatModel(id, profile, createAt, updateAt, messageAllow, unreadMessages, lastMessageId, pageable);
        }

        public ChatModel[] newArray(int size) {
            return new ChatModel[size];
        }

    };

}

