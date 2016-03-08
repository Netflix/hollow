package com.netflix.vms.transformer.hollowoutput;


public class VideoAwardFestival implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;

    public VideoAwardFestival() { }

    public VideoAwardFestival(int value) {
        this.id = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoAwardFestival))
            return false;

        VideoAwardFestival o = (VideoAwardFestival) other;
        if(o.id != id) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + id;
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoAwardFestival{");
        builder.append("id=").append(id);
        builder.append("}");
        return builder.toString();
    }

    public VideoAwardFestival clone() {
        try {
            VideoAwardFestival clone = (VideoAwardFestival)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}