package com.netflix.vms.hollowoutput.pojos;


public class Hook {

    public HookType type;
    public int rank;
    public Video video;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}