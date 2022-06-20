package com.sergey.zhuravlev.mobile.social.data;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.client.api.LoginEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.client.dto.LoginResponseDto;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginRepository {

    private static volatile LoginRepository instance;

    private final Executor executor;
    private final LoginDataSource dataSource;

    private LoginRepository() {
        LoginEndpoints loginEndpoints = Client.getLoginEndpoints();
        this.executor = Executors.newSingleThreadExecutor();
        this.dataSource = new LoginDataSource(loginEndpoints, executor);
    }

    public static LoginRepository getInstance() {
        if(instance == null){
            instance = new LoginRepository();
        }
        return instance;
    }

    public ListenableFuture<Result<LoginResponseDto, ErrorDto>> login(String email, String password) {
        return dataSource.login(email, password);
    }

    public void login(String email, String password, FutureCallback<Result<LoginResponseDto, ErrorDto>> callback) {
        Futures.addCallback(dataSource.login(email, password), callback, executor);
    }

}