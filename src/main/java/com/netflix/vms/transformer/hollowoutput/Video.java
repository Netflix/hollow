package com.netflix.vms.transformer.hollowoutput;


public class Video {

    public int value = java.lang.Integer.MIN_VALUE;

    public Video() { }

    public Video(int value) {
        this.value = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Video))
            return false;

        Video o = (Video) other;
        if(o.value != value) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}