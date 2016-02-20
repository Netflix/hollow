package com.netflix.vms.transformer.hollowoutput;


public class ImageSubtitleIndexByteRange {

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}