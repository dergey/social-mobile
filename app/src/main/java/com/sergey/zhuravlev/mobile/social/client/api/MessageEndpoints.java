package com.sergey.zhuravlev.mobile.social.client.api;

import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.dto.PageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.CreateMessageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.UpdateTextMessageDto;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MessageEndpoints {

    @GET("/api/chat/{chatId}/message")
    ListenableFuture<PageDto<MessageDto>> getChatMessages(@Path("chatId") Long chatId,
                                                          @Query(value = "page") Integer page,
                                                          @Query(value = "size") Integer size);

    @POST("/api/chat/{chatId}/message")
    ListenableFuture<MessageDto> createMessage(@Path("chatId") Long chatId, @Body CreateMessageDto dto);

    @POST("/api/chat/{chatId}/message/image")
    ListenableFuture<MessageDto> createImageMessage(@Path("chatId") Long chatId,
                                                    @Part("image") MultipartBody.Part image);

    @PUT("/api/chat/{chatId}/message/{messageId}")
    ListenableFuture<MessageDto> updateTextMessage(@Path("chatId") Long chatId, @Path("messageId") Long messageId,
                                                   @Body UpdateTextMessageDto dto);

    @DELETE("/api/chat/{chatId}/message/{messageId}")
    ListenableFuture<Void> deleteMessage(@Path("chatId") Long chatId, @Path("messageId") Long messageId);

}
