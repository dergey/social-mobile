package com.sergey.zhuravlev.mobile.social.ui.message;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.CombinedLoadStates;
import androidx.paging.ExperimentalPagingApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sergey.zhuravlev.mobile.social.constrain.IntentConstrains;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityMessageBinding;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        EditText editText = binding.messageEditText;

        ImageButton button = binding.sendMessageButton;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.createTextMessage(chatId, editText.getText().toString());
                editText.getText().clear();
            }
        });
        viewModel.getDatabaseCreateMessage().observe(this, result -> {
            // This observer allows to scroll to a position after applying the data in the MessageAdapter:
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    linearLayoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, 20);
                    adapter.unregisterAdapterDataObserver(this);
                }
            });
        });

        if (getIntent().getExtras() != null) {
            chatId = getIntent().getExtras().getLong(IntentConstrains.EXTRA_CHAT_ID);
            viewModel.fetchChatMessageModelLiveData(chatId).observe(this, pagingData ->
                    adapter.submitData(getLifecycle(), pagingData)
            );
        } else {
            finish();
        }
    }

}