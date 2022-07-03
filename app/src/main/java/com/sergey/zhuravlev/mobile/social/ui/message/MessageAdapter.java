package com.sergey.zhuravlev.mobile.social.ui.message;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.common.collect.Iterables;
import com.sergey.zhuravlev.mobile.social.R;
import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.client.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.client.mapper.MessageModelMapper;
import com.sergey.zhuravlev.mobile.social.database.model.MessageModel;
import com.sergey.zhuravlev.mobile.social.enums.MessageSenderType;
import com.sergey.zhuravlev.mobile.social.ui.common.Item;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public class MessageAdapter extends PagingDataAdapter<Item<MessageModel>, RecyclerView.ViewHolder> {

    private final static int VIEW_TYPE_TEXT = 0;
    private final static int VIEW_TYPE_IMAGE = 1;
    private final static int VIEW_TYPE_SEPARATOR = 100;
    private final static int VIEW_TYPE_PLACEHOLDER = 200;

    private final Context context;
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);
    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    public MessageAdapter(Context context) {
        super(new RepositoryComparator());
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public int getItemViewType(int position) {
        Item<MessageModel> messageItem = getItem(position);
        if (messageItem instanceof Item.RepoItem) {
            switch (((Item.RepoItem<MessageModel>) messageItem).getModel().getType()) {
                case TEXT:
                    return VIEW_TYPE_TEXT;
                case IMAGE:
                    return VIEW_TYPE_IMAGE;
                default:
                    throw new IllegalArgumentException(String.format("Data on position %s isn't supported", position));
            }
        } else if (messageItem instanceof Item.DateSeparatorItem) {
            return VIEW_TYPE_SEPARATOR;
        } else {
            throw new IllegalArgumentException(String.format("Item on position %s isn't supported", position));
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
            case VIEW_TYPE_SEPARATOR:
                return SeparatorViewHolder.getInstance(parent);
            default:
                throw new IllegalArgumentException("ViewType: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessageViewHolder) {
            Item.RepoItem<MessageModel> messageItem = (Item.RepoItem<MessageModel>) getItem(position);
            ((MessageViewHolder) holder).bind(messageItem.getModel(), context);
        } else if (holder instanceof MessageImageViewHolder) {
            Item.RepoItem<MessageModel> messageItem = (Item.RepoItem<MessageModel>) getItem(position);
            ((MessageImageViewHolder) holder).bind(messageItem.getModel(), context);
        } else if (holder instanceof SeparatorViewHolder) {
            Item.DateSeparatorItem<MessageModel> separatorItem = (Item.DateSeparatorItem<MessageModel>) getItem(position);
            ((SeparatorViewHolder) holder).bind(separatorItem.getDate(), context);
        }
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout constraintLayout;
        private final TextView messageText;
        private final TextView messageDate;
        private final ImageView sendErrorImageView;
        private final ProgressBar sendStatusProgressBar;

        public MessageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            constraintLayout = (ConstraintLayout) itemView.getRootView();
            messageText = itemView.findViewById(R.id.message_text_view);
            messageDate = itemView.findViewById(R.id.message_date_text_view);
            sendErrorImageView = itemView.findViewById(R.id.send_error_image_view);
            sendStatusProgressBar = itemView.findViewById(R.id.send_status_progress_bar);
        }

        public static MessageViewHolder getInstance(ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(view);
        }

        public MessageViewHolder bind(MessageModel item, Context context) {
            ConstraintSet cs = new ConstraintSet();
            cs.clone(constraintLayout);
            if (item.getSender() == MessageSenderType.TARGET) {
                cs.setHorizontalBias(R.id.card_view, 0f);
            } else {
                cs.setHorizontalBias(R.id.card_view, 100f);
            }
            cs.applyTo(constraintLayout);
            switch (item.getType()) {
                case TEXT:
                case SERVICE:
                    messageText.setText(item.getText());
                    break;
                case IMAGE:
                    messageText.setText(R.string.holder_last_message_image);
                    break;
                case STICKER:
                    messageText.setText(R.string.holder_last_message_sticker);
                    break;
            }
            messageDate.setText(item.getCreateAt().format(TIME_FORMATTER));
            // Status logic:
            if (item.isPrepend() && !item.isPrependError()) {
                sendStatusProgressBar.setVisibility(View.VISIBLE);
            } else {
                sendStatusProgressBar.setVisibility(View.GONE);
            }
            if (item.isPrependError()) {
                sendErrorImageView.setVisibility(View.VISIBLE);
            } else {
                sendErrorImageView.setVisibility(View.GONE);
            }
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
            messageImage = itemView.findViewById(R.id.message_image_view);
            messageDate = itemView.findViewById(R.id.message_date_text_view);
        }

        public static MessageImageViewHolder getInstance(ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_message_image, parent, false);
            return new MessageImageViewHolder(view);
        }

        public MessageImageViewHolder bind(MessageModel item, Context context) {
            ConstraintSet cs = new ConstraintSet();
            cs.clone(constraintLayout);
            if (item.getSender() == MessageSenderType.TARGET) {
                cs.setHorizontalBias(R.id.card_view, 0f);
            } else {
                cs.setHorizontalBias(R.id.card_view, 100f);
            }
            cs.applyTo(constraintLayout);

            messageDate.setText(item.getCreateAt().format(TIME_FORMATTER));

            if (!item.isPrepend()) {
                String messageImageUrl = String.format("%s/api/chat/%s/message/%s/image_preview",
                        Client.getBaseUrl(),
                        item.getChatId(),
                        item.getNetworkId());
                GlideUrl glideUrl = new GlideUrl(messageImageUrl,
                        new LazyHeaders.Builder()
                                .addHeader("Authorization", "Bearer " + Client.getBarrierToken())
                                .build());
                Glide.with(context).load(glideUrl)
                        .signature(new ObjectKey(messageImageUrl))
                        .centerCrop()
                        .into(messageImage);
            } else {
                Uri uri = Uri.parse(item.getGlideSignature());
                ObjectKey objectKey = new ObjectKey(item.getGlideSignature());
                Glide.with(context).asBitmap()
                        .load(uri)
                        .signature(objectKey)
                        .onlyRetrieveFromCache(true)
                        .into(messageImage);
            }
            return this;
        }
    }

    static class SeparatorViewHolder extends RecyclerView.ViewHolder {

        private final TextView dateTextView;

        public SeparatorViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_text_view);
        }

        public static SeparatorViewHolder getInstance(ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_date_separator, parent, false);
            return new SeparatorViewHolder(view);
        }

        public SeparatorViewHolder bind(LocalDate item, Context context) {
            dateTextView.setText(item.format(DATE_FORMATTER));
            return this;
        }
    }

    static class RepositoryComparator extends DiffUtil.ItemCallback<Item<MessageModel>> {

        @Override
        public boolean areItemsTheSame(@NonNull Item<MessageModel> oldItem, @NonNull Item<MessageModel> newItem) {
            boolean isSameChatPreviewModel = oldItem instanceof Item.RepoItem
                    && newItem instanceof Item.RepoItem
                    && Objects.equals(
                    ((Item.RepoItem<MessageModel>) oldItem).getModel().getId(),
                    ((Item.RepoItem<MessageModel>) newItem).getModel().getId());

            boolean isSameSeparator = oldItem instanceof Item.DateSeparatorItem
                    && newItem instanceof Item.DateSeparatorItem
                    && Objects.equals(
                    ((Item.DateSeparatorItem<MessageModel>) oldItem).getDate(),
                    ((Item.DateSeparatorItem<MessageModel>) newItem).getDate());

            return isSameChatPreviewModel || isSameSeparator;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Item<MessageModel> oldItem, @NonNull Item<MessageModel> newItem) {
            boolean isSameChatPreviewModel = oldItem instanceof Item.RepoItem
                    && newItem instanceof Item.RepoItem
                    && Objects.equals(
                    ((Item.RepoItem<MessageModel>) oldItem).getModel(),
                    ((Item.RepoItem<MessageModel>) newItem).getModel());

            boolean isSameSeparator = oldItem instanceof Item.DateSeparatorItem
                    && newItem instanceof Item.DateSeparatorItem
                    && Objects.equals(
                    ((Item.DateSeparatorItem<MessageModel>) oldItem).getDate(),
                    ((Item.DateSeparatorItem<MessageModel>) newItem).getDate());

            return isSameChatPreviewModel || isSameSeparator;
        }
    }

}