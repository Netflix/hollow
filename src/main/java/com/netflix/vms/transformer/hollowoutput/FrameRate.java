package com.netflix.vms.transformer.hollowoutput;


public class FrameRate implements Cloneable {

    public float val = java.lang.Float.NaN;

    public FrameRate() { }

    public FrameRate(float value) {
        this.val = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof FrameRate))
            return false;

        FrameRate o = (FrameRate) other;
        if(o.val != val) return false;
        return true;
    }

    public FrameRate clone() {
        try {
            FrameRate clone = (FrameRate)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}