package com.sergey.zhuravlev.mobile.social.ui.registration;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.sergey.zhuravlev.mobile.social.client.dto.registration.RegistrationStatusDto;
import com.sergey.zhuravlev.mobile.social.data.repository.RegistrationRepository;
import com.sergey.zhuravlev.mobile.social.enums.ValidatedField;
import com.sergey.zhuravlev.mobile.social.ui.common.NetworkLiveDataFutureCallback;
import com.sergey.zhuravlev.mobile.social.ui.common.UiNetworkResult;
import com.sergey.zhuravlev.mobile.social.ui.common.ValidationViewModel;

import java.util.Set;

public class RegistrationViewModel extends ValidationViewModel {

    private final MutableLiveData<UiNetworkResult<RegistrationStatusDto>> startRegistrationResult = new MutableLiveData<>();

    private final RegistrationRepository registrationRepository;

    public MutableLiveData<UiNetworkResult<RegistrationStatusDto>> getStartRegistrationResult() {
        return startRegistrationResult;
    }

    public RegistrationViewModel(Context context) {
        super(context, Set.of(ValidatedField.EMAIL, ValidatedField.PASSWORD, ValidatedField.PASSWORD_CONFIRMATION));
        this.registrationRepository = RegistrationRepository.getInstance();
    }

    public void startRegistration(String email) {
        registrationRepository.startRegistration(email, new NetworkLiveDataFutureCallback<>(startRegistrationResult));
    }

}
