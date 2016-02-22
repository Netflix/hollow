package com.netflix.vms.transformer.hollowoutput;


public class VideoAwardType implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;
    public VideoAwardFestival festival = null;

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

    public VideoAwardType clone() {
        try {
            return (VideoAwardType)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}