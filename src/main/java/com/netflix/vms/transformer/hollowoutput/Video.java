package com.netflix.vms.transformer.hollowoutput;


public class Video implements Cloneable {

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

    public Video clone() {
        try {
            Video clone = (Video)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}