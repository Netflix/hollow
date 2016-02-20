package com.netflix.vms.transformer.hollowoutput;


public class VideoResolution {

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}