package com.netflix.vms.transformer.hollowoutput;


public class PixelAspect implements Cloneable {

    public int height = java.lang.Integer.MIN_VALUE;
    public int width = java.lang.Integer.MIN_VALUE;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PixelAspect))
            return false;

        PixelAspect o = (PixelAspect) other;
        if(o.height != height) return false;
        if(o.width != width) return false;
        return true;
    }

    public PixelAspect clone() {
        try {
            PixelAspect clone = (PixelAspect)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}