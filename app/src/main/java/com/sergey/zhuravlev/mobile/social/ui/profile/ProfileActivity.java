package com.sergey.zhuravlev.mobile.social.ui.profile;

import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.sergey.zhuravlev.mobile.social.R;
import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDetailDto;
import com.sergey.zhuravlev.mobile.social.constrain.IntentConstrains;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityProfileBinding;
import com.sergey.zhuravlev.mobile.social.ui.common.FriendHorizontalAdapter;
import com.sergey.zhuravlev.mobile.social.ui.message.MessageActivity;
import com.sergey.zhuravlev.mobile.social.util.GlideUtils;
import com.sergey.zhuravlev.mobile.social.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private FriendHorizontalAdapter adapter;

    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ENGLISH);
    private final static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy hh:mm:ss", Locale.ENGLISH);
    private final static DateTimeFormatter FULLY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileViewModel = new ViewModelProvider(this, new ProfileViewModelFactory(this))
                .get(ProfileViewModel.class);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getExtras() == null) {
            finish();
        }

        String username = getIntent().getExtras().getString(IntentConstrains.EXTRA_PROFILE_USERNAME);
        if (StringUtils.isBlank(username)) {
            finish();
        }

        // Header initializing:
        ImageView backImageView = binding.backImageView;
        ImageView menuImageView = binding.menuImageView;

        ConstraintLayout constraintLayoutMain = binding.constraintLayoutMain;
        TextView fullNameTextView = binding.fullnameTextView;
        TextView statusTextView = binding.statusTextView;
        TextView cityTextView = binding.cityTextView;
        TextView joinedTextView = binding.joinedTextView;
        ImageView avatarImageView = binding.avatarImageView;

        TextView overviewTextView = binding.overviewTextView;
        ConstraintLayout overviewSubcardLayout = binding.overviewSubcardLayout;
        TextView bornValueTextView = binding.bornValueTextView;
        ConstraintLayout bornSubcardLayout = binding.bornSubcardLayout;
        TextView statusValueTextView = binding.statusValueTextView;
        ConstraintLayout statusSubcardLayout = binding.statusSubcardLayout;

        backImageView.setOnClickListener(v -> ProfileActivity.super.onBackPressed());

        PopupMenu.OnMenuItemClickListener onMenuItemClickListener = item -> {

            return false;
        };
        menuImageView.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(ProfileActivity.this, v);
            popup.setOnMenuItemClickListener(onMenuItemClickListener);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.profile_menu, popup.getMenu());
            popup.show();
        });

        profileViewModel.getProfileResult().observe(this, currentProfileResult -> {
            if (currentProfileResult == null) {
                return;
            }

            if (currentProfileResult.isHasErrors()) {
//                if (currentProfileResult.getErrorDto() != null && Objects.equals(
//                        currentProfileResult.getErrorDto().getCode(), ErrorCode.UNAUTHORIZED)) {
//                    activityCallback.onFragmentEvent(ActivityCodes.TOKEN_EXPIRED_CODE);
//                }
                return;
            }

            if (currentProfileResult.getData() == null) {
                return;
            }
            ProfileDetailDto currentProfile = currentProfileResult.getData();

            // Setting values for the top header:
            ConstraintSet normalConstraintSet = new ConstraintSet();
            normalConstraintSet.clone(constraintLayoutMain);
            normalConstraintSet.constrainWidth(fullNameTextView.getId(), ConstraintSet.WRAP_CONTENT);
            normalConstraintSet.constrainedWidth(fullNameTextView.getId(), true);
            normalConstraintSet.constrainWidth(statusTextView.getId(), ConstraintSet.WRAP_CONTENT);
            normalConstraintSet.constrainedWidth(statusTextView.getId(), true);
            normalConstraintSet.constrainWidth(joinedTextView.getId(), ConstraintSet.WRAP_CONTENT);
            normalConstraintSet.constrainedWidth(joinedTextView.getId(), true);
            normalConstraintSet.constrainWidth(cityTextView.getId(), ConstraintSet.WRAP_CONTENT);
            normalConstraintSet.constrainedWidth(cityTextView.getId(), true);
            fullNameTextView.setBackground(null);
            statusTextView.setBackground(null);
            joinedTextView.setBackground(null);
            cityTextView.setBackground(null);

            normalConstraintSet.applyTo(constraintLayoutMain);

            fullNameTextView.setText(Stream.of(currentProfile.getFirstName(),
                            currentProfile.getMiddleName(), currentProfile.getSecondName())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(" ")));

            statusTextView.setText(String.format("Last seen %s", currentProfile.getLastSeen().format(DATETIME_FORMATTER)));

            joinedTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_calendar_today_16, 0, 0, 0);
            joinedTextView.setText(getString(R.string.profile_joined_on, currentProfile.getCreateAt().format(DATE_FORMATTER)));

            cityTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_location_on_24, 0, 0, 0);
            cityTextView.setText(currentProfile.getCity());

            if (currentProfile.getOverview() != null) {
                overviewTextView.setText(currentProfile.getOverview());
                overviewSubcardLayout.setVisibility(View.VISIBLE);
            } else {
                overviewSubcardLayout.setVisibility(View.GONE);
            }
            if (currentProfile.getBirthDate() != null) {
                bornValueTextView.setText(currentProfile.getBirthDate().format(FULLY_DATE_FORMATTER));
                bornSubcardLayout.setVisibility(View.VISIBLE);
            } else {
                bornSubcardLayout.setVisibility(View.GONE);
            }
            if (currentProfile.getRelationshipStatus() != null) {
                statusValueTextView.setText(currentProfile.getRelationshipStatus().toString().charAt(0) +
                        currentProfile.getRelationshipStatus().toString().toLowerCase().substring(1));
                statusSubcardLayout.setVisibility(View.VISIBLE);
            } else {
                statusSubcardLayout.setVisibility(View.GONE);
            }

            String profileAvatarUrl = String.format("%s/api/profile/%s/avatar", Client.getBaseUrl(),
                    currentProfileResult.getData().getUsername());
            GlideUrl glideUrl = new GlideUrl(profileAvatarUrl,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + Client.getBarrierToken())
                            .build());
            Glide.with(this).load(glideUrl)
                    .signature(GlideUtils.getMediaStoreSignature(currentProfileResult.getData().getAvatar()))
                    .circleCrop()
                    .into(avatarImageView);
        });

        // Friends card initializing:

        ConstraintLayout friendLayout = binding.constraintLayoutCardFriend;
        TextView friendCountTextView = binding.friendCountTextView;

        adapter = new FriendHorizontalAdapter(this);
        adapter.addLoadStateListener(loadState -> {
            if (loadState.getRefresh() instanceof LoadState.Loading) {
                friendLayout.setVisibility(View.GONE);
            } else if (loadState.getRefresh() instanceof LoadState.NotLoading) {
                friendLayout.setVisibility(View.VISIBLE);
            }
            return null;
        });

        RecyclerView friendRecyclerView = binding.friendListRecyclerView;
        friendRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        friendRecyclerView.setAdapter(adapter);
        profileViewModel.getProfileFriendPage().observe(this, page -> {
            friendCountTextView.setText(String.valueOf(page.getTotalElements()));
        });

        // Fetch data:
        profileViewModel.getProfile(username);
        profileViewModel.fetchProfileFriendLiveData(username).observe(this, pagingData ->
                adapter.submitData(getLifecycle(), pagingData)
        );
    }
}