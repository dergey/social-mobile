package com.sergey.zhuravlev.mobile.social.ui.message;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.ExperimentalPagingApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sergey.zhuravlev.mobile.social.R;
import com.sergey.zhuravlev.mobile.social.constrain.ActivityCodes;
import com.sergey.zhuravlev.mobile.social.constrain.IntentConstrains;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityMessageBinding;

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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        EditText editText = binding.messageEditText;

        ImageButton sendMessageButton = binding.sendMessageButton;
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.createTextMessage(chatId, editText.getText().toString());
                editText.getText().clear();
            }
        });
        ImageButton addMessageButton = binding.addMessageButton;
        PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.image_attachment_menu_item:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                                ActivityCodes.IMAGE_CHOOSE_REQUEST);
                        return true;
                    case R.id.sticker_attachment_menu_item:
                        return true;
                    default:
                        return false;
                }
            }
        };
        addMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MessageActivity.this, v);
                popup.setForceShowIcon(true);
                popup.setOnMenuItemClickListener(onMenuItemClickListener);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.attachment_menu, popup.getMenu());
                popup.show();
            }
        });

        viewModel.getCreateMessageResult().observe(this, result -> {
            if (result.isHasErrors()) {
                Log.e("MessageViewModel/createMessageResult", "Creating message error: " + result.getErrorMessage());
            }
        });

        viewModel.getDatabaseCreateMessage().observe(this, result -> {
            // This observer allows to scroll to a position after applying the data in the MessageAdapter:
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    linearLayoutManager.scrollToPositionWithOffset(0, 0);
                    adapter.unregisterAdapterDataObserver(this);
                }
            });
        });

        if (getIntent().getExtras() != null) {
            chatId = getIntent().getExtras().getLong(IntentConstrains.EXTRA_CHAT_ID);
            viewModel.fetchChatMessageModelLiveData(chatId).observe(this, pagingData ->
                    adapter.submitData(getLifecycle(), pagingData)
            );
            viewModel.updateReadStatus(chatId);
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ActivityCodes.IMAGE_CHOOSE_REQUEST:
                    Uri selectedImageUri = data.getData();
                    if (Objects.nonNull(selectedImageUri)) {
                        viewModel.createImageMessage(chatId, selectedImageUri);
                    }
                    break;
            }
        }
    }

}