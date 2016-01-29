package com.netflix.vms.hollowoutput.pojos;


public class VideoResolution {

    public int height;
    public int width;

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