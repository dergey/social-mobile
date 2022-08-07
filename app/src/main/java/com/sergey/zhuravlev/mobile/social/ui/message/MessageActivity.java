package com.sergey.zhuravlev.mobile.social.ui.message;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.ExperimentalPagingApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.sergey.zhuravlev.mobile.social.R;
import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.constrain.ActivityCodes;
import com.sergey.zhuravlev.mobile.social.constrain.IntentConstrains;
import com.sergey.zhuravlev.mobile.social.database.model.ChatModel;
import com.sergey.zhuravlev.mobile.social.database.model.ProfilePreviewEmbeddable;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityMessageBinding;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExperimentalPagingApi
public class MessageActivity extends AppCompatActivity {

    private ActivityMessageBinding binding;
    private MessageViewModel viewModel;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;

    private ChatModel chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, new MessageViewModelFactory(this))
                .get(MessageViewModel.class);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        retrieveIntentExtras();

        // ActionBar initializing:

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarCustomView = inflater.inflate(R.layout.actionbar_activity_message, null);

        ActionBar actionBar = getSupportActionBar();
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.START);
        actionBar.setCustomView(actionBarCustomView, params);
        actionBar.setDisplayShowCustomEnabled(true);

        ImageView actionBarAvatarImageView = actionBarCustomView.findViewById(R.id.avatar_image_view);
        TextView actionBarFullnameTextView = actionBarCustomView.findViewById(R.id.fullname_text_view);
        TextView actionBarStatusTextView = actionBarCustomView.findViewById(R.id.status_text_view);

        ProfilePreviewEmbeddable profile = chat.getTargetProfile();

        String profileAvatarUrl = String.format("%s/api/profile/%s/avatar", Client.getBaseUrl(),
                profile.getUsername());
        GlideUrl glideUrl = new GlideUrl(profileAvatarUrl,
                new LazyHeaders.Builder()
                        .addHeader("Authorization", "Bearer " + Client.getBarrierToken())
                        .build());
        Glide.with(this).load(glideUrl)
                    .circleCrop()
                    .into(actionBarAvatarImageView);

        actionBarFullnameTextView.setText(Stream.of(profile.getFirstName(), profile.getMiddleName(), profile.getSecondName())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" ")));

        // todo: move last seen to profile preview, instead of profile details!
        actionBarStatusTextView.setText("Last seen 01 January 1970 03:01:00");

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
                viewModel.createTextMessage(chat.getId(), editText.getText().toString());
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

        viewModel.fetchChatMessageModelLiveData(chat.getId()).observe(this, pagingData ->
                adapter.submitData(getLifecycle(), pagingData)
        );
        viewModel.updateReadStatus(chat.getId());
    }

    private void retrieveIntentExtras() {
        chat = getIntent().getExtras().getParcelable(IntentConstrains.EXTRA_CHAT);
        if (chat == null) {
            Log.e("MessageActivity/retrieveIntentExtras",
                    "Defined intent doesn't provide the necessary data to start MessageActivity!");
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
                        viewModel.createImageMessage(chat.getId(), selectedImageUri);
                    }
                    break;
            }
        }
    }

}