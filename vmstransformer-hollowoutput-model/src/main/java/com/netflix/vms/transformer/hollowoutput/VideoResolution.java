package com.netflix.vms.transformer.hollowoutput;


public class VideoResolution implements Cloneable {

    public int height = java.lang.Integer.MIN_VALUE;
    public int width = java.lang.Integer.MIN_VALUE;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoResolution))
            return false;

        VideoResolution o = (VideoResolution) other;
        if(o.height != height) return false;
        if(o.width != width) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + height;
        hashCode = hashCode * 31 + width;
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoResolution{");
        builder.append("height=").append(height);
        builder.append(",width=").append(width);
        builder.append("}");
        return builder.toString();
    }

    public VideoResolution clone() {
        try {
            VideoResolution clone = (VideoResolution)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
