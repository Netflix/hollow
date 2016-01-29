package com.netflix.vms.hollowoutput.pojos;

import java.util.Arrays;

public class RolloutInfo {

    public int rolloutId;
    public Video video;
    public char[] type;
    public char[] name;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}