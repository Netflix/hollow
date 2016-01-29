package com.netflix.vms.hollowoutput.pojos;


public class ImageSubtitleIndexByteRange {

    public long masterIndexOffset;
    public int masterIndexSize;

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