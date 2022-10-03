package com.sergey.zhuravlev.mobile.social.data.repository;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.client.api.RegistrationEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.client.dto.registration.RegistrationStatusDto;
import com.sergey.zhuravlev.mobile.social.client.dto.user.UserDto;
import com.sergey.zhuravlev.mobile.social.data.Result;
import com.sergey.zhuravlev.mobile.social.data.datasource.RegistrationDataSource;
import com.sergey.zhuravlev.mobile.social.enums.Gender;

import java.time.LocalDate;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegistrationRepository {

    private static volatile RegistrationRepository instance;

    private final Executor executor;
    private final RegistrationDataSource dataSource;

    private RegistrationRepository() {
        RegistrationEndpoints registrationEndpoints = Client.getRegistrationEndpoints();
        this.executor = Executors.newSingleThreadExecutor();
        this.dataSource = new RegistrationDataSource(registrationEndpoints, executor);
    }

    public static RegistrationRepository getInstance() {
        if (instance == null) {
            instance = new RegistrationRepository();
        }
        return instance;
    }

    public void startRegistration(String email, FutureCallback<Result<RegistrationStatusDto, ErrorDto>> callback) {
        Futures.addCallback(dataSource.startRegistration(email), callback, executor);
    }

    public void confirmByCode(String continuationCode, String code, FutureCallback<Result<RegistrationStatusDto, ErrorDto>> callback) {
        Futures.addCallback(dataSource.confirmByCode(code, continuationCode), callback, executor);
    }

    public void resend(String continuationCode, FutureCallback<Result<RegistrationStatusDto, ErrorDto>> callback) {
        Futures.addCallback(dataSource.resend(continuationCode), callback, executor);
    }

    public void completeRegistration(String continuationCode, String password, String username,
                                     String firstName, String middleName, String secondName,
                                     Gender gender, LocalDate birthDate,
                                     FutureCallback<Result<UserDto, ErrorDto>> callback) {
        Futures.addCallback(dataSource.completeRegistration(continuationCode, password, username,
                firstName, middleName, secondName, gender, birthDate), callback, executor);
    }



}
