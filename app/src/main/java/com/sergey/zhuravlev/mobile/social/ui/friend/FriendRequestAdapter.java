package com.sergey.zhuravlev.mobile.social.ui.friend;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDto;
import com.sergey.zhuravlev.mobile.social.constrain.IntentConstrains;
import com.sergey.zhuravlev.mobile.social.ui.profile.ProfileActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FriendRequestAdapter extends PagingDataAdapter<ProfileDto, RecyclerView.ViewHolder> {

    interface OnItemButtonClickListener {
        boolean onItemButtonClick(View v, ProfileDto item);
    }

    private final Context context;
    private OnItemButtonClickListener onAcceptClickListener;
    private OnItemButtonClickListener onRejectClickListener;

    public FriendRequestAdapter(Context context) {
        super(new RepositoryComparator());
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setOnAcceptClickListener(OnItemButtonClickListener onAcceptClickListener) {
        this.onAcceptClickListener = onAcceptClickListener;
    }

    public void setOnRejectClickListener(OnItemButtonClickListener onRejectClickListener) {
        this.onRejectClickListener = onRejectClickListener;
    }


    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return FriendRequestViewHolder.getInstance(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        ProfileDto profileDto = getItem(position);
        if (profileDto != null) {
            if (holder instanceof FriendRequestViewHolder) {
                ((FriendRequestViewHolder) holder).bind(profileDto, context, onAcceptClickListener, onRejectClickListener);
            }
        }
    }

    static class FriendRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView avatarImageView;
        private final TextView fullNameTextView;
        private final Button acceptButton;
        private final Button rejectButton;

        private String username;

        private Context context;

        public FriendRequestViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatar_image_view);
            fullNameTextView = itemView.findViewById(R.id.full_name_text_view);
            acceptButton = itemView.findViewById(R.id.accept_button);
            rejectButton = itemView.findViewById(R.id.reject_button);
            itemView.setOnClickListener(this);
        }

        public static FriendRequestViewHolder getInstance(ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_friend_request, parent, false);
            return new FriendRequestViewHolder(view);
        }

        public FriendRequestViewHolder bind(ProfileDto item, Context context,
                                            OnItemButtonClickListener onAcceptClickListener,
                                            OnItemButtonClickListener onRejectClickListener) {
            this.context = context;
            this.username = item.getUsername();

            fullNameTextView.setText(Stream.of(item.getFirstName(), item.getMiddleName(), item.getSecondName())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(" ")));
            String messageImageUrl = String.format("%s/api/profile/%s/avatar",
                    Client.getBaseUrl(),
                    item.getUsername());
            GlideUrl glideUrl = new GlideUrl(messageImageUrl,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + Client.getBarrierToken())
                            .build());
            Glide.with(context).load(glideUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                    .apply(RequestOptions.circleCropTransform()).into(avatarImageView);
            acceptButton.setOnClickListener(v -> onAcceptClickListener.onItemButtonClick(v, item));
            rejectButton.setOnClickListener(v -> onRejectClickListener.onItemButtonClick(v, item));
            return this;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra(IntentConstrains.EXTRA_PROFILE_USERNAME, username);
            context.startActivity(intent);
        }
    }

    static class RepositoryComparator extends DiffUtil.ItemCallback<ProfileDto> {

        @Override
        public boolean areItemsTheSame(@NonNull @NotNull ProfileDto oldItem, @NonNull @NotNull ProfileDto newItem) {
            return Objects.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull ProfileDto oldItem, @NonNull @NotNull ProfileDto newItem) {
            return Objects.equals(oldItem, newItem);
        }
    }

}
