package com.sergey.zhuravlev.mobile.social.ui.message;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.ExperimentalPagingApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sergey.zhuravlev.mobile.social.constrain.IntentConstrains;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityMessageBinding;

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
                    //todo insert into DB, and load cache.
                }
            });


        } else {
            finish();
        }
    }

}