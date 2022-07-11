package com.sergey.zhuravlev.mobile.social.database.model;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

public class ChatAndLastMessageModel {

    @Embedded
    private ChatModel chat;

    @Relation(
            parentColumn = "last_message_id",
            entityColumn = "id"
    )
    private MessageModel lastMessage;

    public ChatAndLastMessageModel() {
    }

    @Ignore
    public ChatAndLastMessageModel(ChatModel chat, MessageModel lastMessage) {
        this.chat = chat;
        this.lastMessage = lastMessage;
    }

    public ChatModel getChat() {
        return chat;
    }

    public void setChat(ChatModel chat) {
        this.chat = chat;
    }

    public MessageModel getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageModel lastMessage) {
        this.lastMessage = lastMessage;
    }
}
