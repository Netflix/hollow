package com.netflix.vms.hollowoutput.pojos;


public class PixelAspect {

    public int height;
    public int width;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PixelAspect))
            return false;

        PixelAspect o = (PixelAspect) other;
        if(o.height != height) return false;
        if(o.width != width) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}