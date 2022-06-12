package com.sergey.zhuravlev.mobile.social.ui.profile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sergey.zhuravlev.mobile.social.constrain.IntentConstrains;
import com.sergey.zhuravlev.mobile.social.databinding.ActivityProfileBinding;
import com.sergey.zhuravlev.mobile.social.ui.common.ProfilesHorizontalListAdapter;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private ProfileViewModel viewModel;
    private RecyclerView recyclerView;
    private ProfilesHorizontalListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new ProfilesHorizontalListAdapter(this);

        recyclerView = binding.friendListRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        if (getIntent().getExtras() != null) {
            String username = getIntent().getExtras().getString(IntentConstrains.EXTRA_PROFILE_USERNAME);
            viewModel.fetchProfileFriendLiveData(username).observe(this, pagingData ->
                    adapter.submitData(getLifecycle(), pagingData)
            );
        } else {
            finish();
        }
    }
}