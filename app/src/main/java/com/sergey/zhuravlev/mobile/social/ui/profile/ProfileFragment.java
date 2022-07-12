package com.sergey.zhuravlev.mobile.social.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.ExperimentalPagingApi;
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
import com.sergey.zhuravlev.mobile.social.database.model.ProfileAndDetailModel;
import com.sergey.zhuravlev.mobile.social.database.model.ProfileDetailModel;
import com.sergey.zhuravlev.mobile.social.database.model.ProfileModel;
import com.sergey.zhuravlev.mobile.social.databinding.FragmentProfileBinding;
import com.sergey.zhuravlev.mobile.social.enums.ErrorCode;
import com.sergey.zhuravlev.mobile.social.ui.chat.ChatViewModel;
import com.sergey.zhuravlev.mobile.social.ui.chat.ChatViewModelFactory;
import com.sergey.zhuravlev.mobile.social.ui.common.FriendHorizontalAdapter;
import com.sergey.zhuravlev.mobile.social.ui.common.UiNetworkResult;
import com.sergey.zhuravlev.mobile.social.ui.common.UiResult;
import com.sergey.zhuravlev.mobile.social.ui.friend.FriendActivity;
import com.sergey.zhuravlev.mobile.social.util.FragmentCallable;
import com.sergey.zhuravlev.mobile.social.util.GlideUtils;
import com.sergey.zhuravlev.mobile.social.util.PrefetchedPagingData;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExperimentalPagingApi
public class ProfileFragment extends Fragment {

    private FragmentCallable activityCallback;

    private FragmentProfileBinding binding;
    private ProfileFragmentViewModel profileFragmentViewModel;

    private FriendHorizontalAdapter adapter;

    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ENGLISH);
    private final static DateTimeFormatter FULLY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileFragmentViewModel = new ViewModelProvider(this, new ProfileFragmentViewModelFactory(this.getActivity()))
                .get(ProfileFragmentViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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


        Observer<UiResult<ProfileAndDetailModel>> currentProfileObserver = currentProfileResult -> {
            if (currentProfileResult == null) {
                return;
            }

            if (currentProfileResult.isHasErrors()) {
                if (currentProfileResult instanceof UiNetworkResult &&
                        ((UiNetworkResult<?>) currentProfileResult).getErrorDto() != null && Objects.equals(
                                ((UiNetworkResult<?>) currentProfileResult).getErrorDto().getCode(), ErrorCode.UNAUTHORIZED)) {
                        activityCallback.onFragmentEvent(ActivityCodes.TOKEN_EXPIRED_CODE);
                    }
                return;
            }

            if (currentProfileResult.getData() == null) {
                return;
            }

            ProfileModel profileModel = currentProfileResult.getData().getProfile();
            ProfileDetailModel detailModel = currentProfileResult.getData().getDetail();

            fullNameTextView.setText(Stream.of(profileModel.getFirstName(),
                            profileModel.getMiddleName(), profileModel.getSecondName())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(" ")));

            joinedTextView.setText(getString(R.string.profile_joined_on, detailModel.getCreateAt().format(DATE_FORMATTER)));
            cityTextView.setText(detailModel.getCity());

            if (detailModel.getOverview() != null) {
                overviewTextView.setText(detailModel.getOverview());
                overviewSubcardLayout.setVisibility(View.VISIBLE);
            } else {
                overviewSubcardLayout.setVisibility(View.GONE);
            }
            if (detailModel.getBirthDate() != null) {
                bornValueTextView.setText(detailModel.getBirthDate().format(FULLY_DATE_FORMATTER));
                bornSubcardLayout.setVisibility(View.VISIBLE);
            } else {
                bornSubcardLayout.setVisibility(View.GONE);
            }
            if (detailModel.getRelationshipStatus() != null) {
                statusValueTextView.setText(detailModel.getRelationshipStatus().toString().charAt(0) +
                        detailModel.getRelationshipStatus().toString().toLowerCase().substring(1));
                statusSubcardLayout.setVisibility(View.VISIBLE);
            } else {
                statusSubcardLayout.setVisibility(View.GONE);
            }

            String profileAvatarUrl = String.format("%s/api/profile/%s/avatar", Client.getBaseUrl(),
                    profileModel.getUsername());
            GlideUrl glideUrl = new GlideUrl(profileAvatarUrl,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + Client.getBarrierToken())
                            .build());
            if (getContext() != null) {
                Glide.with(getContext()).load(glideUrl)
                        //todo .signature(GlideUtils.getMediaStoreSignature(detailModel.getAvatar()))
                        .circleCrop()
                        .into(avatarImageView);
            }

        };
        profileFragmentViewModel.getCacheCurrentProfileResult().observe(getActivity(), currentProfileObserver);
        profileFragmentViewModel.getNetworkCurrentProfileResult().observe(getActivity(), currentProfileObserver);


        // Friends card initializing:
        TextView allCountTextView = binding.allCountTextView;

        ConstraintLayout friendLayout = binding.constraintLayoutCardFriend;
        Button findFriendButton = binding.findFriendButton;
        TextView friendCountTextView = binding.friendCountTextView;
        TextView friendRequestCountTextView = binding.friendRequestCountTextView;
        ConstraintLayout friendOpenLayout = binding.friendOpenLayout;

        adapter = new FriendHorizontalAdapter(getActivity());
        adapter.addLoadStateListener(loadState -> {
            if (loadState.getRefresh() instanceof LoadState.Loading) {
                friendLayout.setVisibility(View.GONE);
            } else if (loadState.getRefresh() instanceof LoadState.NotLoading) {
                friendLayout.setVisibility(View.VISIBLE);
                if (adapter.getItemCount() <= 0) {
                    findFriendButton.setVisibility(View.VISIBLE);
                } else {
                    findFriendButton.setVisibility(View.INVISIBLE);
                }
            }
            return null;
        });

        RecyclerView friendRecyclerView = binding.friendListRecyclerView;
        friendRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        friendRecyclerView.setAdapter(adapter);
        profileFragmentViewModel.getCurrentFriendPage().observe(getActivity(), page -> {
            friendCountTextView.setText(String.valueOf(page.getTotalElements()));
            allCountTextView.setText("0 Subscribers | " + page.getTotalElements() + " Friends");
        });
        profileFragmentViewModel.getCurrentFriendRequestPage().observe(getActivity(), page -> {
            if (page.getTotalElements() > 0) {
                friendRequestCountTextView.setText(String.valueOf(page.getTotalElements()));
                friendRequestCountTextView.setVisibility(View.VISIBLE);
                friendOpenLayout.setBackgroundResource(R.drawable.background_error_layout);
            } else {
                friendRequestCountTextView.setVisibility(View.INVISIBLE);
                friendOpenLayout.setBackground(null);
            }
        });
        friendOpenLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FriendActivity.class);
            startActivity(intent);
        });

        // Fetch data:
        profileFragmentViewModel.getCurrentProfile();
        profileFragmentViewModel.fetchCurrentUserFriendLiveData().observe(getActivity(), pagingData ->
                adapter.submitData(getLifecycle(), pagingData)
        );

        PrefetchedPagingData<ProfileDto> prefetchedPagingData = new PrefetchedPagingData<>();
        profileFragmentViewModel.fetchCurrentUserFriendRequestLiveData().observe(getActivity(),
                pagingData -> prefetchedPagingData.submitData(getLifecycle(), pagingData));

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activityCallback = (FragmentCallable) context;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}