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
import com.sergey.zhuravlev.mobile.social.dto.profile.ProfileDto;
import com.sergey.zhuravlev.mobile.social.ui.profile.ProfileActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ProfilesHorizontalListAdapter extends PagingDataAdapter<ProfileDto, RecyclerView.ViewHolder> {

    private final Context context;

    public ProfilesHorizontalListAdapter(Context context) {
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
        return ProfilesHorizontalListAdapter.ProfileViewHolder.getInstance(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        ProfileDto profileDto = getItem(position);
        if (holder instanceof ProfilesHorizontalListAdapter.ProfileViewHolder && profileDto != null) {
            ((ProfilesHorizontalListAdapter.ProfileViewHolder) holder).bind(profileDto, context);
        }
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView avatarImage;
        private final TextView fullNameText;

        private Context context;
        private String username;

        public ProfileViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            avatarImage = itemView.findViewById(R.id.avatar);
            fullNameText = itemView.findViewById(R.id.full_name_text_view);
            itemView.setOnClickListener(this);
        }

        public static ProfilesHorizontalListAdapter.ProfileViewHolder getInstance(ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_circle_profile, parent, false);
            return new ProfilesHorizontalListAdapter.ProfileViewHolder(view);
        }

        public ProfilesHorizontalListAdapter.ProfileViewHolder bind(ProfileDto item, Context context) {
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
