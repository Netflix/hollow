package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="ImageStreamInfo")
public class ImageStreamInfo implements Cloneable {

    public int imageCount = java.lang.Integer.MIN_VALUE;
    public String imageFormat = null;
    public long offsetMillis = java.lang.Long.MIN_VALUE;

    public ImageStreamInfo setImageCount(int imageCount) {
        this.imageCount = imageCount;
        return this;
    }
    public ImageStreamInfo setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
        return this;
    }
    public ImageStreamInfo setOffsetMillis(long offsetMillis) {
        this.offsetMillis = offsetMillis;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ImageStreamInfo))
            return false;

        ImageStreamInfo o = (ImageStreamInfo) other;
        if(o.imageCount != imageCount) return false;
        if(o.imageFormat == null) {
            if(imageFormat != null) return false;
        } else if(!o.imageFormat.equals(imageFormat)) return false;
        if(o.offsetMillis != offsetMillis) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + imageCount;
        hashCode = hashCode * 31 + (imageFormat == null ? 1237 : imageFormat.hashCode());
        hashCode = hashCode * 31 + (int) (offsetMillis ^ (offsetMillis >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ImageStreamInfo{");
        builder.append("imageCount=").append(imageCount);
        builder.append(",imageFormat=").append(imageFormat);
        builder.append(",offsetMillis=").append(offsetMillis);
        builder.append("}");
        return builder.toString();
    }

    public ImageStreamInfo clone() {
        try {
            ImageStreamInfo clone = (ImageStreamInfo)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}