package com.sergey.zhuravlev.mobile.social.ui.message;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.PagingData;
import androidx.paging.PagingDataTransforms;
import androidx.paging.PagingLiveData;

import com.sergey.zhuravlev.mobile.social.client.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.data.repository.ChatRepository;
import com.sergey.zhuravlev.mobile.social.data.repository.MessageRepository;
import com.sergey.zhuravlev.mobile.social.database.model.MessageModel;
import com.sergey.zhuravlev.mobile.social.ui.common.LiveDataFutureCallback;
import com.sergey.zhuravlev.mobile.social.ui.common.NetworkLiveDataFutureCallback;
import com.sergey.zhuravlev.mobile.social.ui.common.UiNetworkResult;
import com.sergey.zhuravlev.mobile.social.ui.common.Item;

import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import kotlinx.coroutines.CoroutineScope;

@ExperimentalPagingApi
public class MessageViewModel extends ViewModel {

    private final MutableLiveData<UiNetworkResult<MessageDto>> createMessageResult = new MutableLiveData<>();
    private final MutableLiveData<MessageModel> databaseCreateMessage = new MutableLiveData<>();
    private final MutableLiveData<UiNetworkResult<Void>> deleteMessageResult = new MutableLiveData<>();
    private final MutableLiveData<UiNetworkResult<Void>> updateReadStatusResult = new MutableLiveData<>();

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final Executor executor;

    MessageViewModel(Context context) {
        this.messageRepository = MessageRepository.getInstance(context);
        this.chatRepository = ChatRepository.getInstance(context);
        this.executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<UiNetworkResult<MessageDto>> getCreateMessageResult() {
        return createMessageResult;
    }

    public LiveData<MessageModel> getDatabaseCreateMessage() {
        return databaseCreateMessage;
    }

    public LiveData<UiNetworkResult<Void>> getDeleteMessageResult() {
        return deleteMessageResult;
    }

    public LiveData<UiNetworkResult<Void>> getUpdateReadStatusResult() {
        return updateReadStatusResult;
    }

    public LiveData<PagingData<Item<MessageModel>>> fetchChatMessageModelLiveData(Long chatId) {
        LiveData<PagingData<MessageModel>> sourceData = messageRepository.letChatMessageModelLiveData(chatId);
        LiveData<PagingData<Item<MessageModel>>> liveData = Transformations.map(
                sourceData,
                pagingData -> {
                    PagingData<Item<MessageModel>> newPagingData = PagingDataTransforms.map(pagingData, executor, Item.RepoItem::new);
                    newPagingData = PagingDataTransforms.insertSeparators(newPagingData, executor, (before, after) -> {
                        if (before == null) {
                            return null;
                        }

                        LocalDate beforeCreateAtDate = ((Item.RepoItem<MessageModel>) before).getModel().getCreateAt().toLocalDate();
                        if (after == null) {
                            return new Item.DateSeparatorItem<>(beforeCreateAtDate);
                        }

                        LocalDate afterCreateAtDate = ((Item.RepoItem<MessageModel>) after).getModel().getCreateAt().toLocalDate();
                        if (Objects.equals(afterCreateAtDate, beforeCreateAtDate)) {
                            return null;
                        } else {
                            return new Item.DateSeparatorItem<>(beforeCreateAtDate);
                        }
                    });
                    return newPagingData;
                }
        );
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        return PagingLiveData.cachedIn(liveData, viewModelScope);
    }

    public void createTextMessage(Long chatId, String text) {
        messageRepository.createTextMessage(chatId, text,
                new LiveDataFutureCallback<>(databaseCreateMessage),
                new NetworkLiveDataFutureCallback<>(createMessageResult));
    }

    public void createImageMessage(Long chatId, final Uri filePath) {
        messageRepository.createImageMessage(chatId, filePath,
                new LiveDataFutureCallback<>(databaseCreateMessage),
                new NetworkLiveDataFutureCallback<>(createMessageResult));
    }

    public void createStickerMessage(Long chatId, Long stickerId) {
        messageRepository.createStickerMessage(chatId, stickerId, new NetworkLiveDataFutureCallback<>(createMessageResult));
    }

    public void deleteMessage(Long chatId, Long messageId) {
        messageRepository.deleteMessage(chatId, messageId, new NetworkLiveDataFutureCallback<>(deleteMessageResult));
    }

    public void updateReadStatus(Long chatId) {
        chatRepository.updateReadStatus(chatId, new NetworkLiveDataFutureCallback<>(updateReadStatusResult));
    }

}
