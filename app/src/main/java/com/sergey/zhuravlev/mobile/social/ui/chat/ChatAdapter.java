package com.sergey.zhuravlev.mobile.social.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.sergey.zhuravlev.mobile.social.R;
import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.constrain.IntentConstrains;
import com.sergey.zhuravlev.mobile.social.database.model.ChatAndLastMessageModel;
import com.sergey.zhuravlev.mobile.social.database.model.ChatModel;
import com.sergey.zhuravlev.mobile.social.database.model.MessageModel;
import com.sergey.zhuravlev.mobile.social.enums.MessageSenderType;
import com.sergey.zhuravlev.mobile.social.ui.message.MessageActivity;

import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

@ExperimentalPagingApi
public class ChatAdapter extends PagingDataAdapter<ChatAndLastMessageModel, RecyclerView.ViewHolder> {

    private final Context context;
    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

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
        ChatAndLastMessageModel chatAndLastMessage = getItem(position);
        if (holder instanceof ChatViewHolder && chatAndLastMessage != null) {
            ((ChatViewHolder) holder).bind(chatAndLastMessage, context);
        }
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ChatModel chat;
        private MessageModel lastMessage;

        private final ImageView chatProfileAvatar;
        private final TextView chatTitle;
        private final TextView chatLastMessageText;
        private final TextView chatLastMessageDate;
        private final TextView unreadCountTextView;
        private final ImageView readStatusImageView;

        public ChatViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            chatProfileAvatar = itemView.findViewById(R.id.avatar);
            chatTitle = itemView.findViewById(R.id.chatTitle);
            chatLastMessageText = itemView.findViewById(R.id.lastMessageText);
            chatLastMessageDate = itemView.findViewById(R.id.lastMessageDate);
            unreadCountTextView = itemView.findViewById(R.id.unread_count_text_view);
            readStatusImageView = itemView.findViewById(R.id.read_status_image_view);
        }

        public static ChatViewHolder getInstance(ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_chat, parent, false);
            ChatViewHolder chatViewHolder = new ChatViewHolder(view);
            view.setOnClickListener(chatViewHolder);
            return chatViewHolder;
        }

        public ChatViewHolder bind(ChatAndLastMessageModel item, Context context) {
            this.chat = item.getChat();
            this.lastMessage = item.getLastMessage();

            chatTitle.setText(String.format("%s %s", chat.getTargetProfile().getFirstName(), chat.getTargetProfile().getSecondName()));

            if (lastMessage != null) {
                switch (lastMessage.getType()) {
                    case TEXT:
                    case SERVICE:
                        chatLastMessageText.setText(lastMessage.getText());
                        break;
                    case IMAGE:
                        chatLastMessageText.setText(R.string.holder_last_message_image);
                        break;
                    case STICKER:
                        chatLastMessageText.setText(R.string.holder_last_message_sticker);
                        break;
                }
                chatLastMessageDate.setText(lastMessage.getCreateAt().format(TIME_FORMATTER));
                if (chat.getUnreadMessages() > 0) {
                    unreadCountTextView.setText(String.valueOf(chat.getUnreadMessages()));
                    unreadCountTextView.setVisibility(View.VISIBLE);
                } else {
                    unreadCountTextView.setVisibility(View.GONE);
                }
                if (lastMessage.getSender() == MessageSenderType.SOURCE) {
                    if (lastMessage.isRead()) {
                        readStatusImageView.setImageResource(R.drawable.ic_round_done_all_24);
                    } else {
                        readStatusImageView.setImageResource(R.drawable.ic_round_done_24);
                    }
                    readStatusImageView.setVisibility(View.VISIBLE);
                } else {
                    readStatusImageView.setVisibility(View.GONE);
                }

            }
            String chatProfileAvatarUrl = String.format("%s/api/profile/%s/avatar", Client.getBaseUrl(), chat.getTargetProfile().getUsername());
            GlideUrl glideUrl = new GlideUrl(chatProfileAvatarUrl,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + Client.getBarrierToken())
                            .build());
            //todo add an update when the Internet is connected
            Glide.with(context).load(glideUrl)
                    .apply(RequestOptions.circleCropTransform()).into(chatProfileAvatar);
            return this;
        }

        @Override
        public void onClick(final View view) {
            Intent intent = new Intent(view.getContext(), MessageActivity.class);
            intent.putExtra(IntentConstrains.EXTRA_CHAT, chat);
            view.getContext().startActivity(intent);
        }
    }

    static class RepositoryComparator extends DiffUtil.ItemCallback<ChatAndLastMessageModel> {

        @Override
        public boolean areItemsTheSame(@NonNull @NotNull ChatAndLastMessageModel oldItem, @NonNull @NotNull ChatAndLastMessageModel newItem) {
            return Objects.equals(oldItem.getChat().getId(), newItem.getChat().getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull ChatAndLastMessageModel oldItem, @NonNull @NotNull ChatAndLastMessageModel newItem) {
            return Objects.equals(oldItem.getChat(), newItem.getChat());
        }
    }

}
