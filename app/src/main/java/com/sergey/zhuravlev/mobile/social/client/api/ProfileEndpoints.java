package com.sergey.zhuravlev.mobile.social.client.api;

import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.dto.PageDto;
import com.sergey.zhuravlev.mobile.social.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.dto.profile.ProfileDetailDto;
import com.sergey.zhuravlev.mobile.social.dto.profile.ProfileDto;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProfileEndpoints {


    @GET("/api/profile")
    ListenableFuture<ProfileDetailDto> getCurrentUserProfile();

    @GET("/api/friend")
    ListenableFuture<PageDto<ProfileDto>> getCurrentUserFriends(@Query(value = "page") Integer page,
                                                                @Query(value = "size") Integer size);



    @GET("/api/profile/{username}/friend")
    ListenableFuture<PageDto<ProfileDto>> getProfileFriends(@Path("username") String username,
                                                            @Query(value = "page") Integer page,
                                                            @Query(value = "size") Integer size);

}
