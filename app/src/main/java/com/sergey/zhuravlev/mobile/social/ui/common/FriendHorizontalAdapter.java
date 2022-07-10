package com.sergey.zhuravlev.mobile.social.ui.common;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sergey.zhuravlev.mobile.social.constrain.IntentConstrains;
import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDto;
import com.sergey.zhuravlev.mobile.social.ui.profile.ProfileActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FriendHorizontalAdapter extends PagingDataAdapter<ProfileDto, RecyclerView.ViewHolder> {

    private final Context context;

    public FriendHorizontalAdapter(Context context) {
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
        return FriendHorizontalViewHolder.getInstance(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        ProfileDto profileDto = getItem(position);
        if (profileDto != null) {
            if (holder instanceof FriendHorizontalViewHolder) {
                ((FriendHorizontalViewHolder) holder).bind(profileDto, context);
            }
        }
    }

    static class FriendHorizontalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView avatarImage;
        private final TextView fullNameText;

        private String username;

        private Context context;

        public FriendHorizontalViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            avatarImage = itemView.findViewById(R.id.avatar);
            fullNameText = itemView.findViewById(R.id.full_name_text_view);
            itemView.setOnClickListener(this);
        }

        public static FriendHorizontalViewHolder getInstance(ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_circle_profile, parent, false);
            return new FriendHorizontalViewHolder(view);
        }

        public FriendHorizontalViewHolder bind(ProfileDto item, Context context) {
            this.context = context;
            this.username = item.getUsername();

            fullNameText.setText(String.format("%s\n%s", item.getFirstName(), item.getSecondName()));
            String messageImageUrl = String.format("%s/api/profile/%s/avatar",
                    Client.getBaseUrl(),
                    item.getUsername());
            GlideUrl glideUrl = new GlideUrl(messageImageUrl,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + Client.getBarrierToken())
                            .build());
            Glide.with(context).load(glideUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                    .apply(RequestOptions.circleCropTransform()).into(avatarImage);
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
