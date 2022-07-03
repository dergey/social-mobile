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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.sergey.zhuravlev.mobile.social.R;
import com.sergey.zhuravlev.mobile.social.constrain.IntentConstrains;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityRegistrationBinding;
import com.sergey.zhuravlev.mobile.social.ui.profile.ProfileSettingActivity;

public class RegistrationActivity extends AppCompatActivity {

    private RegistrationViewModel registrationViewModel;
    private ActivityRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registrationViewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);

        Button nextButton = binding.nextButton;
        EditText emailEditText = binding.emailEditText;
        EditText passwordEditText = binding.passwordEditText;
        EditText confirmPasswordEditText = binding.confirmPasswordEditText;

        registrationViewModel.getRegistrationFormState().observe(this, new Observer<RegistrationFormState>() {
            @Override
            public void onChanged(@Nullable RegistrationFormState registrationFormState) {
                if (registrationFormState == null) {
                    return;
                }
                nextButton.setEnabled(registrationFormState.isDataValid());
                if (registrationFormState.getEmailError() != null) {
                    emailEditText.setError(getString(registrationFormState.getEmailError()));
                }
                if (registrationFormState.getEmailErrorString() != null) {
                    emailEditText.setError(registrationFormState.getEmailErrorString());
                }
                if (registrationFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(registrationFormState.getPasswordError()));
                }
                if (registrationFormState.getPasswordErrorString() != null) {
                    passwordEditText.setError(registrationFormState.getPasswordErrorString());
                }
                if (registrationFormState.getConfirmPasswordError() != null) {
                    confirmPasswordEditText.setError(getString(registrationFormState.getConfirmPasswordError()));
                }
                if (registrationFormState.getConfirmPasswordErrorString() != null) {
                    confirmPasswordEditText.setError(registrationFormState.getConfirmPasswordErrorString());
                }
            }
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
                registrationViewModel.registrationDataChanged(emailEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        confirmPasswordEditText.getText().toString());
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        confirmPasswordEditText.addTextChangedListener(afterTextChangedListener);
        confirmPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    startNextStepActivity(emailEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextStepActivity(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
    }

    private void startNextStepActivity(String email, String password) {
        Intent intent = new Intent(RegistrationActivity.this, ProfileSettingActivity.class);
        if (!startNextMatchingActivity(intent)) {
            intent.putExtra(IntentConstrains.EXTRA_PROFILE_SETTING_TYPE, "REGISTRATION");
            intent.putExtra(IntentConstrains.EXTRA_REGISTRATION_EMAIL, email);
            intent.putExtra(IntentConstrains.EXTRA_REGISTRATION_PASSWORD, password);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        }
    }

}