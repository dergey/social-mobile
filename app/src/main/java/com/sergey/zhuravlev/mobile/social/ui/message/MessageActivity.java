package com.sergey.zhuravlev.mobile.social.ui.message;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.ExperimentalPagingApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.sergey.zhuravlev.mobile.social.R;
import com.sergey.zhuravlev.mobile.social.client.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.client.mapper.MessageModelMapper;
import com.sergey.zhuravlev.mobile.social.constrain.IntentConstrains;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityLoginBinding;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityMainBinding;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityMessageBinding;
import com.sergey.zhuravlev.mobile.social.ui.chat.ChatAdapter;
import com.sergey.zhuravlev.mobile.social.ui.chat.ChatViewModel;
import com.sergey.zhuravlev.mobile.social.ui.common.UiResult;
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

    private Long chatId;

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

        EditText editText = binding.editTextTextPersonName;

        ImageButton button = binding.imageButton;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.createTextMessage(chatId, editText.getText().toString());
                editText.getText().clear();
            }
        });

        if (getIntent().getExtras() != null) {
            chatId = getIntent().getExtras().getLong(IntentConstrains.EXTRA_CHAT_ID);
            viewModel.fetchChatMessageModelLiveData(chatId).observe(this, pagingData ->
                    adapter.submitData(getLifecycle(), pagingData)
            );
            viewModel.getCreateMessageResult().observe(this, result -> {
                if (!result.isHasErrors()) {
                    adapter.addNewMessage(MessageModelMapper.toModel(result.getData()));
                }
            });


        } else {
            finish();
        }
    }

}