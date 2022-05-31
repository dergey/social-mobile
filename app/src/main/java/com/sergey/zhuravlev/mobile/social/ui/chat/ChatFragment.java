package com.sergey.zhuravlev.mobile.social.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sergey.zhuravlev.mobile.social.databinding.FragmentChatsBinding;

public class ChatFragment extends Fragment {

    private FragmentChatsBinding binding;
    private RecyclerView recyclerView;
    private ChatViewModel chatViewModel;
    private ChatAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adapter = new ChatAdapter(getActivity());

        recyclerView = binding.chatPreview;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        chatViewModel.fetchChatPreviewsLiveData().observe(getActivity(), pagingData ->
                adapter.submitData(getLifecycle(), pagingData)
        );

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}