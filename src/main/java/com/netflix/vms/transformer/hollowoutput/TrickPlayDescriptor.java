package com.netflix.vms.transformer.hollowoutput;


public class TrickPlayDescriptor {

    public int width;
    public int height;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof TrickPlayDescriptor))
            return false;

        TrickPlayDescriptor o = (TrickPlayDescriptor) other;
        if(o.width != width) return false;
        if(o.height != height) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}