package com.sergey.zhuravlev.mobile.social.client.api;

import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.dto.registration.CompleteRegistrationDto;
import com.sergey.zhuravlev.mobile.social.client.dto.registration.ContinuationDto;
import com.sergey.zhuravlev.mobile.social.client.dto.registration.ManualCodeConfirmationDto;
import com.sergey.zhuravlev.mobile.social.client.dto.registration.RegistrationStatusDto;
import com.sergey.zhuravlev.mobile.social.client.dto.registration.StartRegistrationDto;
import com.sergey.zhuravlev.mobile.social.client.dto.user.UserDto;

import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegistrationEndpoints {

    @POST("/api/registration")
    ListenableFuture<RegistrationStatusDto> startRegistration(@Body StartRegistrationDto dto);

    @POST("/api/registration/confirm/code")
    ListenableFuture<RegistrationStatusDto> confirmByCode(@Body ManualCodeConfirmationDto dto);

    @POST("/api/registration/confirm/link")
    ListenableFuture<RegistrationStatusDto> confirmByLink();

    @POST("/api/registration/resend")
    ListenableFuture<RegistrationStatusDto> resendConfirmation(@Body ContinuationDto dto);

    @POST("/api/registration/complete")
    ListenableFuture<UserDto> completeRegistration(@Body CompleteRegistrationDto dto);

}
