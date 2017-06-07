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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + rolloutId;
        hashCode = hashCode * 31 + (video == null ? 1237 : video.hashCode());
        hashCode = hashCode * 31 + Arrays.hashCode(type);
        hashCode = hashCode * 31 + Arrays.hashCode(name);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("RolloutInfo{");
        builder.append("rolloutId=").append(rolloutId);
        builder.append(",video=").append(video);
        builder.append(",type=").append(type);
        builder.append(",name=").append(name);
        builder.append("}");
        return builder.toString();
    }

    public RolloutInfo clone() {
        try {
            RolloutInfo clone = (RolloutInfo)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
