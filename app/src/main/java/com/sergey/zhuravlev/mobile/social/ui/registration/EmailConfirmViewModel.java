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

public class EmailConfirmViewModel extends ValidationViewModel {

    private final MutableLiveData<UiNetworkResult<RegistrationStatusDto>> confirmByCodeResult = new MutableLiveData<>();

    private final RegistrationRepository registrationRepository;

    public MutableLiveData<UiNetworkResult<RegistrationStatusDto>> getConfirmByCodeResult() {
        return confirmByCodeResult;
    }

    public EmailConfirmViewModel(Context context) {
        super(context, Set.of(ValidatedField.CONFIRMATION_CODE));
        this.registrationRepository = RegistrationRepository.getInstance();
    }

    public void confirmByCode(String continuationCode, String code) {
        registrationRepository.confirmByCode(continuationCode, code, new NetworkLiveDataFutureCallback<>(confirmByCodeResult));
    }

}
