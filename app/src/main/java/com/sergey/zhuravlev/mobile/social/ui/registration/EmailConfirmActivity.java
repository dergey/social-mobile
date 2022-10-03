package com.sergey.zhuravlev.mobile.social.ui.registration;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.sergey.zhuravlev.mobile.social.R;
import com.sergey.zhuravlev.mobile.social.constrain.IntentConstrains;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityEmailConfirmBinding;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityRegistrationBinding;
import com.sergey.zhuravlev.mobile.social.enums.ValidatedField;
import com.sergey.zhuravlev.mobile.social.ui.profile.ProfileSettingActivity;

import java.util.Map;
import java.util.Objects;

public class EmailConfirmActivity extends AppCompatActivity {

    private EmailConfirmViewModel emailConfirmViewModel;
    private ActivityEmailConfirmBinding binding;

    private String continuationCode;
    private String registrationPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEmailConfirmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        emailConfirmViewModel = new ViewModelProvider(this, new EmailConfirmViewModelFactory(this))
                .get(EmailConfirmViewModel.class);

        Intent intent = getIntent();
        continuationCode = intent.getStringExtra(IntentConstrains.EXTRA_REGISTRATION_CONTINUATION_CODE);
        registrationPassword = intent.getStringExtra(IntentConstrains.EXTRA_REGISTRATION_PASSWORD);

        Button confirmButton = binding.confirmButton;
        TextView hintLinkTextView = binding.hintLinkTextView;
        EditText codeEditText1 = binding.codeEditText1;
        EditText codeEditText2 = binding.codeEditText2;
        EditText codeEditText3 = binding.codeEditText3;
        EditText codeEditText4 = binding.codeEditText4;
        EditText codeEditText5 = binding.codeEditText5;

        emailConfirmViewModel.getFormState().observe(this, formState -> {
            if (formState == null) {
                return;
            }
            confirmButton.setEnabled(formState.isDataValid());
            if (formState.isFieldContainError(ValidatedField.CONFIRMATION_CODE)) {
                codeEditText1.setError("", null);
                codeEditText2.setError("", null);
                codeEditText3.setError("", null);
                codeEditText4.setError("", null);
                codeEditText5.setError(formState.getFieldErrorString(ValidatedField.CONFIRMATION_CODE));
            }
        });

        emailConfirmViewModel.getConfirmByCodeResult().observe(this, networkResult -> {
            if (networkResult.getErrorDto() != null) {
                emailConfirmViewModel.processServerDataFieldError(networkResult.getErrorDto());
                return;
            }

            if (networkResult.isHasErrors() || Objects.isNull(networkResult.getData())) {
                return;
            }

            startNextStepActivity(networkResult.getData().getContinuationCode().toString());
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
                emailConfirmViewModel.processFormDataChanged(Map.of(
                        ValidatedField.CONFIRMATION_CODE, codeEditText1.getText().toString() + codeEditText2.getText().toString()
                                + codeEditText3.getText().toString() + codeEditText4.getText().toString()
                                + codeEditText5.getText().toString())
                );
            }
        };
        codeEditText1.addTextChangedListener(afterTextChangedListener);
        codeEditText2.addTextChangedListener(afterTextChangedListener);
        codeEditText3.addTextChangedListener(afterTextChangedListener);
        codeEditText4.addTextChangedListener(afterTextChangedListener);
        codeEditText5.addTextChangedListener(afterTextChangedListener);

        confirmButton.setOnClickListener(v -> {
            emailConfirmViewModel.confirmByCode(continuationCode, new StringBuilder()
                    .append(codeEditText1.getText())
                    .append(codeEditText2.getText())
                    .append(codeEditText3.getText())
                    .append(codeEditText4.getText())
                    .append(codeEditText5.getText())
                    .toString());
        });
    }

    private void startNextStepActivity(String continuationCode) {
        Intent intent = new Intent(EmailConfirmActivity.this, ProfileSettingActivity.class);
        if (!startNextMatchingActivity(intent)) {
            intent.putExtra(IntentConstrains.EXTRA_PROFILE_SETTING_TYPE, "REGISTRATION");
            intent.putExtra(IntentConstrains.EXTRA_REGISTRATION_CONTINUATION_CODE, continuationCode);
            intent.putExtra(IntentConstrains.EXTRA_REGISTRATION_PASSWORD, registrationPassword);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        }
    }
}