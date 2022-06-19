package com.sergey.zhuravlev.mobile.social.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.sergey.zhuravlev.mobile.social.R;
import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.constrain.IntentConstrains;
import com.sergey.zhuravlev.mobile.social.dto.chat.ChatPreviewDto;
import com.sergey.zhuravlev.mobile.social.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.dto.message.TextMessageDto;
import com.sergey.zhuravlev.mobile.social.ui.message.MessageActivity;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatAdapter extends PagingDataAdapter<ChatPreviewDto, RecyclerView.ViewHolder> {

    private final Context context;
    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());

    public ChatAdapter(Context context) {
        super(new RepositoryComparator());
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return ChatViewHolder.getInstance(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        ChatPreviewDto chatPreview = getItem(position);
        if (holder instanceof ChatViewHolder && chatPreview != null) {
            ((ChatViewHolder) holder).bind(chatPreview, context);
        }
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ChatPreviewDto item;

        private final ImageView chatProfileAvatar;
        private final ImageView unreadMark;
        private final TextView chatTitle;
        private final TextView chatLastMessageText;
        private final TextView chatLastMessageDate;

        public ChatViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            chatProfileAvatar = itemView.findViewById(R.id.avatar);
            chatTitle = itemView.findViewById(R.id.chatTitle);
            chatLastMessageText = itemView.findViewById(R.id.lastMessageText);
            chatLastMessageDate = itemView.findViewById(R.id.lastMessageDate);
            unreadMark = itemView.findViewById(R.id.unreadMark);
        }

        public static ChatViewHolder getInstance(ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_chat, parent, false);
            ChatViewHolder chatViewHolder = new ChatViewHolder(view);
            view.setOnClickListener(chatViewHolder);
            return chatViewHolder;
        }

        public ChatViewHolder bind(ChatPreviewDto item, Context context) {
            this.item = item;
            chatTitle.setText(String.format("%s %s", item.getTargetProfile().getFirstName(), item.getTargetProfile().getSecondName()));

            MessageDto lastMessage = item.getLastMessage();
            if (lastMessage == null) {
                return this;
            }
            switch (lastMessage.getType()) {
                case TEXT:
                case SERVICE:
                    chatLastMessageText.setText(((TextMessageDto) lastMessage).getText());
                    break;
                case IMAGE:
                    chatLastMessageText.setText(R.string.holder_last_message_image);
                    break;
                case STICKER:
                    chatLastMessageText.setText(R.string.holder_last_message_sticker);
                    break;
            }
            chatLastMessageDate.setText(convertToLocalDateTime(lastMessage.getCreateAt()).format(TIME_FORMATTER));
            String chatProfileAvatarUrl = String.format("%s/api/profile/%s/avatar", Client.getBaseUrl(), item.getTargetProfile().getUsername());
            GlideUrl glideUrl = new GlideUrl(chatProfileAvatarUrl,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + Client.getBarrierToken())
                            .build());
            Glide.with(context).load(glideUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                    .apply(RequestOptions.circleCropTransform()).into(chatProfileAvatar);
            if (lastMessage.isRead()) {
                unreadMark.setImageResource(R.drawable.moon_new);
            } else {
                unreadMark.setImageResource(R.drawable.moon_new_fill);
            }
            return this;
        }

        @Override
        public void onClick(final View view) {
            Intent intent = new Intent(view.getContext(), MessageActivity.class);
            intent.putExtra(IntentConstrains.EXTRA_CHAT_ID, item.getId());
            view.getContext().startActivity(intent);
        }
    }

    static class RepositoryComparator extends DiffUtil.ItemCallback<ChatPreviewDto> {

        @Override
        public boolean areItemsTheSame(@NonNull @NotNull ChatPreviewDto oldItem, @NonNull @NotNull ChatPreviewDto newItem) {
            Log.i("REPO_COMPARATOR", String.format("%s compare with %s", oldItem.getId(), newItem.getId()));
            return Objects.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull ChatPreviewDto oldItem, @NonNull @NotNull ChatPreviewDto newItem) {
            Log.i("REPO_COMPARATOR", String.format("%s compare with %s", oldItem.getId(), newItem.getId()));
            return Objects.equals(oldItem, newItem);
        }
    }

    private static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.of("GMT"))
                .toLocalDateTime();
    }

}
