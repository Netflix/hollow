package com.netflix.vms.transformer.hollowoutput;


public class TargetDimensions implements Cloneable {

    public int heightInPixels = java.lang.Integer.MIN_VALUE;
    public int widthInPixels = java.lang.Integer.MIN_VALUE;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof TargetDimensions))
            return false;

        TargetDimensions o = (TargetDimensions) other;
        if(o.heightInPixels != heightInPixels) return false;
        if(o.widthInPixels != widthInPixels) return false;
        return true;
    }

    public TargetDimensions clone() {
        try {
            TargetDimensions clone = (TargetDimensions)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}