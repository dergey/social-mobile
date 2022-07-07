package com.sergey.zhuravlev.mobile.social.ui.profile;

import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDto;
import com.sergey.zhuravlev.mobile.social.constrain.ActivityCodes;
import com.sergey.zhuravlev.mobile.social.constrain.IntentConstrains;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityProfileBinding;
import com.sergey.zhuravlev.mobile.social.enums.ErrorCode;
import com.sergey.zhuravlev.mobile.social.ui.common.ProfilesHorizontalListAdapter;
import com.sergey.zhuravlev.mobile.social.util.GlideUtils;
import com.sergey.zhuravlev.mobile.social.util.PrefetchedPagingData;
import com.sergey.zhuravlev.mobile.social.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private ProfilesHorizontalListAdapter adapter;

    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ENGLISH);
    private final static DateTimeFormatter FULLY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
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
        TextView fullNameTextView = binding.fullnameTextView;
        TextView cityTextView = binding.cityTextView;
        TextView joinedTextView = binding.joinedTextView;
        ImageView avatarImageView = binding.avatarImageView;

        TextView overviewTextView = binding.overviewTextView;
        ConstraintLayout overviewSubcardLayout = binding.overviewSubcardLayout;
        TextView bornValueTextView = binding.bornValueTextView;
        ConstraintLayout bornSubcardLayout = binding.bornSubcardLayout;
        TextView statusValueTextView = binding.statusValueTextView;
        ConstraintLayout statusSubcardLayout = binding.statusSubcardLayout;

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

            fullNameTextView.setText(Stream.of(currentProfile.getFirstName(),
                            currentProfile.getMiddleName(), currentProfile.getSecondName())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(" ")));

            joinedTextView.setText(getString(R.string.profile_joined_on, currentProfile.getCreateAt().format(DATE_FORMATTER)));
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
        TextView allCountTextView = binding.allCountTextView;

        ConstraintLayout friendLayout = binding.constraintLayoutCardFriend;
        TextView friendCountTextView = binding.friendCountTextView;

        adapter = new ProfilesHorizontalListAdapter(this);
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
            allCountTextView.setText("0 Subscribers | " + page.getTotalElements() + " Friends");
        });

        // Profile information animation initializing:

        Button showUpButton = binding.showUpButton;
        ConstraintLayout constraintLayoutCardInfo = binding.constraintLayoutCardInfo;

        ConstraintSet showedConstraintSet = new ConstraintSet();
        ConstraintSet collapsedConstraintSet = new ConstraintSet();
        showedConstraintSet.clone(constraintLayoutCardInfo);
        collapsedConstraintSet.clone(constraintLayoutCardInfo);
        collapsedConstraintSet.setVisibility(R.id.show_up_button, ConstraintSet.VISIBLE);
        collapsedConstraintSet.setVisibility(R.id.collapsing_profile_info_shadow, ConstraintSet.VISIBLE);
        collapsedConstraintSet.constrainMaxHeight(R.id.collapsing_profile_info_layout, 200);
        collapsedConstraintSet.applyTo(constraintLayoutCardInfo);
        showUpButton.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(constraintLayoutCardInfo);
            showedConstraintSet.applyTo(constraintLayoutCardInfo);
        });

        // Fetch data:
        profileViewModel.getProfile(username);
        profileViewModel.fetchProfileFriendLiveData(username).observe(this, pagingData ->
                adapter.submitData(getLifecycle(), pagingData)
        );
    }
}