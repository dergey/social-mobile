package com.sergey.zhuravlev.mobile.social.client.api;

import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.dto.PageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.chat.ChatDto;
import com.sergey.zhuravlev.mobile.social.client.dto.chat.ChatPreviewDto;
import com.sergey.zhuravlev.mobile.social.client.dto.chat.CreateChatDto;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatEndpoints {

    @GET("/api/chat")
    ListenableFuture<PageDto<ChatPreviewDto>> getCurrentUserChats(@Query(value = "page") Integer page,
                                                                  @Query(value = "size") Integer size);

    @POST("/api/chat")
    ListenableFuture<ChatDto> getOrCreateChat(@Body CreateChatDto dto);

    @POST("/api/chat/{chatId}/read")
    ListenableFuture<Void> updateReadStatus(@Path("chatId") Long chatId);

    @POST("/api/chat/{id}/block")
    ListenableFuture<ChatDto> blockChat(@Path("chatId") Long chatId);

    @POST("/api/chat/{id}/unblock")
    ListenableFuture<ChatDto> unblockChat(@Path("chatId") Long chatId);

}
