package com.sergey.zhuravlev.mobile.social.ui.message;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.ExperimentalPagingApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.sergey.zhuravlev.mobile.social.R;
import com.sergey.zhuravlev.mobile.social.constrain.IntentConstrains;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityLoginBinding;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityMainBinding;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityMessageBinding;
import com.sergey.zhuravlev.mobile.social.ui.chat.ChatAdapter;
import com.sergey.zhuravlev.mobile.social.ui.chat.ChatViewModel;
import com.sergey.zhuravlev.mobile.social.ui.login.LoginViewModel;
import com.sergey.zhuravlev.mobile.social.ui.login.LoginViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ExperimentalPagingApi
public class MessageActivity extends AppCompatActivity {

    private ActivityMessageBinding binding;
    private MessageViewModel viewModel;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, new MessageViewModelFactory(this))
                .get(MessageViewModel.class);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new MessageAdapter(this);

        recyclerView = binding.messageRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        if (getIntent().getExtras() != null) {
            Long chatId = getIntent().getExtras().getLong(IntentConstrains.EXTRA_CHAT_ID);
            viewModel.fetchChatMessageModelLiveData(chatId).observe(this, pagingData ->
                    adapter.submitData(getLifecycle(), pagingData)
            );
        } else {
            finish();
        }
    }

}