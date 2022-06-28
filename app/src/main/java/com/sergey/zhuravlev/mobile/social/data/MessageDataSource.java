package com.sergey.zhuravlev.mobile.social.data;

import android.content.Context;
import android.net.Uri;

import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.sergey.zhuravlev.mobile.social.client.api.MessageEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.CreateStickerMessageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.CreateTextMessageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.MessageDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.HttpException;

@SuppressWarnings("UnstableApiUsage")
public class MessageDataSource {

    private final MessageEndpoints messageEndpoints;
    private final Executor executor;
    private final Context context;

    public MessageDataSource(MessageEndpoints messageEndpoints, Executor executor, Context context) {
        this.messageEndpoints = messageEndpoints;
        this.executor = executor;
        this.context = context;
    }

    public ListenableFuture<Result<MessageDto, ErrorDto>> createTextMessage(Long chatId, String text) {
        CreateTextMessageDto request = new CreateTextMessageDto(text);

        ListenableFuture<Result<MessageDto, ErrorDto>> loginFuture =
                Futures.transform(messageEndpoints.createMessage(chatId, request),
                        Result.Success::new, executor);

        ListenableFuture<Result<MessageDto, ErrorDto>> partialResultFuture =
                Futures.catching(loginFuture, HttpException.class,
                        Result.Error::fromHttpException, executor);

        return Futures.catching(partialResultFuture,
                IOException.class, Result.Error::fromIOException, executor);
    }

    public ListenableFuture<Result<MessageDto, ErrorDto>> createStickerMessage(Long chatId, Long stickerId) {
        CreateStickerMessageDto request = new CreateStickerMessageDto(stickerId);

        ListenableFuture<Result<MessageDto, ErrorDto>> loginFuture =
                Futures.transform(messageEndpoints.createMessage(chatId, request),
                        Result.Success::new, executor);

        ListenableFuture<Result<MessageDto, ErrorDto>> partialResultFuture =
                Futures.catching(loginFuture, HttpException.class,
                        Result.Error::fromHttpException, executor);

        return Futures.catching(partialResultFuture,
                IOException.class, Result.Error::fromIOException, executor);
    }


    public ListenableFuture<Result<MessageDto, ErrorDto>> createImageMessage(Long chatId, final Uri filePath) {
        ListenableFuture<MultipartBody.Part> multipartBodyPartFuture = ListenableFutureTask.create(() -> {
            InputStream is = context.getContentResolver().openInputStream(filePath);
            byte[] imageBytes = ByteStreams.toByteArray(is);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageBytes);
            return MultipartBody.Part.createFormData("image",
                    filePath.getPath().substring(filePath.getPath().lastIndexOf('/') + 1),
                    requestFile);
        });

        ListenableFuture<MessageDto> messageFuture = Futures.transformAsync(multipartBodyPartFuture,
                input -> messageEndpoints.createImageMessage(chatId, input),
                executor);

        ListenableFuture<Result<MessageDto, ErrorDto>> loginFuture =
                Futures.transform(messageFuture,
                        Result.Success::new, executor);

        ListenableFuture<Result<MessageDto, ErrorDto>> partialResultFuture =
                Futures.catching(loginFuture, HttpException.class,
                        Result.Error::fromHttpException, executor);

        return Futures.catching(partialResultFuture,
                IOException.class, Result.Error::fromIOException, executor);
    }

    public ListenableFuture<Result<Void, ErrorDto>> deleteMessage(Long chatId, Long messageId) {
        ListenableFuture<Result<Void, ErrorDto>> loginFuture =
                Futures.transform(messageEndpoints.deleteMessage(chatId, messageId),
                        Result.Success::new, executor);

        ListenableFuture<Result<Void, ErrorDto>> partialResultFuture =
                Futures.catching(loginFuture, HttpException.class,
                        Result.Error::fromHttpException, executor);

        return Futures.catching(partialResultFuture,
                IOException.class, Result.Error::fromIOException, executor);
    }

}