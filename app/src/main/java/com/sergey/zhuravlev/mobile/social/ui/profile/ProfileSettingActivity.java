package com.sergey.zhuravlev.mobile.social.ui.profile;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.sergey.zhuravlev.mobile.social.MainActivity;
import com.sergey.zhuravlev.mobile.social.R;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.constrain.IntentConstrains;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityProfileSettingBinding;
import com.sergey.zhuravlev.mobile.social.ui.login.LoginActivity;
import com.sergey.zhuravlev.mobile.social.ui.registration.RegistrationActivity;
import com.sergey.zhuravlev.mobile.social.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public class ProfileSettingActivity extends AppCompatActivity {

    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);

    private ProfileSettingViewModel profileSettingViewModel;
    private ActivityProfileSettingBinding binding;

    private String type;
    private String registrationEmail;
    private String registrationPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        profileSettingViewModel = new ViewModelProvider(this).get(ProfileSettingViewModel.class);

        ImageView abBackImageView = binding.abBackImageView;
        abBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileSettingActivity.super.onBackPressed();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

        Intent intent = getIntent();
        type = intent.getStringExtra(IntentConstrains.EXTRA_PROFILE_SETTING_TYPE);

        switch (type) {
            case "REGISTRATION":
                registrationEmail = intent.getStringExtra(IntentConstrains.EXTRA_REGISTRATION_EMAIL);
                registrationPassword = intent.getStringExtra(IntentConstrains.EXTRA_REGISTRATION_PASSWORD);
                profileSettingViewModel.getRegisterResult().observe(this, (result) -> {
                    if (!result.isHasErrors() && result.getData() != null) {
                        profileSettingViewModel.login(result.getData().getEmail(), registrationPassword);
                    } else {
                        if (result.getErrorDto() != null && !Iterables.isEmpty(result.getErrorDto().getFields())) {
                            profileSettingViewModel.processServerFieldError(result.getErrorDto());
                        } else if (result.getErrorDto() != null) {
                            Toast.makeText(this,
                                            String.format("Code %s: %s", result.getErrorDto().getCode(),
                                                    result.getErrorDto().getMessage()),
                                            LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(this, result.getErrorMessage(), LENGTH_SHORT).show();
                        }
                    }
                });
                profileSettingViewModel.getLoginResult().observe(this, (loginResult) -> {
                    Intent startingIntent;
                    if (!loginResult.isHasErrors() && loginResult.getData() != null) {
                        startingIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startingIntent.putExtra(IntentConstrains.EXTRA_TOKEN, loginResult.getData().getJwtToken());
                    } else {
                        startingIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    }
                    startingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(startingIntent);
                });
                break;
            case "UPDATE":
                registrationEmail = null;
                registrationPassword = null;
                break;
        }

        Button saveButton = binding.saveButton;
        EditText firstNameEditText = binding.firstNameEditText;
        EditText lastNameEditText = binding.lastNameEditText;
        EditText additionalNameEditText = binding.additionalNameEditText;
        EditText usernameEditText = binding.usernameEditText;
        EditText birthdayEditText = binding.birthdayEditText;

        profileSettingViewModel.getProfileSettingFormState().observe(this, new Observer<ProfileSettingFormState>() {
            @Override
            public void onChanged(@Nullable ProfileSettingFormState profileSettingFormState) {
                if (profileSettingFormState == null) {
                    return;
                }
                saveButton.setEnabled(profileSettingFormState.isDataValid());
                if (profileSettingFormState.getFirstNameError() != null) {
                    firstNameEditText.setError(getString(profileSettingFormState.getFirstNameError()));
                }
                if (profileSettingFormState.getFirstNameErrorString() != null) {
                    firstNameEditText.setError(profileSettingFormState.getFirstNameErrorString());
                }
                if (profileSettingFormState.getLastNameError() != null) {
                    lastNameEditText.setError(getString(profileSettingFormState.getLastNameError()));
                }
                if (profileSettingFormState.getLastNameErrorString() != null) {
                    lastNameEditText.setError(profileSettingFormState.getLastNameErrorString());
                }
                if (profileSettingFormState.getAdditionalNameError() != null) {
                    additionalNameEditText.setError(getString(profileSettingFormState.getAdditionalNameError()));
                }
                if (profileSettingFormState.getAdditionalNameErrorString() != null) {
                    additionalNameEditText.setError(profileSettingFormState.getAdditionalNameErrorString());
                }
                if (profileSettingFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(profileSettingFormState.getUsernameError()));
                }
                if (profileSettingFormState.getUsernameErrorString() != null) {
                    usernameEditText.setError(profileSettingFormState.getUsernameErrorString());
                }
                if (profileSettingFormState.getBirthDateError() != null) {
                    birthdayEditText.setError(getString(profileSettingFormState.getBirthDateError()));
                }
                if (profileSettingFormState.getBirthDateErrorString() != null) {
                    birthdayEditText.setError(profileSettingFormState.getBirthDateErrorString());
                }

                // todo its not working, pls use Task and Backstack
                if (profileSettingFormState.getEmailErrorString() != null) {

                    Intent startingIntent = new Intent(getApplicationContext(), RegistrationActivity.class);
                    startingIntent.putExtra(IntentConstrains.EXTRA_REGISTRATION_EMAIL_ERROR, profileSettingFormState.getEmailErrorString());
                    startingIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(startingIntent);
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                }
                if (profileSettingFormState.getPasswordErrorString() != null) {
                    Intent startingIntent = new Intent(getApplicationContext(), RegistrationActivity.class);
                    startingIntent.putExtra(IntentConstrains.EXTRA_REGISTRATION_PASSWORD_ERROR, profileSettingFormState.getPasswordErrorString());
                    startingIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(startingIntent);
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
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
                profileSettingViewModel.profileSettingDataChanged(firstNameEditText.getText().toString(),
                        lastNameEditText.getText().toString(),
                        additionalNameEditText.getText().toString(),
                        usernameEditText.getText().toString(),
                        birthdayEditText.getText().toString());
            }
        };
        firstNameEditText.addTextChangedListener(afterTextChangedListener);
        lastNameEditText.addTextChangedListener(afterTextChangedListener);
        additionalNameEditText.addTextChangedListener(afterTextChangedListener);
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        birthdayEditText.addTextChangedListener(afterTextChangedListener);
        birthdayEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onDataComplete(usernameEditText.getText().toString(),
                            firstNameEditText.getText().toString(),
                            additionalNameEditText.getText().toString(),
                            lastNameEditText.getText().toString(),
                            birthdayEditText.getText().toString());
                }
                return false;
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDataComplete(usernameEditText.getText().toString(),
                        firstNameEditText.getText().toString(),
                        additionalNameEditText.getText().toString(),
                        lastNameEditText.getText().toString(),
                        birthdayEditText.getText().toString());
            }
        });
    }

    private void onDataComplete(String username, String firstName, String middleName, String secondName,
                                String birthDateString) {
        username = StringUtils.isBlank(username) ? null : username;
        firstName = StringUtils.isBlank(firstName) ? null : firstName;
        middleName = StringUtils.isBlank(middleName) ? null : middleName;
        secondName = StringUtils.isBlank(secondName) ? null : secondName;
        birthDateString = StringUtils.isBlank(birthDateString) ? null : birthDateString;
        switch (type) {
            case "REGISTRATION":
                // todo added city field
                profileSettingViewModel.register(registrationEmail, registrationPassword, username,
                        firstName, middleName, secondName, "Minsk", LocalDate.parse(birthDateString, DATE_FORMATTER));
                break;
            case "UPDATE":
                break;
        }
    }
}