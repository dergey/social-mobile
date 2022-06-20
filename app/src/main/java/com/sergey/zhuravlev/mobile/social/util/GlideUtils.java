package com.sergey.zhuravlev.mobile.social.util;

import com.bumptech.glide.signature.MediaStoreSignature;
import com.sergey.zhuravlev.mobile.social.client.dto.image.ImageDto;

import java.time.ZoneId;

public class GlideUtils {

    public static MediaStoreSignature getMediaStoreSignature(ImageDto image) {
        return new MediaStoreSignature(image.getMimeType(),
                image.getCreateAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                0);
    }

}
