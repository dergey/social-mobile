package com.sergey.zhuravlev.mobile.social.ui.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.sergey.zhuravlev.mobile.social.R;
import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.dto.enums.MessageSenderType;
import com.sergey.zhuravlev.mobile.social.dto.message.ImageMessageDto;
import com.sergey.zhuravlev.mobile.social.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.dto.message.TextMessageDto;
import com.sergey.zhuravlev.mobile.social.util.GlideUtils;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class MessageAdapter extends PagingDataAdapter<MessageDto, RecyclerView.ViewHolder> {

    private final static int VIEW_TYPE_TEXT = 0;
    private final static int VIEW_TYPE_IMAGE = 1;

    private final Context context;
    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());

    public MessageAdapter(Context context) {
        super(new RepositoryComparator());
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public int getItemViewType(int position) {
        MessageDto messageDto = getItem(position);
        if (messageDto == null) {
            return 0;
        }
        switch (messageDto.getType()) {
            case TEXT:
                return VIEW_TYPE_TEXT;
            case IMAGE:
                return VIEW_TYPE_IMAGE;
            default:
                return 0;
        }
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_TEXT:
                return MessageViewHolder.getInstance(parent);
            case VIEW_TYPE_IMAGE:
                return MessageImageViewHolder.getInstance(parent);
            default:
                throw new IllegalArgumentException("ViewType: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        MessageDto message = getItem(position);
        if (holder instanceof MessageViewHolder && message != null) {
            ((MessageViewHolder) holder).bind(message, context);
        } else if (holder instanceof MessageImageViewHolder && message instanceof ImageMessageDto) {
            ((MessageImageViewHolder) holder).bind((ImageMessageDto) message, context);
        }
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout constraintLayout;
        private final TextView messageText;
        private final TextView messageDate;

        public MessageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            constraintLayout = (ConstraintLayout) itemView.getRootView();
            messageText = itemView.findViewById(R.id.messageText);
            messageDate = itemView.findViewById(R.id.messageDate);
        }

        public static MessageViewHolder getInstance(ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(view);
        }

        public MessageViewHolder bind(MessageDto item, Context context) {
            ConstraintSet cs = new ConstraintSet();
            cs.clone(constraintLayout);
            if (item.getSender() == MessageSenderType.TARGET) {
                cs.setHorizontalBias(R.id.cardView, 0f);
            } else {
                cs.setHorizontalBias(R.id.cardView, 100f);
            }
            cs.applyTo(constraintLayout);
            switch (item.getType()) {
                case TEXT:
                case SERVICE:
                    messageText.setText(((TextMessageDto) item).getText());
                    break;
                case IMAGE:
                    messageText.setText(R.string.holder_last_message_image);
                    break;
                case STICKER:
                    messageText.setText(R.string.holder_last_message_sticker);
                    break;
            }
            messageDate.setText(convertToLocalDateTime(item.getCreateAt()).format(TIME_FORMATTER));
//            String chatProfileAvatarUrl = String.format("%s/api/profile/%s/avatar", Client.getBaseUrl(), item.getTargetProfile().getUsername());
//            GlideUrl glideUrl = new GlideUrl(chatProfileAvatarUrl,
//                    new LazyHeaders.Builder()
//                            .addHeader("Authorization", "Bearer " + Client.getBarrierToken())
//                            .build());
            //Glide.with(context).load(glideUrl).apply(RequestOptions.circleCropTransform()).into(chatProfileAvatar);
            return this;
        }
    }

    static class MessageImageViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout constraintLayout;
        private final ImageView messageImage;
        private final TextView messageDate;

        public MessageImageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            constraintLayout = (ConstraintLayout) itemView.getRootView();
            messageImage = itemView.findViewById(R.id.messageImage);
            messageDate = itemView.findViewById(R.id.messageDate);
        }

        public static MessageImageViewHolder getInstance(ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_message_image, parent, false);
            return new MessageImageViewHolder(view);
        }

        public MessageImageViewHolder bind(ImageMessageDto item, Context context) {
            ConstraintSet cs = new ConstraintSet();
            cs.clone(constraintLayout);
            if (item.getSender() == MessageSenderType.TARGET) {
                cs.setHorizontalBias(R.id.cardView, 0f);
            } else {
                cs.setHorizontalBias(R.id.cardView, 100f);
            }
            cs.applyTo(constraintLayout);

            messageDate.setText(convertToLocalDateTime(item.getCreateAt()).format(TIME_FORMATTER));

            String messageImageUrl = String.format("%s/api/chat/%s/message/%s/image_preview",
                    Client.getBaseUrl(),
                    item.getChatId(),
                    item.getId());
            GlideUrl glideUrl = new GlideUrl(messageImageUrl,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + Client.getBarrierToken())
                            .build());
            Glide.with(context).load(glideUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                    .apply(RequestOptions.centerCropTransform()).into(messageImage);
            return this;
        }
    }

    static class RepositoryComparator extends DiffUtil.ItemCallback<MessageDto> {

        @Override
        public boolean areItemsTheSame(@NonNull @NotNull MessageDto oldItem, @NonNull @NotNull MessageDto newItem) {
            return Objects.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull MessageDto oldItem, @NonNull @NotNull MessageDto newItem) {
            return Objects.equals(oldItem, newItem);
        }
    }

    private static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.of("GMT"))
                .toLocalDateTime();
    }

}