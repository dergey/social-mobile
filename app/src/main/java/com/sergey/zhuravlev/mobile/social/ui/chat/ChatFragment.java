package com.sergey.zhuravlev.mobile.social.ui.chat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.LoadState;
import androidx.paging.RemoteMediator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private RecyclerView recyclerView;
    private ConstraintLayout errorLayout;
    private TextView noChatTextView;
    private Button tryAgainButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chatViewModel = new ViewModelProvider(this, new ChatViewModelFactory(this.getActivity()))
                .get(ChatViewModel.class);
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        noChatTextView = binding.noChatTextView;
        errorLayout = binding.errorLayout;
        tryAgainButton = binding.tryAgainButton;
        errorLayout.setVisibility(View.INVISIBLE);

        adapter = new ChatAdapter(getActivity());
        adapter.addLoadStateListener(combinedLoadStates -> {
            noChatTextView.setVisibility(combinedLoadStates.getRefresh() instanceof LoadState.Loading
                    && adapter.getItemCount() == 0 ?
                    View.VISIBLE : View.GONE);
            if (combinedLoadStates.getRefresh() instanceof LoadState.Error) {
                ErrorCode errorCode = Client.exceptionHandling(((LoadState.Error) combinedLoadStates.getRefresh()).getError());
                if (Objects.equals(errorCode, ErrorCode.UNAUTHORIZED)) {
                    activityCallback.onFragmentEvent(ActivityCodes.TOKEN_EXPIRED_CODE);
                } else {
                    errorLayout.setVisibility(View.VISIBLE);
                    TranslateAnimation animate = new TranslateAnimation(
                            0,
                            0,
                            0,
                            errorLayout.getHeight());
                    animate.setDuration(200);
                    animate.setFillAfter(true);
                    errorLayout.startAnimation(animate);
                }
            }
            return null;
        });
        tryAgainButton.setOnClickListener(event -> {
            TranslateAnimation animate = new TranslateAnimation(
                    0,
                    0,
                    errorLayout.getHeight(),
                    0);
            animate.setDuration(200);
            animate.setFillAfter(true);
            errorLayout.startAnimation(animate);
            errorLayout.setVisibility(View.GONE);
            adapter.retry();
        });

        recyclerView = binding.chatPreview;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        chatViewModel.fetchChatPreviewModelLiveData().observe(getActivity(), pagingData ->
                adapter.submitData(getLifecycle(), pagingData)
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