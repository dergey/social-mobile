package com.sergey.zhuravlev.mobile.social.client.api;

import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.dto.LoginDto;
import com.sergey.zhuravlev.mobile.social.client.dto.LoginResponseDto;
import com.sergey.zhuravlev.mobile.social.client.dto.RegistrationDto;
import com.sergey.zhuravlev.mobile.social.client.dto.user.UserDto;

import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegistrationEndpoints {

    @POST("/api/register")
    ListenableFuture<UserDto> register(@Body RegistrationDto dto);

}
