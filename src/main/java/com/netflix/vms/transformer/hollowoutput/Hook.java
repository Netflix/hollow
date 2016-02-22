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

    public Hook clone() {
        try {
            return (Hook)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}