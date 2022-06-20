package com.sergey.zhuravlev.mobile.social.client.api;

import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.dto.LoginDto;
import com.sergey.zhuravlev.mobile.social.client.dto.LoginResponseDto;

import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginEndpoints {

    @POST("/api/authenticate")
    ListenableFuture<LoginResponseDto> authenticate(@Body LoginDto dto);

}
