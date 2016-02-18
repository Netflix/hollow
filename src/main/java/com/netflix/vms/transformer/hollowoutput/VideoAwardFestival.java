package com.netflix.vms.transformer.hollowoutput;


public class VideoAwardFestival {

    public int id;

    public VideoAwardFestival() { }

    public VideoAwardFestival(int value) {
        this.id = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoAwardFestival))
            return false;

        VideoAwardFestival o = (VideoAwardFestival) other;
        if(o.id != id) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}