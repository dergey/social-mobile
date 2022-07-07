package com.sergey.zhuravlev.mobile.social.data;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.client.api.RegistrationEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.client.dto.user.UserDto;

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

    public void register(String email, String password, String username, String firstName,
                         String middleName, String secondName, String city, LocalDate birthDate,
                         FutureCallback<Result<UserDto, ErrorDto>> callback) {
        Futures.addCallback(dataSource.register(email, password, username, firstName, middleName,
                secondName, city, birthDate), callback, executor);
    }

}
