package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;

public class RolloutInfo implements Cloneable {

    public int rolloutId = java.lang.Integer.MIN_VALUE;
    public Video video = null;
    public char[] type = null;
    public char[] name = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof RolloutInfo))
            return false;

        RolloutInfo o = (RolloutInfo) other;
        if(o.rolloutId != rolloutId) return false;
        if(o.video == null) {
            if(video != null) return false;
        } else if(!o.video.equals(video)) return false;
        if(!Arrays.equals(o.type, type)) return false;
        if(!Arrays.equals(o.name, name)) return false;
        return true;
    }

    public RolloutInfo clone() {
        try {
            return (RolloutInfo)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}