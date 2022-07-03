package com.sergey.zhuravlev.mobile.social.ui.login;

import android.app.Activity;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sergey.zhuravlev.mobile.social.constrain.IntentConstrains;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityLoginBinding;
import com.sergey.zhuravlev.mobile.social.ui.message.MessageActivity;
import com.sergey.zhuravlev.mobile.social.ui.registration.RegistrationActivity;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText emailEditText = binding.emailEditText;
        final EditText passwordEditText = binding.passwordEditText;
        final Button loginButton = binding.loginButton;
        final TextView signUpTextView = binding.signUpTextView;
        final ConstraintLayout errorLayout = binding.errorLayout;
        final TextView errorTextView = binding.errorTextView;
        //final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                errorLayout.setVisibility(View.INVISIBLE);
                if (loginFormState.getEmailError() != null) {
                    emailEditText.setError(getString(loginFormState.getEmailError()));
                }
                if (loginFormState.getEmailErrorString() != null) {
                    emailEditText.setError(loginFormState.getEmailErrorString());
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
                if (loginFormState.getPasswordErrorString() != null) {
                    passwordEditText.setError(loginFormState.getPasswordErrorString());
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                //loadingProgressBar.setVisibility(View.GONE);

                if (loginResult.getErrorMessage() != null) {
                    errorLayout.setVisibility(View.VISIBLE);
                    errorTextView.setText(loginResult.getErrorMessage());
                    loginButton.setEnabled(false);
                    //showLoginFailed(loginResult.getErrorMessage());
                }

                if (loginResult.isHasErrors()) {
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra(IntentConstrains.EXTRA_TOKEN, loginResult.getJwtToken());
                setResult(Activity.RESULT_OK, intent);
                finish();
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
                loginViewModel.loginDataChanged(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(emailEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                if (!startNextMatchingActivity(intent)) {
                    startActivity(intent);
                }
                finish();
            }
        });
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

}