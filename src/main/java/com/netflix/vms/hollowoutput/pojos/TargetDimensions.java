package com.netflix.vms.hollowoutput.pojos;


public class TargetDimensions {

    public int heightInPixels;
    public int widthInPixels;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof TargetDimensions))
            return false;

        TargetDimensions o = (TargetDimensions) other;
        if(o.heightInPixels != heightInPixels) return false;
        if(o.widthInPixels != widthInPixels) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}