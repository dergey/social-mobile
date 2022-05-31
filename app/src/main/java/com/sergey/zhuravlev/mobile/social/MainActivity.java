package com.sergey.zhuravlev.mobile.social;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.constrain.IntentConstrains;
import com.sergey.zhuravlev.mobile.social.constrain.Preferences;
import com.sergey.zhuravlev.mobile.social.constrain.RequestActivityCodes;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityMainBinding;
import com.sergey.zhuravlev.mobile.social.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_chats)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        loadPreferences();
        if (!isLogin) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, RequestActivityCodes.LOGIN_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestActivityCodes.LOGIN_REQUEST:
                String token = data.getStringExtra(IntentConstrains.EXTRA_TOKEN);
                Client.setBarrierToken(token);
                savePreferences(token);
                break;
        }
    }

    private void loadPreferences() {
        SharedPreferences settings = getSharedPreferences(Preferences.PREF_TOKEN, Context.MODE_PRIVATE);
        String token = settings.getString(Preferences.PREF_TOKEN, null);
        if (token != null) {
            isLogin = true;
            Client.setBarrierToken(token);
        } else {
            isLogin = false;
        }
    }

    private void savePreferences(String token) {
        SharedPreferences settings = getSharedPreferences(Preferences.PREF_TOKEN,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Preferences.PREF_TOKEN, token);
        editor.apply();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isLogin) {
            String barrierToken = Client.getBarrierToken();
            savePreferences(barrierToken);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPreferences();
    }

}