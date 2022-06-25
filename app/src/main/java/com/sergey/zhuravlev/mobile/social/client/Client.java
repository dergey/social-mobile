package com.sergey.zhuravlev.mobile.social.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sergey.zhuravlev.mobile.social.client.api.ChatEndpoints;
import com.sergey.zhuravlev.mobile.social.client.api.LoginEndpoints;
import com.sergey.zhuravlev.mobile.social.client.api.MessageEndpoints;
import com.sergey.zhuravlev.mobile.social.client.api.ProfileEndpoints;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.guava.GuavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class Client {

    static class BearerTokenInterceptor implements Interceptor {
        @NotNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder requestBuilder = chain
                    .request()
                    .newBuilder();
            if (Client.barrierToken != null && !Client.barrierToken.isEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer " + Client.barrierToken);
            }
            return chain.proceed(requestBuilder.build());
        }
    }

    private static final String BASE_URL = "https://social.xpolr.space";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new BearerTokenInterceptor())
            .followRedirects(false)
            .build();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.registerModule(new JavaTimeModule());
    }
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(GuavaCallAdapterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .client(client)
            .build();

    private static String barrierToken;

    public static String getBarrierToken() {
        return barrierToken;
    }

    public static void setBarrierToken(String barrierToken) {
        Client.barrierToken = barrierToken;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static LoginEndpoints getLoginEndpoints() {
        return retrofit.create(LoginEndpoints.class);
    }

    public static ChatEndpoints getChatEndpoints() {
        return retrofit.create(ChatEndpoints.class);
    }

    public static MessageEndpoints getMessageEndpoints() {
        return retrofit.create(MessageEndpoints.class);
    }

    public static ProfileEndpoints getProfileEndpoints() {
        return retrofit.create(ProfileEndpoints.class);
    }

}
