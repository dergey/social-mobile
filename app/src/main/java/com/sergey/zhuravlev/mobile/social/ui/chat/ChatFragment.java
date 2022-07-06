package com.sergey.zhuravlev.mobile.social.ui.chat;

import android.content.Context;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.LoadState;
import androidx.paging.RemoteMediator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sergey.zhuravlev.mobile.social.R;
import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.constrain.ActivityCodes;
import com.sergey.zhuravlev.mobile.social.databinding.FragmentChatsBinding;
import com.sergey.zhuravlev.mobile.social.enums.ErrorCode;
import com.sergey.zhuravlev.mobile.social.util.FragmentCallable;

import java.util.Objects;

@ExperimentalPagingApi
public class ChatFragment extends Fragment {

    private FragmentCallable activityCallback;

    private FragmentChatsBinding binding;
    private ChatViewModel chatViewModel;
    private ChatAdapter adapter;

    private ConstraintLayout constraintLayout;
    private RecyclerView recyclerView;
    private ConstraintLayout statusLayout;
    private TextView statusTextView;
    private ImageView statusImageView;
    private TextView noChatTextView;
    private Button statusActionButton;

    private Boolean isShowing = false;
    private final ConstraintSet constraintSetDefault = new ConstraintSet();
    private final ConstraintSet constraintSetShowing = new ConstraintSet();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chatViewModel = new ViewModelProvider(this, new ChatViewModelFactory(this.getActivity()))
                .get(ChatViewModel.class);
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        constraintLayout = binding.constraintLayout;
        noChatTextView = binding.noChatTextView;
        statusLayout = binding.statusLayout;
        statusTextView = binding.statusTextView;
        statusImageView = binding.statusImageView;
        statusActionButton = binding.statusActionButton;

        // Animation declaration:
        constraintSetDefault.clone(constraintLayout);
        constraintSetShowing.clone(ChatFragment.this.getContext(), R.layout.fragment_chats_alt);

        // Adapter initialize:
        adapter = new ChatAdapter(getActivity());
        adapter.addLoadStateListener(combinedLoadStates -> {
//            noChatTextView.setVisibility(combinedLoadStates.getRefresh() instanceof LoadState.Loading
//                    && adapter.getItemCount() == 0 ?
//                    View.VISIBLE : View.GONE);

            if (combinedLoadStates.getRefresh() instanceof LoadState.Loading) {
                statusTextView.setText(R.string.chat_status_loading);
                statusImageView.setImageResource(R.drawable.ic_round_cloud_queue_24);
                statusActionButton.setVisibility(View.INVISIBLE);
                if (!isShowing) {
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    constraintSetShowing.applyTo(constraintLayout);
                    isShowing = true;
                }
            } else if (combinedLoadStates.getRefresh() instanceof LoadState.NotLoading) {
                statusImageView.setImageResource(R.drawable.ic_round_cloud_done_24);
                statusTextView.setText(R.string.chat_status_not_loading);
                statusActionButton.setVisibility(View.INVISIBLE);
                if (isShowing) {
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    constraintSetDefault.applyTo(constraintLayout);
                    isShowing = false;
                }
            } else if (combinedLoadStates.getRefresh() instanceof LoadState.Error) {
                statusImageView.setImageResource(R.drawable.ic_round_cloud_off_24);
                statusTextView.setText(R.string.chat_status_error);
                statusActionButton.setVisibility(View.VISIBLE);
                if (!isShowing) {
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    constraintSetShowing.applyTo(constraintLayout);
                    isShowing = true;
                }
            }

            if (combinedLoadStates.getRefresh() instanceof LoadState.Error) {
                Throwable t = ((LoadState.Error) combinedLoadStates.getRefresh()).getError();
                Log.w("ChatAdapter/onRefresh", "Throw error: ", t);
                ErrorCode errorCode = Client.exceptionHandling(t);
                if (Objects.equals(errorCode, ErrorCode.UNAUTHORIZED)) {
                    activityCallback.onFragmentEvent(ActivityCodes.TOKEN_EXPIRED_CODE);
                }
            }
            return null;
        });

        statusActionButton.setOnClickListener(v -> {
            adapter.retry();
        });

        recyclerView = binding.chatPreview;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        chatViewModel.fetchChatPreviewModelLiveData().observe(getActivity(), pagingData ->
                adapter.submitData(this.getLifecycle(), pagingData)
        );

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