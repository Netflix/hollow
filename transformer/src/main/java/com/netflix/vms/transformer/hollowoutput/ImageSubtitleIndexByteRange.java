package com.netflix.vms.transformer.hollowoutput;


public class ImageSubtitleIndexByteRange implements Cloneable {

    public long masterIndexOffset = java.lang.Long.MIN_VALUE;
    public int masterIndexSize = java.lang.Integer.MIN_VALUE;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ImageSubtitleIndexByteRange))
            return false;

        ImageSubtitleIndexByteRange o = (ImageSubtitleIndexByteRange) other;
        if(o.masterIndexOffset != masterIndexOffset) return false;
        if(o.masterIndexSize != masterIndexSize) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (masterIndexOffset ^ (masterIndexOffset >>> 32));
        hashCode = hashCode * 31 + masterIndexSize;
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ImageSubtitleIndexByteRange{");
        builder.append("masterIndexOffset=").append(masterIndexOffset);
        builder.append(",masterIndexSize=").append(masterIndexSize);
        builder.append("}");
        return builder.toString();
    }

    public ImageSubtitleIndexByteRange clone() {
        try {
            ImageSubtitleIndexByteRange clone = (ImageSubtitleIndexByteRange)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}