package com.sergey.zhuravlev.mobile.social.client.api;

import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.dto.PageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.MessageDto;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MessageEndpoints {

    @GET("/api/chat/{chatId}/message")
    ListenableFuture<PageDto<MessageDto>> getChatMessages(@Path("chatId") Long chatId,
                                                          @Query(value = "page") Integer page,
                                                          @Query(value = "size") Integer size);

}
