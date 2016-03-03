package com.netflix.vms.transformer.hollowoutput;


public class Hook implements Cloneable {

    public HookType type = null;
    public int rank = java.lang.Integer.MIN_VALUE;
    public Video video = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Hook))
            return false;

        Hook o = (Hook) other;
        if(o.type == null) {
            if(type != null) return false;
        } else if(!o.type.equals(type)) return false;
        if(o.rank != rank) return false;
        if(o.video == null) {
            if(video != null) return false;
        } else if(!o.video.equals(video)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (type == null ? 1237 : type.hashCode());
        hashCode = hashCode * 31 + rank;
        hashCode = hashCode * 31 + (video == null ? 1237 : video.hashCode());
        return hashCode;
    }

    public Hook clone() {
        try {
            Hook clone = (Hook)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}