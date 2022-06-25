package com.sergey.zhuravlev.mobile.social.ui.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.constrain.ActivityCodes;
import com.sergey.zhuravlev.mobile.social.databinding.FragmentProfileBinding;
import com.sergey.zhuravlev.mobile.social.enums.ErrorCode;
import com.sergey.zhuravlev.mobile.social.ui.common.ProfilesHorizontalListAdapter;
import com.sergey.zhuravlev.mobile.social.ui.profile.result.GetCurrentProfileResult;
import com.sergey.zhuravlev.mobile.social.util.FragmentCallable;
import com.sergey.zhuravlev.mobile.social.util.GlideUtils;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProfileFragment extends Fragment {

    private FragmentCallable activityCallback;

    private FragmentProfileBinding binding;
    private ProfileFragmentViewModel profileFragmentViewModel;

    private ProfilesHorizontalListAdapter adapter;

    private RecyclerView recyclerView;
    private TextView fullNameTextView;
    private ImageView avatarImageView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileFragmentViewModel = new ViewModelProvider(this).get(ProfileFragmentViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adapter = new ProfilesHorizontalListAdapter(getActivity());

        recyclerView = binding.friendListRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        fullNameTextView = binding.fullNameTextView;
        avatarImageView = binding.avatarImageView;
        profileFragmentViewModel.fetchCurrentUserFriendLiveData().observe(getActivity(), pagingData ->
                adapter.submitData(getLifecycle(), pagingData)
        );
        profileFragmentViewModel.getCurrentProfileResult().observe(getActivity(), new Observer<GetCurrentProfileResult>() {
            @Override
            public void onChanged(@Nullable GetCurrentProfileResult currentProfileResult) {
                if (currentProfileResult == null) {
                    return;
                }

                if (currentProfileResult.isHasErrors()) {
                    if (currentProfileResult.getErrorDto() != null && Objects.equals(
                            currentProfileResult.getErrorDto().getCode(), ErrorCode.UNAUTHORIZED)) {
                            activityCallback.onFragmentEvent(ActivityCodes.TOKEN_EXPIRED_CODE);
                    }
                    return;
                }

                fullNameTextView.setText(Stream.of(
                        currentProfileResult.getData().getFirstName(),
                        currentProfileResult.getData().getMiddleName(),
                        currentProfileResult.getData().getSecondName())
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(" ")));

                String profileAvatarUrl = String.format("%s/api/profile/%s/avatar", Client.getBaseUrl(),
                        currentProfileResult.getData().getUsername());
                GlideUrl glideUrl = new GlideUrl(profileAvatarUrl,
                        new LazyHeaders.Builder()
                                .addHeader("Authorization", "Bearer " + Client.getBarrierToken())
                                .build());
                Glide.with(getActivity()).load(glideUrl)
                        .signature(GlideUtils.getMediaStoreSignature(currentProfileResult.getData().getAvatar()))
                        .apply(RequestOptions.circleCropTransform()).into(avatarImageView);

            }
        });
        profileFragmentViewModel.getCurrentProfile();

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