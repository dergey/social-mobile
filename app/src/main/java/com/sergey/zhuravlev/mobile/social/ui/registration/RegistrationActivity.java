package com.sergey.zhuravlev.mobile.social.ui.registration;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.sergey.zhuravlev.mobile.social.R;
import com.sergey.zhuravlev.mobile.social.constrain.IntentConstrains;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityRegistrationBinding;
import com.sergey.zhuravlev.mobile.social.enums.ValidatedField;
import com.sergey.zhuravlev.mobile.social.ui.profile.ProfileFragmentViewModel;
import com.sergey.zhuravlev.mobile.social.ui.profile.ProfileFragmentViewModelFactory;
import com.sergey.zhuravlev.mobile.social.util.StringUtils;

import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private RegistrationViewModel registrationViewModel;
    private ActivityRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registrationViewModel = new ViewModelProvider(this, new RegistrationViewModelFactory(this))
                .get(RegistrationViewModel.class);

        Button nextButton = binding.nextButton;
        EditText emailEditText = binding.emailEditText;
        EditText passwordEditText = binding.passwordEditText;
        EditText confirmPasswordEditText = binding.confirmPasswordEditText;

        registrationViewModel.getFormState().observe(this, formState -> {
            if (formState == null) {
                return;
            }
            nextButton.setEnabled(formState.isDataValid());
            if (formState.isFieldContainError(ValidatedField.EMAIL)) {
                emailEditText.setError(formState.getFieldErrorString(ValidatedField.EMAIL));
            }
            if (formState.isFieldContainError(ValidatedField.PASSWORD)) {
                passwordEditText.setError(formState.getFieldErrorString(ValidatedField.PASSWORD));
            }
            if (formState.isFieldContainError(ValidatedField.PASSWORD_CONFIRMATION)) {
                confirmPasswordEditText.setError(formState.getFieldErrorString(ValidatedField.PASSWORD_CONFIRMATION));
            }
        });

        registrationViewModel.getStartRegistrationResult().observe(this, networkResult -> {
            if (networkResult.isHasErrors() && networkResult.getErrorDto() != null) {
                registrationViewModel.processServerDataFieldError(networkResult.getErrorDto());
                return;
            }

            startNextStepActivity(networkResult.getData().getContinuationCode().toString(),
                    passwordEditText.getText().toString());
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                registrationViewModel.processFormDataChanged(Map.of(
                        ValidatedField.EMAIL, emailEditText.getText().toString(),
                        ValidatedField.PASSWORD, passwordEditText.getText().toString(),
                        ValidatedField.PASSWORD_CONFIRMATION, confirmPasswordEditText.getText().toString())
                );
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        confirmPasswordEditText.addTextChangedListener(afterTextChangedListener);
        confirmPasswordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                registrationViewModel.startRegistration(emailEditText.getText().toString());
            }
            return false;
        });

        nextButton.setOnClickListener(v -> {
                registrationViewModel.startRegistration(emailEditText.getText().toString());
        });
    }

    @Override
    public void onTopResumedActivityChanged(boolean isTopResumedActivity) {
        if (isTopResumedActivity) {
            Intent intent = getIntent();
            String passwordError = intent.getStringExtra(IntentConstrains.EXTRA_REGISTRATION_PASSWORD_ERROR);
            if (!StringUtils.isBlank(passwordError)) {
                registrationViewModel.setFieldErrors(Map.of(ValidatedField.PASSWORD, passwordError));
            }
        }
        super.onTopResumedActivityChanged(isTopResumedActivity);
    }

    private void startNextStepActivity(String continuationCode, String password) {
        Intent intent = new Intent(RegistrationActivity.this, EmailConfirmActivity.class);
        if (!startNextMatchingActivity(intent)) {
            intent.putExtra(IntentConstrains.EXTRA_REGISTRATION_CONTINUATION_CODE, continuationCode);
            intent.putExtra(IntentConstrains.EXTRA_REGISTRATION_PASSWORD, password);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        }
    }

}