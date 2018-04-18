package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="VideoStreamCropParams")
public class VideoStreamCropParams implements Cloneable {

    public int x = java.lang.Integer.MIN_VALUE;
    public int y = java.lang.Integer.MIN_VALUE;
    public int width = java.lang.Integer.MIN_VALUE;
    public int height = java.lang.Integer.MIN_VALUE;

    public VideoStreamCropParams setX(int x) {
        this.x = x;
        return this;
    }
    public VideoStreamCropParams setY(int y) {
        this.y = y;
        return this;
    }
    public VideoStreamCropParams setWidth(int width) {
        this.width = width;
        return this;
    }
    public VideoStreamCropParams setHeight(int height) {
        this.height = height;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoStreamCropParams))
            return false;

        VideoStreamCropParams o = (VideoStreamCropParams) other;
        if(o.x != x) return false;
        if(o.y != y) return false;
        if(o.width != width) return false;
        if(o.height != height) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + x;
        hashCode = hashCode * 31 + y;
        hashCode = hashCode * 31 + width;
        hashCode = hashCode * 31 + height;
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoStreamCropParams{");
        builder.append("x=").append(x);
        builder.append(",y=").append(y);
        builder.append(",width=").append(width);
        builder.append(",height=").append(height);
        builder.append("}");
        return builder.toString();
    }

    public VideoStreamCropParams clone() {
        try {
            VideoStreamCropParams clone = (VideoStreamCropParams)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}