package com.sergey.zhuravlev.mobile.social.ui.friend;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sergey.zhuravlev.mobile.social.databinding.ActivityFriendBinding;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class FriendActivity extends AppCompatActivity {

    private ActivityFriendBinding binding;
    private FriendViewModel friendViewModel;
    private final Timer friendRequestRefreshTimer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
        binding = ActivityFriendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Friend request card initialize:
        RecyclerView friendRequestRecyclerView = binding.friendRequestRecyclerView;
        TextView requestCountTextView = binding.requestCountTextView;
        ConstraintLayout requestLayout = binding.requestLayout;
        FriendRequestAdapter friendRequestAdapter = new FriendRequestAdapter(this);
        friendRequestAdapter.setOnAcceptClickListener((v, position, item) -> {
            friendViewModel.acceptFriendRequest(item.getUsername());
            Optional.ofNullable(friendRequestAdapter.snapshot().get(position))
                    .ifPresent(i -> i.setAccepted(true));
            friendRequestAdapter.notifyItemChanged(position);
            return true;
        });
        friendViewModel.getAcceptResult().observe(this, result -> {
            if (result.isHasErrors()) {
                Log.e("FriendActivity/acceptFriendRequest", "Unable to accept this friend request!\n" + result.getErrorMessage());
                return;
            }
            friendRequestRefreshTimer.cancel();
            friendRequestRefreshTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    friendRequestAdapter.refresh();
                }
            }, 5000);
        });
        friendRequestAdapter.setOnRejectClickListener((v, position, item) -> {
            friendViewModel.declineFriendRequest(item.getUsername());
            Optional.ofNullable(friendRequestAdapter.snapshot().get(position))
                    .ifPresent(i -> i.setRejected(true));
            friendRequestAdapter.notifyItemChanged(position);
            return true;
        });
        friendViewModel.getRejectResult().observe(this, result -> {
            if (result.isHasErrors()) {
                Log.e("FriendActivity/declineFriendRequest", "Unable to reject this friend request!\n" + result.getErrorMessage());
                return;
            }
            friendRequestRefreshTimer.cancel();
            friendRequestRefreshTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    friendRequestAdapter.refresh();
                }
            }, 5000);
        });
        friendRequestAdapter.addLoadStateListener(loadState -> {
            if (loadState.getRefresh() instanceof LoadState.NotLoading) {
                if (friendRequestAdapter.getItemCount() <= 0) {
                    requestLayout.setVisibility(View.GONE);
                } else {
                    requestLayout.setVisibility(View.VISIBLE);
                }
            }
            return null;
        });

        friendRequestRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        friendRequestRecyclerView.setAdapter(friendRequestAdapter);

        RecyclerView friendRecyclerView = binding.friendRecyclerView;
        FriendAdapter friendAdapter = new FriendAdapter(this);
        friendRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        friendRecyclerView.setAdapter(friendAdapter);


        // Fetch data:
        friendViewModel.fetchCurrentUserFriendRequestLiveData().observe(this, pagingData ->
                friendRequestAdapter.submitData(getLifecycle(), pagingData)
        );
        friendViewModel.fetchCurrentUserFriendLiveData().observe(this, pagingData ->
                friendAdapter.submitData(getLifecycle(), pagingData)
        );
        friendViewModel.getCurrentFriendRequestPage().observe(this, page -> {
            if (page.getTotalElements() > 0) {
                requestCountTextView.setText(String.valueOf(page.getTotalElements()));
                requestCountTextView.setVisibility(View.VISIBLE);
            } else {
                requestCountTextView.setVisibility(View.INVISIBLE);
            }
        });
    }
}