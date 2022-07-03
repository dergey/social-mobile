package com.sergey.zhuravlev.mobile.social.util;

public class GlideCompressedImage {

    private String glideSignature;
    private String filename;
    private byte[] bytearray;

    public String getGlideSignature() {
        return glideSignature;
    }

    public void setGlideSignature(String glideSignature) {
        this.glideSignature = glideSignature;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getBytearray() {
        return bytearray;
    }

    public void setBytearray(byte[] bytearray) {
        this.bytearray = bytearray;
    }
}
