package com.sergey.zhuravlev.mobile.social.dto.image;

public class ImageDto {

    private String mimeType;

    private Integer height;

    private Integer width;

    private String dataSize;

    public ImageDto() {
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String getDataSize() {
        return dataSize;
    }

    public void setDataSize(String dataSize) {
        this.dataSize = dataSize;
    }

}
