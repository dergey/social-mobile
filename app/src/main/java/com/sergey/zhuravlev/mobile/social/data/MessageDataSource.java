package com.sergey.zhuravlev.mobile.social.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.api.MessageEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.CreateStickerMessageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.CreateTextMessageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.client.mapper.MessageModelMapper;
import com.sergey.zhuravlev.mobile.social.database.AppDatabase;
import com.sergey.zhuravlev.mobile.social.database.dao.ChatModelDao;
import com.sergey.zhuravlev.mobile.social.database.dao.MessageModelDao;
import com.sergey.zhuravlev.mobile.social.database.model.ChatModel;
import com.sergey.zhuravlev.mobile.social.database.model.MessageModel;
import com.sergey.zhuravlev.mobile.social.enums.MessageSenderType;
import com.sergey.zhuravlev.mobile.social.enums.MessageType;
import com.sergey.zhuravlev.mobile.social.util.GlideCompressedImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.HttpException;

@SuppressWarnings("UnstableApiUsage")
public class MessageDataSource {

    private final MessageEndpoints messageEndpoints;
    private final AppDatabase database;
    private final MessageModelDao messageModelDao;
    private final ChatModelDao chatModelDao;
    private final Executor executor;
    private final Context context;

    public MessageDataSource(MessageEndpoints messageEndpoints, AppDatabase database, Executor executor, Context context) {
        this.messageEndpoints = messageEndpoints;
        this.database = database;
        this.messageModelDao = database.getMessageModelDao();
        this.chatModelDao = database.getChatModelDao();
        this.executor = executor;
        this.context = context;
    }

    public ListenableFuture<Result<MessageDto, ErrorDto>> createTextMessage(Long chatId, String text,
                                                                            FutureCallback<MessageModel> partialCallback) {

        final AtomicLong modelIdAtomicLong = new AtomicLong();

        MessageModel prepareModel = new MessageModel();
        prepareModel.setSender(MessageSenderType.SOURCE);
        prepareModel.setChatId(chatId);
        prepareModel.setType(MessageType.TEXT);
        prepareModel.setSender(MessageSenderType.SOURCE);
        prepareModel.setText(text);
        prepareModel.setRead(true);
        prepareModel.setPrepend(true);
        prepareModel.setCreateAt(LocalDateTime.now());
        prepareModel.setUpdateAt(LocalDateTime.now());

        ListenableFuture<MessageModel> prepareModelFuture = Futures.immediateFuture(prepareModel);

        ListenableFuture<MessageModel> databaseFuture = Futures.transform(prepareModelFuture,
                model -> {
                    database.runInTransaction(() -> {
                        long id = messageModelDao.insert(model);
                        ChatModel chatModel = chatModelDao.getOneById(model.getChatId());
                        chatModel.setLastMessageId(id);
                        chatModelDao.insert(chatModel);
                        modelIdAtomicLong.set(id);
                    });
                    model.setId(modelIdAtomicLong.get());
                    partialCallback.onSuccess(prepareModel);
                    return model;
                },
                executor);

        ListenableFuture<MessageDto> networkFuture = Futures.transformAsync(databaseFuture,
                model -> messageEndpoints.createMessage(model.getChatId(), new CreateTextMessageDto(model.getText())),
                executor);

        ListenableFuture<Result<MessageDto, ErrorDto>> networkResultFuture =
                Futures.transform(networkFuture, Result.Success::new, executor);

        ListenableFuture<Result<MessageDto, ErrorDto>> partialResultFuture =
                Futures.catching(networkResultFuture, HttpException.class,
                        Result.Error::fromHttpException, executor);

        ListenableFuture<Result<MessageDto, ErrorDto>> catchingResultFuture = Futures.catching(partialResultFuture,
                IOException.class, Result.Error::fromIOException, executor);

        return Futures.transform(catchingResultFuture,
                result -> {
                    if (result.isSuccess()) {
                        Result.Success<MessageDto, ErrorDto> successResult = (Result.Success<MessageDto, ErrorDto>) result;
                        database.runInTransaction(() -> {
                            MessageModel prependModel = messageModelDao.getOneById(modelIdAtomicLong.get());
                            messageModelDao.insert(MessageModelMapper.updateModel(prependModel, successResult.getData()));
                        });
                    } else {
                        Result.Error<MessageDto, ErrorDto> errorResult = (Result.Error<MessageDto, ErrorDto>) result;
                        Log.w("MessageDataSource/createTextMessage", "Backend return error result: " + errorResult.getMessage());
                        database.runInTransaction(() -> {
                            MessageModel prependModel = messageModelDao.getOneById(modelIdAtomicLong.get());
                            prependModel.setPrependError(true);
                            messageModelDao.insert(prependModel);
                            Log.w("MessageDataSource/createTextMessage", "Insert model with error: " + prependModel);
                        });
                    }
                    return result;
                },
                executor);
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


    public ListenableFuture<Result<MessageDto, ErrorDto>> createImageMessage(Long chatId, final Uri filePath,
                                                                             FutureCallback<MessageModel> partialCallback) {

        final AtomicLong modelIdAtomicLong = new AtomicLong();

        MessageModel prepareModel = new MessageModel();
        prepareModel.setSender(MessageSenderType.SOURCE);
        prepareModel.setChatId(chatId);
        prepareModel.setType(MessageType.IMAGE);
        prepareModel.setSender(MessageSenderType.SOURCE);
        prepareModel.setRead(true);
        prepareModel.setPrepend(true);
        prepareModel.setCreateAt(LocalDateTime.now());
        prepareModel.setUpdateAt(LocalDateTime.now());

        ListenableFuture<GlideCompressedImage> glideCompressedFuture = Futures.submit(() -> {
            GlideCompressedImage glideCompressedImage = new GlideCompressedImage();
            glideCompressedImage.setGlideSignature(filePath.toString());
            glideCompressedImage.setFilename(filePath.getPath().substring(filePath.getPath().lastIndexOf('/') + 1));

            Bitmap bitmap = Glide.with(context)
                    .asBitmap()
                    .load(filePath)
                    .apply(new RequestOptions()
                            .override(1280)
                            .downsample(DownsampleStrategy.AT_MOST)
                            .signature(new ObjectKey(glideCompressedImage.getGlideSignature())))
                    .submit().get();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
            byte[] byteArray = out.toByteArray();
            out.close();
            glideCompressedImage.setBytearray(byteArray);
            return glideCompressedImage;
        }, executor);

        glideCompressedFuture = Futures.transform(glideCompressedFuture,
                glideCompressedImage -> {
                    prepareModel.setGlideSignature(glideCompressedImage.getGlideSignature());
                    database.runInTransaction(() -> {
                        long id = messageModelDao.insert(prepareModel);
                        ChatModel chatModel = chatModelDao.getOneById(prepareModel.getChatId());
                        chatModel.setLastMessageId(id);
                        chatModelDao.insert(chatModel);
                        modelIdAtomicLong.set(id);
                    });
                    prepareModel.setId(modelIdAtomicLong.get());
                    partialCallback.onSuccess(prepareModel);
                    return glideCompressedImage;
                },
                executor);

        ListenableFuture<MultipartBody.Part> multipartBodyPartFuture = Futures.transform(glideCompressedFuture,
                glideCompressedImage -> {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),
                            glideCompressedImage.getBytearray());
                    return MultipartBody.Part.createFormData("image", glideCompressedImage.getFilename(),
                            requestFile);
                }, executor);

        ListenableFuture<MessageDto> messageFuture = Futures.transformAsync(multipartBodyPartFuture,
                input -> messageEndpoints.createImageMessage(chatId, input),
                executor);

        ListenableFuture<Result<MessageDto, ErrorDto>> messageResultFuture =
                Futures.transform(messageFuture,
                        Result.Success::new, executor);

        ListenableFuture<Result<MessageDto, ErrorDto>> partialResultFuture =
                Futures.catching(messageResultFuture, HttpException.class,
                        Result.Error::fromHttpException, executor);

        ListenableFuture<Result<MessageDto, ErrorDto>> catchingResultFuture =
                Futures.catching(partialResultFuture,
                        IOException.class, Result.Error::fromIOException, executor);

        return Futures.transform(catchingResultFuture,
                result -> {
                    if (result.isSuccess()) {
                        Result.Success<MessageDto, ErrorDto> successResult = (Result.Success<MessageDto, ErrorDto>) result;
                        database.runInTransaction(() -> {
                            MessageModel prependModel = messageModelDao.getOneById(modelIdAtomicLong.get());
                            messageModelDao.insert(MessageModelMapper.updateModel(prependModel, successResult.getData()));
                        });
                        //todo add deletion from the Glide cache
                    } else {
                        Result.Error<MessageDto, ErrorDto> errorResult = (Result.Error<MessageDto, ErrorDto>) result;
                        Log.w("MessageDataSource/createImageMessage", "Backend return error result: " + errorResult.getMessage());
                        database.runInTransaction(() -> {
                            MessageModel prependModel = messageModelDao.getOneById(modelIdAtomicLong.get());
                            prependModel.setPrependError(true);
                            messageModelDao.insert(prependModel);
                            Log.w("MessageDataSource/createImageMessage", "Insert model with error: " + prependModel);
                        });
                    }
                    return result;
                },
                executor);
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