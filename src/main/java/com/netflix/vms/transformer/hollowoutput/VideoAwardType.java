package com.netflix.vms.transformer.hollowoutput;


public class VideoAwardType {

    public int id;
    public VideoAwardFestival festival;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoAwardType))
            return false;

        VideoAwardType o = (VideoAwardType) other;
        if(o.id != id) return false;
        if(o.festival == null) {
            if(festival != null) return false;
        } else if(!o.festival.equals(festival)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}