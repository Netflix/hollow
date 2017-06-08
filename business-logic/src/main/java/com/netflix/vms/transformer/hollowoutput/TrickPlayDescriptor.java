package com.netflix.vms.transformer.hollowoutput;


public class TrickPlayDescriptor implements Cloneable {

    public int width = java.lang.Integer.MIN_VALUE;
    public int height = java.lang.Integer.MIN_VALUE;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof TrickPlayDescriptor))
            return false;

        TrickPlayDescriptor o = (TrickPlayDescriptor) other;
        if(o.width != width) return false;
        if(o.height != height) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + width;
        hashCode = hashCode * 31 + height;
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("TrickPlayDescriptor{");
        builder.append("width=").append(width);
        builder.append(",height=").append(height);
        builder.append("}");
        return builder.toString();
    }

    public TrickPlayDescriptor clone() {
        try {
            TrickPlayDescriptor clone = (TrickPlayDescriptor)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
